/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.leaveroom.api.LeaveRoomEvent
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开房间内部状态预览参数提供者
 *
 * 提供 InternalLeaveRoomState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 * 包含多种状态场景：未初始化、各种确认场景、加载中、失败等。
 *
 * @see InternalLeaveRoomState 离开房间内部状态
 */
class InternalLeaveRoomStateProvider : PreviewParameterProvider<InternalLeaveRoomState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 InternalLeaveRoomState 序列
     */
    override val values: Sequence<InternalLeaveRoomState>
        get() = sequenceOf(
            aLeaveRoomState(),
            aLeaveRoomState(
                leaveAction = Confirmation.Generic(roomId = A_ROOM_ID),
            ),
            aLeaveRoomState(
                leaveAction = Confirmation.PrivateRoom(roomId = A_ROOM_ID),
            ),
            aLeaveRoomState(
                leaveAction = Confirmation.LastUserInRoom(roomId = A_ROOM_ID),
            ),
            aLeaveRoomState(
                leaveAction = Confirmation.Dm(roomId = A_ROOM_ID),
            ),
            aLeaveRoomState(
                leaveAction = Confirmation.LastOwnerInRoom(roomId = A_ROOM_ID),
            ),
            aLeaveRoomState(
                leaveAction = AsyncAction.Loading,
            ),
            aLeaveRoomState(
                leaveAction = AsyncAction.Failure(RuntimeException("Something went wrong")),
            ),
        )
}

/** 预览用的示例房间 ID */
private val A_ROOM_ID = RoomId("!aRoomId:aDomain")

/**
 * 创建示例离开房间状态
 *
 * @param leaveAction 离开操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return 示例 InternalLeaveRoomState 实例
 */
fun aLeaveRoomState(
    leaveAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (LeaveRoomEvent) -> Unit = {},
) = InternalLeaveRoomState(
    leaveAction = leaveAction,
    eventSink = eventSink,
)
