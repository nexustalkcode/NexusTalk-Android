/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

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
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

/**
 * 社区列表状态提供者
 *
 * 为预览和测试提供 GroupListState 示例数据。
 *
 * @see GroupListState 社区列表状态
 */
open class GroupListStateProvider : PreviewParameterProvider<GroupListState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<GroupListState>
        get() = sequenceOf(
            aGroupListState(),
            aGroupListState(contextMenu = aContextMenuShown(roomName = null)),
            aGroupListState(contextMenu = aContextMenuShown(roomName = "A nice room name")),
            aGroupListState(contextMenu = aContextMenuShown(isFavorite = true)),
            aGroupListState(contentState = anEmptyContentState()),
            aGroupListState(contentState = aSkeletonContentState()),
            aGroupListState(searchState = aRoomListSearchState(isSearchActive = true, query = "Test")),
        )
}

/**
 * 创建示例社区列表状态
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
 * @return GroupListState 示例实例
 */
internal fun aGroupListState(
    contextMenu: GroupListState.ContextMenu = GroupListState.ContextMenu.Hidden,
    leaveRoomState: LeaveRoomState = aLeaveRoomState(),
    searchState: RoomListSearchState = aRoomListSearchState(),
    filtersState: RoomListFiltersState = aRoomListFiltersState(),
    contentState: GroupListContentState = aRoomsContentState(),
    acceptDeclineInviteState: AcceptDeclineInviteState = anAcceptDeclineInviteState(),
    hideInvitesAvatars: Boolean = false,
    canReportRoom: Boolean = true,
    eventSink: (GroupListEvents) -> Unit = {}
) = GroupListState(
    contextMenu = contextMenu,
    declineInviteMenu = GroupListState.DeclineInviteMenu.Hidden,
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
 * 创建示例上下文菜单显示状态
 *
 * @param roomName 房间名称
 * @param isDm 是否为直接消息
 * @param hasNewContent 是否有新内容
 * @param isFavorite 是否为收藏
 * @return GroupListState.ContextMenu.Shown 示例实例
 */
internal fun aContextMenuShown(
    roomName: String? = "aRoom",
    isDm: Boolean = false,
    hasNewContent: Boolean = false,
    isFavorite: Boolean = false,
) = GroupListState.ContextMenu.Shown(
    roomId = RoomId("!aRoom:aDomain"),
    roomName = roomName,
    isDm = isDm,
    hasNewContent = hasNewContent,
    isFavorite = isFavorite,
    displayClearRoomCacheAction = false,
)

/**
 * 创建示例骨架屏内容状态
 *
 * @param count 骨架项数量
 * @return GroupListContentState.Skeleton 示例实例
 */
internal fun aSkeletonContentState(count: Int = 16) = GroupListContentState.Skeleton(count = count)

/**
 * 创建示例空内容状态
 *
 * @return GroupListContentState.Empty 示例实例
 */
internal fun anEmptyContentState() = GroupListContentState.Empty

/**
 * 创建示例房间列表内容状态
 *
 * @param summaries 房间摘要列表
 * @return GroupListContentState.Rooms 示例实例
 */
internal fun aRoomsContentState(
    summaries: ImmutableList<RoomListRoomSummary> = aRoomListRoomSummaryList(),
) = GroupListContentState.Rooms(
    summaries = summaries,
    seenRoomInvites = persistentListOf<RoomId>().toImmutableSet(),
)

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
