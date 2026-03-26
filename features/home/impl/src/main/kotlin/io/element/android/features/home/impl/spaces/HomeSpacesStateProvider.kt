/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.spaces

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

/**
 * 首页空间状态提供者
 *
 * 为预览和测试提供 HomeSpacesState 示例数据。
 *
 * @see HomeSpacesState 首页空间状态
 */
open class HomeSpacesStateProvider : PreviewParameterProvider<HomeSpacesState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<HomeSpacesState>
        get() = sequenceOf(
            aHomeSpacesState(
                spaceRooms = SpaceRoomProvider().values.toList(),
                seenSpaceInvites = setOf(
                    RoomId("!spaceId3:example.com"),
                ),
            ),
            aHomeSpacesState(
                space = CurrentSpace.Space(
                    spaceRoom = aSpaceRoom(roomId = RoomId("!mySpace:example.com"))
                ),
                spaceRooms = aListOfSpaceRooms(),
            ),
            aHomeSpacesState(
                space = CurrentSpace.Space(
                    spaceRoom = aSpaceRoom(roomId = RoomId("!mySpace:example.com"))
                ),
                spaceRooms = aListOfSpaceRooms(),
                canCreateSpaces = false,
            ),
            aHomeSpacesState(
                space = CurrentSpace.Root,
                spaceRooms = emptyList(),
                canCreateSpaces = true,
            ),
        )
}

/**
 * 创建示例首页空间状态
 *
 * @param space 当前空间
 * @param spaceRooms 空间房间列表
 * @param seenSpaceInvites 已查看的空间邀请 ID 集合
 * @param hideInvitesAvatar 是否隐藏邀请头像
 * @param canCreateSpaces 是否可以创建空间
 * @param canExploreSpaces 是否可以探索空间
 * @param eventSink 事件处理函数
 * @return HomeSpacesState 示例实例
 */
internal fun aHomeSpacesState(
    space: CurrentSpace = CurrentSpace.Root,
    spaceRooms: List<SpaceRoom> = aListOfSpaceRooms(),
    seenSpaceInvites: Set<RoomId> = emptySet(),
    hideInvitesAvatar: Boolean = false,
    canCreateSpaces: Boolean = true,
    canExploreSpaces: Boolean = true,
    eventSink: (HomeSpacesEvents) -> Unit = {},
) = HomeSpacesState(
    space = space,
    spaceRooms = spaceRooms.toImmutableList(),
    seenSpaceInvites = seenSpaceInvites.toImmutableSet(),
    hideInvitesAvatar = hideInvitesAvatar,
    canCreateSpaces = canCreateSpaces,
    canExploreSpaces = canExploreSpaces,
    eventSink = eventSink,
)

/**
 * 创建示例空间房间列表
 *
 * @return 空间房间列表
 */
fun aListOfSpaceRooms(): List<SpaceRoom> {
    return listOf(
        aSpaceRoom(roomId = RoomId("!spaceId0:example.com")),
        aSpaceRoom(roomId = RoomId("!spaceId1:example.com")),
        aSpaceRoom(roomId = RoomId("!spaceId2:example.com")),
    )
}
