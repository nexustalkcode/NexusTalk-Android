/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.androidutils.hash.hash
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.preferences.api.store.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 历史可见性确认状态仓库接口
 *
 * 定义存储和检索用户对历史可见性警告确认状态的接口。
 * 用于记录用户是否已确认了解房间历史可见性的相关警告信息。
 *
 * 此接口采用响应式设计，使用 [Flow] 来追踪状态变化，
 * 确保 UI 能够实时响应用户的确认操作。
 *
 * @see DefaultHistoryVisibleAcknowledgementRepository 默认实现
 * @see HistoryVisibleStatePresenter 状态展示器，使用此接口
 */
interface HistoryVisibleAcknowledgementRepository {
    /**
     * 检查用户是否已确认历史可见性警告
     *
     * @param roomId 房间的唯一标识
     * @return [Flow] 流，包含布尔值，表示用户是否已确认
     *         - true: 用户已确认，不再显示警告
     *         - false: 用户未确认，可能会显示警告
     */
    fun hasAcknowledged(roomId: RoomId): Flow<Boolean>

    /**
     * 设置用户的确认状态
     *
     * 持久化用户对历史可见性警告的确认状态。
     * 当用户点击"我已知悉"或类似按钮时调用此方法。
     *
     * @param roomId 房间的唯一标识
     * @param value 确认状态，true 表示用户已确认，false 表示未确认
     */
    suspend fun setAcknowledged(roomId: RoomId, value: Boolean)
}

/**
 * 历史可见性确认状态仓库默认实现
 *
 * 使用 DataStore 作为持久化存储，基于会话（Session）级别创建独立的数据存储。
 * 每个用户会话有独立的数据存储空间，确保数据隔离和安全性。
 *
 * 存储策略：
 * - 使用会话ID的哈希值创建唯一的存储名称
 * - 每个房间使用其roomId作为偏好设置的键
 * - 存储布尔值表示确认状态
 *
 * @param sessionId 会话ID，用于创建独立的存储空间
 * @param preferenceDataStoreFactory 数据存储工厂，用于创建 DataStore 实例
 *
 * @see HistoryVisibleAcknowledgementRepository 仓库接口
 * @see io.element.android.libraries.preferences.api.store.PreferenceDataStoreFactory 数据存储工厂
 */
@ContributesBinding(SessionScope::class)
class DefaultHistoryVisibleAcknowledgementRepository(
    sessionId: SessionId,
    preferenceDataStoreFactory: PreferenceDataStoreFactory,
) : HistoryVisibleAcknowledgementRepository {
    /**
     * 数据存储实例
     *
     * 使用会话ID的哈希值（取前16位）创建唯一的存储名称，
     * 确保不同用户的确认状态相互隔离。
     */
    val store =
        sessionId.value.hash().take(16).let { hash ->
            preferenceDataStoreFactory.create("elementx_historyvisible_$hash")
        }

    /**
     * 检查特定房间的确认状态
     *
     * @param roomId 房间ID，用于查找对应的确认状态
     * @return [Flow] 流， emit 房间的确认状态布尔值
     */
    override fun hasAcknowledged(roomId: RoomId): Flow<Boolean> {
        return store.data.map { prefs ->
            val acknowledged = prefs[booleanPreferencesKey(roomId.value)] ?: false
            acknowledged
        }
    }

    /**
     * 设置特定房间的确认状态
     *
     * @param roomId 房间ID，用于设置对应的确认状态
     * @param value 确认状态布尔值，true表示已确认，false表示未确认
     */
    override suspend fun setAcknowledged(roomId: RoomId, value: Boolean) {
        store.edit { prefs ->
            prefs[booleanPreferencesKey(roomId.value)] = value
        }
    }
}
