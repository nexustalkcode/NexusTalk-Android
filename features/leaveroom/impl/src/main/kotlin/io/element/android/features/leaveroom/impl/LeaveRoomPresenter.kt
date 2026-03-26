/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.leaveroom.api.LeaveRoomEvent
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.room.powerlevels.usersWithRole
import io.element.android.libraries.push.api.notifications.conversations.NotificationConversationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 离开房间 Presenter
 *
 * 负责处理离开房间的业务逻辑，包括：
 * - 显示离开房间确认对话框
 * - 执行离开房间操作
 * - 处理各种确认场景（私信、私密房间、最后所有者等）
 * - 清理相关通知
 *
 * 使用协程进行异步操作，确保主线程流畅响应。
 *
 * @property client Matrix 客户端，用于执行房间操作
 * @property dispatchers 协程调度器
 * @property notificationConversationService 通知会话服务，用于清理通知
 * @see LeaveRoomState 离开房间状态
 */
@Inject
class LeaveRoomPresenter(
    private val client: MatrixClient,
    private val dispatchers: CoroutineDispatchers,
    private val notificationConversationService: NotificationConversationService,
) : Presenter<LeaveRoomState> {
    /**
     * 创建视图状态
     *
     * @return LeaveRoomState 当前离开房间的状态
     */
    @Composable
    override fun present(): LeaveRoomState {
        val scope = rememberCoroutineScope()
        val leaveAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }
        return InternalLeaveRoomState(
            leaveAction = leaveAction.value,
        ) { event ->
            when (event) {
                is LeaveRoomEvent.LeaveRoom ->
                    if (event.needsConfirmation) {
                        scope.showLeaveRoomAlert(roomId = event.roomId, leaveAction = leaveAction)
                    } else {
                        scope.leaveRoom(roomId = event.roomId, leaveAction = leaveAction)
                    }
                InternalLeaveRoomEvent.ResetState -> leaveAction.value = AsyncAction.Uninitialized
            }
        }
    }

    /**
     * 显示离开房间确认对话框
     *
     * 根据房间类型和用户权限确定要显示的确认场景。
     *
     * @param roomId 房间 ID
     * @param leaveAction 离开操作状态
     */
    private fun CoroutineScope.showLeaveRoomAlert(
        roomId: RoomId,
        leaveAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        client.getRoom(roomId)?.use { room ->
            val roomInfo = room.roomInfoFlow.first()
            leaveAction.value = when {
                roomInfo.isDm -> Confirmation.Dm(roomId)
                room.isLastOwner() && roomInfo.joinedMembersCount > 1L -> Confirmation.LastOwnerInRoom(roomId)
                // If unknown, assume the room is private
                roomInfo.isPublic == null || roomInfo.isPublic == false -> Confirmation.PrivateRoom(roomId)
                roomInfo.joinedMembersCount == 1L -> Confirmation.LastUserInRoom(roomId)
                else -> Confirmation.Generic(roomId)
            }
        }
    }

    /**
     * 执行离开房间操作
     *
     * @param roomId 房间 ID
     * @param leaveAction 离开操作状态
     */
    private fun CoroutineScope.leaveRoom(
        roomId: RoomId,
        leaveAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        leaveAction.runCatchingUpdatingState {
            client.getRoom(roomId)!!.use { room ->
                room
                    .leave()
                    .onSuccess { notificationConversationService.onLeftRoom(client.sessionId, roomId) }
                    .onFailure { Timber.e(it, "Error while leaving room ${room.roomId}") }
                    .getOrThrow()
            }
        }
    }

    /**
     * 判断是否是房间的最后一个所有者
     *
     * @return 是否是最后一个所有者
     */
    private suspend fun BaseRoom.isLastOwner(): Boolean {
        if (roomInfoFlow.value.isDm) {
            // DMs are not owned by the user, so we can return false
            return false
        } else {
            val hasPrivilegedCreatorRole = roomInfoFlow.value.privilegedCreatorRole
            if (!hasPrivilegedCreatorRole) return false
            val owners = usersWithRole { role -> role is RoomMember.Role.Owner }.first()
            return owners.size == 1 && owners.first().userId == sessionId
        }
    }
}
