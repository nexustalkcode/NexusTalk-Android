/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

/**
 * 固定消息横幅事件密封接口
 *
 * 定义了固定消息横幅界面中用户交互产生的事件类型。
 * 用于处理用户在固定消息横幅上的各种操作。
 *
 * @see MoveToNextPinned 切换到下一个置顶消息
 */
sealed interface PinnedMessagesBannerEvents {
    /**
     * 移动到下一个置顶消息事件
     *
     * 当用户点击横幅或执行切换操作时触发，
     * 会将当前显示的置顶消息切换到列表中的前一个。
     * 使用模运算确保在消息列表中循环切换。
     */
    data object MoveToNextPinned : PinnedMessagesBannerEvents
}
