/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.ui.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.CurrentUserMembership
import io.element.android.libraries.matrix.api.room.RoomType
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.toImmutableList

class SpaceRoomProvider : PreviewParameterProvider<SpaceRoom> {
    override val values: Sequence<SpaceRoom> = sequenceOf(
        aSpaceRoom(
            roomType = RoomType.Room,
            displayName = "Room name with topic",
            topic = "Room topic that is quite long and might be truncated"
        ),
        aSpaceRoom(
            roomType = RoomType.Room,
            displayName = "Room name no topic",
            state = CurrentUserMembership.LEFT,
        ),
        aSpaceRoom(
            displayName = "Alice",
            roomType = RoomType.Room,
            isDirect = true,
            heroes = listOf(aMatrixUser(displayName = "Alice")),
            state = CurrentUserMembership.JOINED,
            numJoinedMembers = 2,
        ),
        aSpaceRoom(
            roomType = RoomType.Room,
            displayName = "Room name with topic",
            topic = "Room topic that is quite long and might be truncated",
            state = CurrentUserMembership.INVITED,
        ),
        aSpaceRoom(
            roomType = RoomType.Room,
            displayName = "Room name no topic",
            state = CurrentUserMembership.INVITED,
        ),
        aSpaceRoom(
            numJoinedMembers = 5,
            childrenCount = 10,
            worldReadable = true,
            roomId = RoomId("!spaceId0:example.com"),
        ),
        aSpaceRoom(
            numJoinedMembers = 5,
            childrenCount = 10,
            worldReadable = true,
            avatarUrl = "anUrl",
            roomId = RoomId("!spaceId1:example.com"),
            state = CurrentUserMembership.LEFT,
        ),
        aSpaceRoom(
            numJoinedMembers = 5,
            childrenCount = 10,
            worldReadable = true,
            avatarUrl = "anUrl",
            roomId = RoomId("!spaceId2:example.com"),
            state = CurrentUserMembership.INVITED,
        ),
        aSpaceRoom(
            displayName = "Alice",
            roomType = RoomType.Space,
            heroes = listOf(aMatrixUser(displayName = "Alice")),
            state = CurrentUserMembership.JOINED,
            numJoinedMembers = 2,
        ),
    )
}

private fun aSpaceRoom(
    rawName: String? = null,
    displayName: String = "Space name",
    avatarUrl: String? = null,
    canonicalAlias: RoomAlias? = null,
    childrenCount: Int = 0,
    guestCanJoin: Boolean = false,
    heroes: List<MatrixUser> = emptyList(),
    joinRule: JoinRule? = null,
    numJoinedMembers: Int = 0,
    roomId: RoomId = RoomId("!roomId:example.com"),
    roomType: RoomType = RoomType.Space,
    state: CurrentUserMembership? = null,
    topic: String? = null,
    worldReadable: Boolean = false,
    isDirect: Boolean? = null,
    via: List<String> = emptyList(),
) = SpaceRoom(
    rawName = rawName,
    displayName = displayName,
    avatarUrl = avatarUrl,
    canonicalAlias = canonicalAlias,
    childrenCount = childrenCount,
    guestCanJoin = guestCanJoin,
    heroes = heroes.toImmutableList(),
    joinRule = joinRule,
    numJoinedMembers = numJoinedMembers,
    roomId = roomId,
    roomType = roomType,
    state = state,
    topic = topic,
    worldReadable = worldReadable,
    via = via.toImmutableList(),
    isDirect = isDirect,
)
