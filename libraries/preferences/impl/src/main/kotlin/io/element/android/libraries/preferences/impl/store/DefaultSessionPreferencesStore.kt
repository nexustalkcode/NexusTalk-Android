/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.preferences.impl.store

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import io.element.android.libraries.androidutils.file.safeDelete
import io.element.android.libraries.androidutils.hash.hash
import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

/**
 * 会话偏好存储的默认实现。
 *
 * 基于 Android DataStore Preferences，按会话（Session）持久化用户在该会话下的各项设置，
 * 包括在线状态、已读回执、输入状态、媒体压缩、首次使用引导完成状态等。
 * 每个 [SessionId] 对应一个独立的偏好文件，通过 [SessionPreferencesStore] 接口对外提供读写。
 */
class DefaultSessionPreferencesStore(
    context: Context,
    sessionId: String,
    @SessionCoroutineScope sessionCoroutineScope: CoroutineScope,
) : SessionPreferencesStore {

    companion object {
        /**
         * 根据应用上下文和会话 ID 生成该会话对应的 DataStore 偏好文件。
         *
         * 使用 [SessionId] 的哈希值前 16 位作为文件名一部分，避免直接暴露用户 ID，
         * 同时保证同一会话在不同设备/进程中文件名一致。
         *
         * @param context 应用上下文，用于获取 DataStore 文件目录
         * @param sessionId 当前会话 ID
         * @return 该会话的偏好存储文件（路径，非已创建的文件句柄）
         */
        fun storeFile(context: Context, sessionId: String): File {
            val hashedUserId = sessionId.hash().take(16)
            return context.preferencesDataStoreFile("session_${hashedUserId}_preferences")
        }
    }

    // ---------- DataStore 偏好键定义 ----------

    /** 是否分享在线状态（presence）：true 表示向服务器/其他用户广播自己的在线状态。 */
    private val sharePresenceKey = booleanPreferencesKey("sharePresence")
    /** 是否发送公开已读回执：发送后其他人可见“已读”。 */
    private val sendPublicReadReceiptsKey = booleanPreferencesKey("sendPublicReadReceipts")
    /** 是否在 UI 上显示已读回执（小勾等）。 */
    private val renderReadReceiptsKey = booleanPreferencesKey("renderReadReceipts")
    /** 是否发送“正在输入”通知到服务器。 */
    private val sendTypingNotificationsKey = booleanPreferencesKey("sendTypingNotifications")
    /** 是否在 UI 上显示他人的“正在输入”状态。 */
    private val renderTypingNotificationsKey = booleanPreferencesKey("renderTypingNotifications")
    /** 是否跳过会话验证（例如首次登录或安全流程中的验证步骤）。 */
    private val skipSessionVerification = booleanPreferencesKey("skipSessionVerification")
    /** 是否对发送的图片/媒体进行压缩（优化）。 */
    private val compressImages = booleanPreferencesKey("compressMedia")
    /** 视频压缩预设名称（如 STANDARD、FAST 等），对应 [VideoCompressionPreset]。 */
    private val compressMediaPreset = stringPreferencesKey("compressMediaPreset")
    /** 是否已完成首次使用引导（FTUE = First Time User Experience）。 */
    private val ftueCompleted = booleanPreferencesKey("ftueCompleted")

    /** 当前会话对应的 DataStore 偏好文件。用于 [clear] 等操作时删除文件。 */
    private val dataStoreFile = storeFile(context, sessionId)
    /**
     * DataStore 实例。在 [sessionCoroutineScope] 下执行 IO，并应用 [SessionPreferencesStoreMigration]
     * 以兼容旧版本中“分享在线状态”与“发送公开已读回执”的迁移逻辑。
     */
    private val store = PreferenceDataStoreFactory.create(
        scope = sessionCoroutineScope,
        migrations = listOf(
            SessionPreferencesStoreMigration(
                sharePresenceKey,
                sendPublicReadReceiptsKey,
            )
        ),
    ) { dataStoreFile }

    // ---------- 在线状态与关联设置 ----------

    /**
     * 设置是否分享在线状态。
     * 开启时同时将“发送公开已读回执”“显示已读回执”“发送/显示正在输入”设为与 [enabled] 一致，
     * 保证这些与“在线/隐私”相关的选项行为统一。
     */
    override suspend fun setSharePresence(enabled: Boolean) {
        update(sharePresenceKey, enabled)
        setSendPublicReadReceipts(enabled)
        setRenderReadReceipts(enabled)
        setSendTypingNotifications(enabled)
        setRenderTypingNotifications(enabled)
    }

    /** 获取是否已开启分享在线状态。默认 true。 */
    override fun isSharePresenceEnabled(): Flow<Boolean> {
        return get(sharePresenceKey) { true }
    }

    override suspend fun setSendPublicReadReceipts(enabled: Boolean) = update(sendPublicReadReceiptsKey, enabled)
    override fun isSendPublicReadReceiptsEnabled(): Flow<Boolean> = get(sendPublicReadReceiptsKey) { true }

    override suspend fun setRenderReadReceipts(enabled: Boolean) = update(renderReadReceiptsKey, enabled)
    override fun isRenderReadReceiptsEnabled(): Flow<Boolean> = get(renderReadReceiptsKey) { true }

    override suspend fun setSendTypingNotifications(enabled: Boolean) = update(sendTypingNotificationsKey, enabled)
    override fun isSendTypingNotificationsEnabled(): Flow<Boolean> = get(sendTypingNotificationsKey) { true }

    override suspend fun setRenderTypingNotifications(enabled: Boolean) = update(renderTypingNotificationsKey, enabled)
    override fun isRenderTypingNotificationsEnabled(): Flow<Boolean> = get(renderTypingNotificationsKey) { true }

    // ---------- 会话验证 ----------

    override suspend fun setSkipSessionVerification(skip: Boolean) = update(skipSessionVerification, skip)
    /** 是否已跳过会话验证。默认 false。 */
    override fun isSessionVerificationSkipped(): Flow<Boolean> = get(skipSessionVerification) { false }

    // ---------- 媒体压缩 ----------

    override suspend fun setOptimizeImages(compress: Boolean) = update(compressImages, compress)
    /** 是否对图片进行优化（压缩）。默认 true。 */
    override fun doesOptimizeImages(): Flow<Boolean> = get(compressImages) { true }

    override suspend fun setVideoCompressionPreset(preset: VideoCompressionPreset) = update(compressMediaPreset, preset.name)
    /**
     * 获取视频压缩预设。若存储值无法解析为 [VideoCompressionPreset] 枚举，则回退为 [VideoCompressionPreset.STANDARD]。
     */
    override fun getVideoCompressionPreset(): Flow<VideoCompressionPreset> = get(compressMediaPreset) { VideoCompressionPreset.STANDARD.name }
        .map { tryOrNull { VideoCompressionPreset.valueOf(it) } ?: VideoCompressionPreset.STANDARD }

    // ---------- 首次使用引导 ----------

    override suspend fun setFtueCompleted(completed: Boolean) = update(ftueCompleted, completed)
    /** 是否已完成首次使用引导。默认 false。 */
    override fun isFtueCompleted(): Flow<Boolean> = get(ftueCompleted) { false }

    // ---------- 清除与重置 ----------

    /**
     * 清空当前会话的所有偏好：删除对应的 DataStore 文件。
     * 调用后该会话下所有上述设置恢复为各 get 方法中的默认值（下次读取时）。
     */
    override suspend fun clear() {
        dataStoreFile.safeDelete()
    }

    /**
     * 清空当前会话偏好，但保留“已完成首次使用引导”状态。
     * 先读取 [isFtueCompleted]，删除文件后若之前为 true 则重新写入 ftueCompleted=true。
     * 适用于登出或切换账号时希望下次登录不再展示 FTUE，但其余设置重置的场景。
     */
    suspend fun clearButPreserveFtueCompletion() {
        val completed = isFtueCompleted().first()
        dataStoreFile.safeDelete()
        if (completed) {
            update(ftueCompleted, true)
        }
    }

    // ---------- 内部读写封装 ----------

    /**
     * 将指定键的值更新为 [value]，写入 DataStore。
     * @param key 偏好键（与 [T] 类型一致）
     * @param value 要写入的值
     */
    private suspend fun <T> update(key: Preferences.Key<T>, value: T) {
        store.edit { prefs -> prefs[key] = value }
    }

    /**
     * 以 Flow 形式读取指定键的当前值；若不存在则使用 [default] 的返回值。
     * @param key 偏好键
     * @param default 无存储值时的默认值提供函数
     * @return 会随 DataStore 变化而发射新值的 Flow
     */
    private fun <T> get(key: Preferences.Key<T>, default: () -> T): Flow<T> {
        return store.data.map { prefs -> prefs[key] ?: default() }
    }
}
