/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.store

import io.element.android.features.announcement.api.Announcement
import kotlinx.coroutines.flow.Flow

/**
 * 公告存储接口
 *
 * 定义了公告状态持久化的抽象操作，包括设置状态、查询状态流以及重置所有状态。
 * 该接口将公告状态的存储逻辑抽象出来，便于实现不同的存储策略。
 *
 * @see DefaultAnnouncementStore 默认实现类
 * @see io.element.android.features.announcement.impl.store.InMemoryAnnouncementStore 内存实现（测试用）
 */
interface AnnouncementStore {
    /**
     * 设置公告状态
     *
     * 更新指定公告的状态，状态变化会被持久化存储。
     * 常见用途是将公告从未显示(NeverShown)设置为显示(Show)，
     * 或从显示(Show)设置为已显示(Shown)。
     *
     * @param announcement 公告类型
     * @param status 新的公告状态
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    suspend fun setAnnouncementStatus(
        /** 公告类型 */
        announcement: Announcement,
        /** 新的公告状态 */
        status: AnnouncementStatus,
    )

    /**
     * 获取公告状态 Flow
     *
     * 返回一个 Flow，用于监听指定公告的状态变化。
     * 状态变化会实时推送给观察者，便于 UI 层响应状态更新。
     *
     * @param announcement 公告类型
     * @return Flow<AnnouncementStatus> 公告状态的 Flow
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    fun announcementStatusFlow(
        /** 公告类型 */
        announcement: Announcement,
    ): Flow<AnnouncementStatus>

    /**
     * 重置所有公告状态
     *
     * 清除所有公告的存储状态，通常用于测试或用户清除数据场景。
     * 重置后，所有公告将恢复为初始状态（从未显示）。
     */
    suspend fun reset()
}
