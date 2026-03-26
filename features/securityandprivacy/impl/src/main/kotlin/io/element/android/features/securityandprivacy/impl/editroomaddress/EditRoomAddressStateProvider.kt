/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.editroomaddress

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidity

/**
 * 编辑房间地址状态提供者
 *
 * 用于在预览模式下提供各种状态的 EditRoomAddressState 实例。
 * 继承自 PreviewParameterProvider，用于 Compose 预览功能。
 *
 * @see EditRoomAddressState 页面状态
 * @see PreviewParameterProvider 预览参数提供者基类
 */
open class EditRoomAddressStateProvider : PreviewParameterProvider<EditRoomAddressState> {
    override val values: Sequence<EditRoomAddressState>
        get() = sequenceOf(
            anEditRoomAddressState(),
            anEditRoomAddressState(roomAddressValidity = RoomAddressValidity.NotAvailable),
            anEditRoomAddressState(roomAddressValidity = RoomAddressValidity.InvalidSymbols),
            anEditRoomAddressState(roomAddressValidity = RoomAddressValidity.Valid),
            anEditRoomAddressState(roomAddressValidity = RoomAddressValidity.Valid, saveAction = AsyncAction.Loading),
        )
}

fun anEditRoomAddressState(
    roomAddress: String = "therapy",
    roomAddressValidity: RoomAddressValidity = RoomAddressValidity.Unknown,
    homeserverName: String = ":myserver.org",
    saveAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (EditRoomAddressEvents) -> Unit = {}
) = EditRoomAddressState(
    roomAddress = roomAddress,
    roomAddressValidity = roomAddressValidity,
    homeserverName = homeserverName,
    saveAction = saveAction,
    eventSink = eventSink
)
