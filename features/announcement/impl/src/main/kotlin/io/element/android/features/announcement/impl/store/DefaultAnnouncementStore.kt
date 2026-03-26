/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.store

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.announcement.api.Announcement
import io.element.android.libraries.preferences.api.store.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** 空间公告的偏好设置键名 */
private val spaceAnnouncementKey = intPreferencesKey("spaceAnnouncement")
/** 新通知声音公告的偏好设置键名 */
private val newNotificationSoundKey = intPreferencesKey("newNotificationSound")

/**
 * 默认公告存储实现类
 *
 * 使用 DataStore 实现公告状态的持久化存储。
 * 每个公告类型对应一个偏好设置项，使用整数值存储状态枚举的序号。
 * 该实现将状态存储在名为 "elementx_announcement" 的 DataStore 中。
 *
 * @property preferenceDataStoreFactory DataStore 工厂，用于创建数据存储实例
 * @see AnnouncementStore 公告存储接口
 * @see androidx.datastore.preferences.core.intPreferencesKey 整数偏好设置键
 */
@ContributesBinding(AppScope::class)
class DefaultAnnouncementStore(
    /** DataStore 工厂，用于创建数据存储实例 */
    preferenceDataStoreFactory: PreferenceDataStoreFactory,
) : AnnouncementStore {
    /** 公告数据存储实例 */
    private val store = preferenceDataStoreFactory.create("elementx_announcement")

    /**
     * 设置公告状态
     *
     * 将公告的状态写入 DataStore 持久化存储。
     * 使用状态枚举的 ordinal 值作为存储的整数值。
     *
     * @param announcement 公告类型
     * @param status 新的公告状态
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    override suspend fun setAnnouncementStatus(announcement: Announcement, status: AnnouncementStatus) {
        val key = announcement.toKey()
        store.edit { prefs ->
            prefs[key] = status.ordinal
        }
    }

    /**
     * 获取公告状态 Flow
     *
     * 返回一个 Flow，监听指定公告的状态变化。
     * 如果存储中没有该公告的状态，则返回默认值：
     * - Space 公告默认返回 NeverShown（从未显示）
     * - NewNotificationSound 公告默认返回 Shown（已显示），因为应用升级时会通过迁移设置为 Show
     *
     * @param announcement 公告类型
     * @return Flow<AnnouncementStatus> 公告状态的 Flow
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     * @see io.element.android.libraries.preferences.api.store.PreferenceDataStoreFactory
     */
    override fun announcementStatusFlow(announcement: Announcement): Flow<AnnouncementStatus> {
        val key = announcement.toKey()
        // For NewNotificationSound, a migration will set it to Show on application upgrade (see AppMigration08)
        val defaultStatus = when (announcement) {
            Announcement.Space -> AnnouncementStatus.NeverShown
            Announcement.NewNotificationSound -> AnnouncementStatus.Shown
        }
        return store.data.map { prefs ->
            val ordinal = prefs[key] ?: defaultStatus.ordinal
            AnnouncementStatus.entries.getOrElse(ordinal) { defaultStatus }
        }
    }

    /**
     * 重置所有公告状态
     *
     * 清除 DataStore 中的所有偏好设置，恢复到初始状态。
     * 此操作会清除所有公告的显示记录，谨慎使用。
     */
    override suspend fun reset() {
        store.edit { it.clear() }
    }
}

/**
 * 将公告类型转换为对应的偏好设置键
 *
 * 根据公告类型返回对应的 DataStore 键，用于持久化存储。
 *
 * @return intPreferencesKey 对应的偏好设置键
 * @see androidx.datastore.preferences.core.intPreferencesKey
 */
private fun Announcement.toKey() = when (this) {
    Announcement.Space -> spaceAnnouncementKey
    Announcement.NewNotificationSound -> newNotificationSoundKey
}
