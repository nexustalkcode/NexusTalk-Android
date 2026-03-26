/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.spaces

/**
 * 空间公告事件 sealed 接口
 *
 * 定义了空间公告界面可能发生的用户交互事件。
 * 使用 sealed interface 可以确保所有事件类型都被明确列举，便于模式匹配处理。
 *
 * @see SpaceAnnouncementPresenter 用于处理这些事件
 * @see SpaceAnnouncementState 用于包含事件处理函数
 */
sealed interface SpaceAnnouncementEvents {
    /**
     * 继续事件
     *
     * 当用户点击"继续"按钮时触发，表示用户已阅读完空间公告介绍，
     * 准备关闭公告页面继续使用应用。此事件会触发公告状态更新，
     * 将空间公告标记为已显示(Shown)，确保后续不再向用户展示。
     *
     * @see SpaceAnnouncementPresenter 处理此事件
     * @see AnnouncementStatus 公告状态枚举
     */
    data object Continue : SpaceAnnouncementEvents
}
