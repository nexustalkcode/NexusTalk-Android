/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow

/**
 * 公告服务接口
 *
 * 定义了应用内公告的显示和管理功能，包括公告的展示、关闭处理、
 * 状态监听以及界面渲染等核心能力。
 * 该接口是公告功能的核心抽象，提供了与公告系统交互的统一入口。
 *
 * @see DefaultAnnouncementService 默认实现类
 */
interface AnnouncementService {
    /**
     * 显示公告
     *
     * 触发指定公告的显示逻辑，根据公告类型执行相应的展示操作。
     * 例如，对于空间公告，会检查是否从未显示过，只有未显示过的才会标记为显示状态。
     *
     * @param announcement 要显示的公告类型
     * @see Announcement 公告类型枚举
     */
    suspend fun showAnnouncement(announcement: Announcement)

    /**
     * 处理公告关闭事件
     *
     * 当用户关闭某个公告时调用此方法，将该公告的状态更新为已显示(Shown)，
     * 以便后续不会再向用户展示该公告。
     *
     * @param announcement 被关闭的公告类型
     * @see Announcement 公告类型枚举
     */
    suspend fun onAnnouncementDismissed(announcement: Announcement)

    /**
     * 获取待显示公告的 Flow
     *
     * 返回一个 Flow，用于监听所有待显示的公告列表。
     * 当有新的公告需要显示时，会通过此 Flow 通知观察者。
     * 该 Flow 会持续 emit 公告列表的变化。
     *
     * @return Flow<List<Announcement>> 待显示公告列表的 Flow
     * @see Announcement 公告类型枚举
     */
    fun announcementsToShowFlow(): Flow<List<Announcement>>

    /**
     * 使用 Composable 渲染全屏公告 UI
     *
     * 渲染公告界面的入口方法，在 Compose 环境中绘制公告的 UI 组件。
     * 根据当前状态决定是否显示公告内容，通常与 announcementsToShowFlow 配合使用。
     *
     * @param modifier 修饰符，用于控制布局和行为
     * @see androidx.compose.runtime.Composable Composable 注解
     */
    @Composable
    fun Render(
        modifier: Modifier,
    )
}
