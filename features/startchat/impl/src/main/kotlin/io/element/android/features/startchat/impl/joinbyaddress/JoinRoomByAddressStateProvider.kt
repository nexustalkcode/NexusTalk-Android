/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.joinbyaddress

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

/**
 * 为“按地址加入房间”页面预览提供样例状态。
 */
open class JoinRoomByAddressStateProvider : PreviewParameterProvider<JoinRoomByAddressState> {
    override val values: Sequence<JoinRoomByAddressState>
        get() = sequenceOf(
            aJoinRoomByAddressState(),
            aJoinRoomByAddressState(address = "#room-"),
            aJoinRoomByAddressState(address = "#room-", addressState = RoomAddressState.Invalid),
            aJoinRoomByAddressState(address = "#room-name:nexustalk.space", addressState = RoomAddressState.Resolving),
            aJoinRoomByAddressState(address = "#room-name-none:matrix.org", addressState = RoomAddressState.RoomNotFound),
            aJoinRoomByAddressState(
                address = "#room-name:nexustalk.space",
                addressState = RoomAddressState.RoomFound(ResolvedRoomAlias(RoomId("!aRoom:id"), emptyList())),
            ),
        )
}

/**
 * 构造一份“按地址加入房间”页面样例状态。
 */
fun aJoinRoomByAddressState(
    address: String = "",
    addressState: RoomAddressState = RoomAddressState.Unknown,
    eventSink: (JoinRoomByAddressEvent) -> Unit = {},
) = JoinRoomByAddressState(
    address = address,
    addressState = addressState,
    eventSink = eventSink
)
