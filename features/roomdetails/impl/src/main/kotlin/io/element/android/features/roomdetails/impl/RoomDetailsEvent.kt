/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

/**
 * 房间详情事件密封接口
 *
 * 定义房间详情页面可能发生的事件。
 * 使用密封接口确保类型安全，只能创建预定义的子类型。
 */
sealed interface RoomDetailsEvent {
    /**
     * 离开房间
     *
     * 用户请求离开当前房间。
     *
     * @property needsConfirmation 是否需要确认对话框
     */
    data class LeaveRoom(val needsConfirmation: Boolean) : RoomDetailsEvent

    /**
     * 静音通知
     *
     * 用户将房间通知设置为静音。
     */
    data object MuteNotification : RoomDetailsEvent

    /**
     * 取消静音通知
     *
     * 用户取消房间通知静音。
     */
    data object UnmuteNotification : RoomDetailsEvent

    /**
     * 复制到剪贴板
     *
     * 用户复制房间相关信息到剪贴板，如房间链接或房间ID。
     *
     * @property text 要复制的文本内容
     */
    data class CopyToClipboard(val text: String) : RoomDetailsEvent

    /**
     * 设置收藏状态
     *
     * 用户收藏或取消收藏当前房间。
     *
     * @property isFavorite 是否收藏
     */
    data class SetFavorite(val isFavorite: Boolean) : RoomDetailsEvent
}
