/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 社区列表状态数据类
 *
 * 表示社区列表的完整状态，包含上下文菜单、邀请管理、筛选器、搜索和内容状态等信息。
 *
 * @property contextMenu 上下文菜单状态
 * @property declineInviteMenu 拒绝邀请菜单状态
 * @property leaveRoomState 离开房间状态
 * @property filtersState 房间筛选状态
 * @property searchState 搜索状态
 * @property contentState 列表内容状态
 * @property acceptDeclineInviteState 接受/拒绝邀请状态
 * @property hideInvitesAvatars 是否隐藏邀请头像
 * @property canReportRoom 是否可以报告房间
 * @property eventSink 事件处理函数
 */
data class GroupListState(
    val contextMenu: ContextMenu,
    val declineInviteMenu: DeclineInviteMenu,
    val leaveRoomState: LeaveRoomState,
    val filtersState: RoomListFiltersState,
    val searchState: RoomListSearchState,
    val contentState: GroupListContentState,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val hideInvitesAvatars: Boolean,
    val canReportRoom: Boolean,
    val eventSink: (GroupListEvents) -> Unit,
) {
    /** 是否显示筛选器（仅在房间列表模式下显示） */
    val displayFilters = contentState is GroupListContentState.Rooms

    /**
     * 上下文菜单密封接口
     */
    sealed interface ContextMenu {
        /** 隐藏状态 */
        data object Hidden : ContextMenu
        /**
         * 显示状态
         *
         * @property roomId 房间 ID
         * @property roomName 房间名称
         * @property isDm 是否为直接消息
         * @property isFavorite 是否为收藏
         * @property hasNewContent 是否有新内容
         * @property displayClearRoomCacheAction 是否显示清除缓存操作
         */
        data class Shown(
            val roomId: RoomId,
            val roomName: String?,
            val isDm: Boolean,
            val isFavorite: Boolean,
            val hasNewContent: Boolean,
            val displayClearRoomCacheAction: Boolean,
        ) : ContextMenu
    }

    /**
     * 拒绝邀请菜单密封接口
     */
    sealed interface DeclineInviteMenu {
        /** 隐藏状态 */
        data object Hidden : DeclineInviteMenu
        /**
         * 显示状态
         *
         * @property roomSummary 房间摘要
         */
        data class Shown(val roomSummary: RoomListRoomSummary) : DeclineInviteMenu
    }
}

/**
 * 社区列表内容状态密封接口
 *
 * 表示社区列表的不同内容状态（加载中、空状态、房间列表）。
 */
@Immutable
sealed interface GroupListContentState {
    /**
     * 骨架屏加载状态
     *
     * @property count 骨架项数量
     */
    data class Skeleton(val count: Int) : GroupListContentState

    /**
     * 空状态
     */
    data object Empty : GroupListContentState

    /**
     * 社区列表状态
     *
     * @property summaries 房间摘要列表
     * @property seenRoomInvites 已查看的房间邀请 ID 集合
     */
    data class Rooms(
        val summaries: ImmutableList<RoomListRoomSummary>,
        val seenRoomInvites: ImmutableSet<RoomId>,
    ) : GroupListContentState
}
