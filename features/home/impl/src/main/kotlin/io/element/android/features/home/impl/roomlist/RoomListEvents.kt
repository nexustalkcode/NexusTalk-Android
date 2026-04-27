/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 首页房间列表可能触发的用户事件。
 */
sealed interface RoomListEvents {
    /** 更新当前列表可见范围。 */
    data class UpdateVisibleRange(val range: IntRange) : RoomListEvents
    /** 关闭验证请求提示。 */
    data object DismissRequestVerificationPrompt : RoomListEvents
    /** 关闭顶部横幅。 */
    data object DismissBanner : RoomListEvents
    /** 关闭全屏意图权限横幅。 */
    data object DismissFullScreenIntentPermissionBanner : RoomListEvents
    /** 关闭电池优化横幅。 */
    data object DismissBatteryOptimizationBanner : RoomListEvents
    /** 关闭新通知音横幅。 */
    data object DismissNewNotificationSoundBanner : RoomListEvents
    /** 切换搜索结果显示状态。 */
    data object ToggleSearchResults : RoomListEvents
    /** 显示房间上下文菜单。 */
    data class ShowContextMenu(val roomSummary: RoomListRoomSummary) : RoomListEvents
    /** 接受邀请。 */
    data class AcceptInvite(val roomSummary: RoomListRoomSummary) : RoomListEvents
    /** 拒绝邀请，可选是否同时拉黑用户。 */
    data class DeclineInvite(val roomSummary: RoomListRoomSummary, val blockUser: Boolean) : RoomListEvents
    /** 显示拒绝邀请菜单。 */
    data class ShowDeclineInviteMenu(val roomSummary: RoomListRoomSummary) : RoomListEvents
    /** 隐藏拒绝邀请菜单。 */
    data object HideDeclineInviteMenu : RoomListEvents

    /**
     * 房间上下文菜单内部事件。
     */
    sealed interface ContextMenuEvents : RoomListEvents
    /** 隐藏房间上下文菜单。 */
    data object HideContextMenu : ContextMenuEvents
    /** 离开指定房间。 */
    data class LeaveRoom(val roomId: RoomId, val needsConfirmation: Boolean) : ContextMenuEvents
    /** 标记房间为已读。 */
    data class MarkAsRead(val roomId: RoomId) : ContextMenuEvents
    /** 标记房间为未读。 */
    data class MarkAsUnread(val roomId: RoomId) : ContextMenuEvents
    /** 设置或取消房间收藏状态。 */
    data class SetRoomIsFavorite(val roomId: RoomId, val isFavorite: Boolean) : ContextMenuEvents
    /** 清理指定房间缓存。 */
    data class ClearCacheOfRoom(val roomId: RoomId) : ContextMenuEvents
}
