/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.aRoomListFiltersState
import io.element.android.features.home.impl.model.LatestEvent
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.model.aRoomListRoomSummary
import io.element.android.features.home.impl.model.anInviteSender
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.features.home.impl.search.aRoomListSearchState
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.invite.api.acceptdecline.anAcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomEvent
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.push.api.battery.aBatteryOptimizationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间列表状态提供者
 *
 * 为预览和测试提供 RoomListState 示例数据。
 *
 * @see RoomListState 房间列表状态
 */
open class RoomListStateProvider : PreviewParameterProvider<RoomListState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<RoomListState>
        get() = sequenceOf(
            aRoomListState(),
            aRoomListState(contextMenu = aContextMenuShown(roomName = null)),
            aRoomListState(contextMenu = aContextMenuShown(roomName = "A nice room name")),
            aRoomListState(contextMenu = aContextMenuShown(isFavorite = true)),
            aRoomListState(contentState = aRoomsContentState(securityBannerState = SecurityBannerState.RecoveryKeyConfirmation)),
            aRoomListState(contentState = anEmptyContentState()),
            aRoomListState(contentState = aSkeletonContentState()),
            aRoomListState(searchState = aRoomListSearchState(isSearchActive = true, query = "Test")),
            aRoomListState(contentState = aRoomsContentState(securityBannerState = SecurityBannerState.SetUpRecovery)),
            aRoomListState(contentState = aRoomsContentState(batteryOptimizationState = aBatteryOptimizationState(shouldDisplayBanner = true))),
            aRoomListState(contentState = anEmptyContentState(securityBannerState = SecurityBannerState.RecoveryKeyConfirmation)),
        )
}

/**
 * 创建示例房间列表状态
 *
 * @param contextMenu 上下文菜单状态
 * @param declineInviteMenu 拒绝邀请菜单状态
 * @param leaveRoomState 离开房间状态
 * @param searchState 搜索状态
 * @param filtersState 筛选器状态
 * @param contentState 内容状态
 * @param acceptDeclineInviteState 接受/拒绝邀请状态
 * @param hideInvitesAvatars 是否隐藏邀请头像
 * @param canReportRoom 是否可以报告房间
 * @param eventSink 事件处理函数
 * @return RoomListState 示例实例
 */
internal fun aRoomListState(
    contextMenu: RoomListState.ContextMenu = RoomListState.ContextMenu.Hidden,
    declineInviteMenu: RoomListState.DeclineInviteMenu = RoomListState.DeclineInviteMenu.Hidden,
    leaveRoomState: LeaveRoomState = aLeaveRoomState(),
    searchState: RoomListSearchState = aRoomListSearchState(),
    filtersState: RoomListFiltersState = aRoomListFiltersState(),
    contentState: RoomListContentState = aRoomsContentState(),
    acceptDeclineInviteState: AcceptDeclineInviteState = anAcceptDeclineInviteState(),
    hideInvitesAvatars: Boolean = false,
    canReportRoom: Boolean = true,
    eventSink: (RoomListEvents) -> Unit = {}
) = RoomListState(
    contextMenu = contextMenu,
    declineInviteMenu = declineInviteMenu,
    leaveRoomState = leaveRoomState,
    filtersState = filtersState,
    searchState = searchState,
    contentState = contentState,
    acceptDeclineInviteState = acceptDeclineInviteState,
    hideInvitesAvatars = hideInvitesAvatars,
    canReportRoom = canReportRoom,
    eventSink = eventSink,
)

/**
 * 创建示例离开房间状态
 *
 * @param eventSink 事件处理函数
 * @return LeaveRoomState 示例实例
 */
internal fun aLeaveRoomState(
    eventSink: (LeaveRoomEvent) -> Unit = {}
) = object : LeaveRoomState {
    override val eventSink: (LeaveRoomEvent) -> Unit = eventSink
}

/**
 * 创建示例房间列表摘要列表
 *
 * @return ImmutableList<RoomListRoomSummary> 示例房间摘要列表
 */
internal fun aRoomListRoomSummaryList(): ImmutableList<RoomListRoomSummary> {
    return persistentListOf(
        aRoomListRoomSummary(
            name = "Room Invited",
            avatarData = AvatarData("!roomId", "Room with Alice and Bob", size = AvatarSize.RoomListItem),
            id = "!roomId:domain",
            inviteSender = anInviteSender(),
            displayType = RoomSummaryDisplayType.INVITE,
        ),
        aRoomListRoomSummary(
            name = "Room",
            numberOfUnreadMessages = 1,
            timestamp = "14:18",
            latestEvent = LatestEvent.Synced("A very very very very long message which suites on two lines"),
            avatarData = AvatarData("!id", "R", size = AvatarSize.RoomListItem),
            id = "!roomId5:domain",
        ),
        aRoomListRoomSummary(
            name = "Room#2",
            numberOfUnreadMessages = 0,
            timestamp = "14:16",
            latestEvent = LatestEvent.Synced("A short message"),
            avatarData = AvatarData("!id", "Z", size = AvatarSize.RoomListItem),
            id = "!roomId2:domain",
        ),
        aRoomListRoomSummary(
            id = "!roomId3:domain",
            displayType = RoomSummaryDisplayType.PLACEHOLDER,
        ),
        aRoomListRoomSummary(
            id = "!roomId4:domain",
            displayType = RoomSummaryDisplayType.PLACEHOLDER,
        ),
    )
}

/**
 * 生成房间列表摘要列表
 *
 * @param numberOfRooms 房间数量（默认为 10）
 * @return ImmutableList<RoomListRoomSummary> 生成的房间摘要列表
 */
internal fun generateRoomListRoomSummaryList(
    numberOfRooms: Int = 10,
): ImmutableList<RoomListRoomSummary> {
    return List(numberOfRooms) { index ->
        aRoomListRoomSummary(
            name = "Room#$index",
            numberOfUnreadMessages = 0,
            timestamp = "14:16",
            latestEvent = LatestEvent.Synced("A message"),
            avatarData = AvatarData("!id$index", "${(65 + index % 26).toChar()}", size = AvatarSize.RoomListItem),
            id = "!roomId$index:domain",
        )
    }.toImmutableList()
}
