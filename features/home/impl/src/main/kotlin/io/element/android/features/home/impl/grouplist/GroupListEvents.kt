/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 社区列表事件密封接口
 *
 * 定义社区列表可能发生的用户交互事件。
 */
sealed interface GroupListEvents {
    /**
     * 更新可见范围
     *
     * @property range 可见范围
     */
    data class UpdateVisibleRange(val range: IntRange) : GroupListEvents
    /** 关闭横幅 */
    data object DismissBanner : GroupListEvents
    /** 切换搜索结果 */
    data object ToggleSearchResults : GroupListEvents
    /**
     * 显示上下文菜单
     *
     * @property roomSummary 房间摘要
     */
    data class ShowContextMenu(val roomSummary: RoomListRoomSummary) : GroupListEvents

    /**
     * 接受邀请
     *
     * @property roomSummary 房间摘要
     */
    data class AcceptInvite(val roomSummary: RoomListRoomSummary) : GroupListEvents
    /**
     * 拒绝邀请
     *
     * @property roomSummary 房间摘要
     * @property blockUser 是否阻止用户
     */
    data class DeclineInvite(val roomSummary: RoomListRoomSummary, val blockUser: Boolean) : GroupListEvents
    /**
     * 显示拒绝邀请菜单
     *
     * @property roomSummary 房间摘要
     */
    data class ShowDeclineInviteMenu(val roomSummary: RoomListRoomSummary) : GroupListEvents
    /** 隐藏拒绝邀请菜单 */
    data object HideDeclineInviteMenu : GroupListEvents

    /**
     * 上下文菜单事件密封接口
     */
    sealed interface ContextMenuEvents : GroupListEvents
    /** 隐藏上下文菜单 */
    data object HideContextMenu : ContextMenuEvents
    /**
     * 离开房间
     *
     * @property roomId 房间 ID
     * @property needsConfirmation 是否需要确认
     */
    data class LeaveRoom(val roomId: RoomId, val needsConfirmation: Boolean) : ContextMenuEvents
    /**
     * 标记为已读
     *
     * @property roomId 房间 ID
     */
    data class MarkAsRead(val roomId: RoomId) : ContextMenuEvents
    /**
     * 标记为未读
     *
     * @property roomId 房间 ID
     */
    data class MarkAsUnread(val roomId: RoomId) : ContextMenuEvents
    /**
     * 设置房间收藏状态
     *
     * @property roomId 房间 ID
     * @property isFavorite 是否收藏
     */
    data class SetRoomIsFavorite(val roomId: RoomId, val isFavorite: Boolean) : ContextMenuEvents
    /**
     * 清除房间缓存
     *
     * @property roomId 房间 ID
     */
    data class ClearCacheOfRoom(val roomId: RoomId) : ContextMenuEvents
}
