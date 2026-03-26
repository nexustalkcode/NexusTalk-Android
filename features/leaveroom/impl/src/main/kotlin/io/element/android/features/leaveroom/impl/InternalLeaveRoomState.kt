/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.leaveroom.api.LeaveRoomEvent
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开房间内部状态数据类
 *
 * 实现 LeaveRoomState 接口，提供离开房间功能的内部状态实现。
 * 包含离开操作的状态信息，支持多种确认场景。
 *
 * @property leaveAction 离开操作的状态，包含确认、加载、失败等状态
 * @property eventSink 事件处理函数
 * @see Confirmation 确认场景密封类
 */
data class InternalLeaveRoomState(
    val leaveAction: AsyncAction<Unit>,
    override val eventSink: (LeaveRoomEvent) -> Unit
) : LeaveRoomState

/**
 * 离开房间确认场景密封类
 *
 * 定义离开房间时可能出现的各种确认场景，
 * 根据房间类型和用户权限显示不同的确认对话框。
 */
@Immutable
sealed interface Confirmation : AsyncAction.Confirming {
    /** 离开私信房间场景 */
    data class Dm(val roomId: RoomId) : Confirmation
    /** 离开普通房间场景 */
    data class Generic(val roomId: RoomId) : Confirmation
    /** 离开私密房间场景 */
    data class PrivateRoom(val roomId: RoomId) : Confirmation
    /** 离开只有自己一个用户的房间场景 */
    data class LastUserInRoom(val roomId: RoomId) : Confirmation
    /** 自己是房间最后一个所有者场景 */
    data class LastOwnerInRoom(val roomId: RoomId) : Confirmation
}
