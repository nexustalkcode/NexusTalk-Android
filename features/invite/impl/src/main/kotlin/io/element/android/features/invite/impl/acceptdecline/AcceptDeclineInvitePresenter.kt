/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.acceptdecline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.invite.api.InviteData
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.invite.api.acceptdecline.ConfirmingDeclineInvite
import io.element.android.features.invite.impl.AcceptInvite
import io.element.android.features.invite.impl.DeclineInvite
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runUpdatingState
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 接受/拒绝邀请 Presenter
 *
 * 负责处理接受或拒绝房间邀请功能的业务逻辑和状态管理。
 * 管理邀请的接受、拒绝和确认流程。
 *
 * @property acceptInvite 接受邀请服务
 * @property declineInvite 拒绝邀请服务
 */
@Inject
class AcceptDeclineInvitePresenter(
    private val acceptInvite: AcceptInvite,
    private val declineInvite: DeclineInvite,
) : Presenter<AcceptDeclineInviteState> {
    /**
     * 生成界面状态
     *
     * @return AcceptDeclineInviteState 接受/拒绝邀请状态
     */
    @Composable
    override fun present(): AcceptDeclineInviteState {
        val localCoroutineScope = rememberCoroutineScope()
        val acceptedAction: MutableState<AsyncAction<RoomId>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }
        val declinedAction: MutableState<AsyncAction<RoomId>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }

        /**
         * 处理用户事件
         *
         * @param event 接受/拒绝邀请事件
         */
        fun handleEvent(event: AcceptDeclineInviteEvents) {
            when (event) {
                is AcceptDeclineInviteEvents.AcceptInvite -> {
                    localCoroutineScope.acceptInvite(event.invite.roomId, acceptedAction)
                }

                is AcceptDeclineInviteEvents.DeclineInvite -> {
                    val inviteData = event.invite
                    if (event.shouldConfirm) {
                        declinedAction.value = ConfirmingDeclineInvite(inviteData, event.blockUser)
                    } else {
                        localCoroutineScope.declineInvite(
                            inviteData = inviteData,
                            blockUser = event.blockUser,
                            declinedAction = declinedAction,
                        )
                    }
                }
                is InternalAcceptDeclineInviteEvents.ClearAcceptActionState -> {
                    acceptedAction.value = AsyncAction.Uninitialized
                }

                is InternalAcceptDeclineInviteEvents.ClearDeclineActionState -> {
                    declinedAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return AcceptDeclineInviteState(
            acceptAction = acceptedAction.value,
            declineAction = declinedAction.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 执行接受邀请操作
     *
     * @param roomId 房间 ID
     * @param acceptedAction 接受操作的异步状态
     */
    private fun CoroutineScope.acceptInvite(
        roomId: RoomId,
        acceptedAction: MutableState<AsyncAction<RoomId>>,
    ) = launch {
        acceptedAction.runUpdatingState {
            acceptInvite(roomId)
        }
    }

    /**
     * 执行拒绝邀请操作
     *
     * @param inviteData 邀请数据
     * @param blockUser 是否封禁用户
     * @param declinedAction 拒绝操作的异步状态
     */
    private fun CoroutineScope.declineInvite(
        inviteData: InviteData,
        blockUser: Boolean,
        declinedAction: MutableState<AsyncAction<RoomId>>,
    ) = launch {
        declinedAction.runUpdatingState {
            declineInvite(
                roomId = inviteData.roomId,
                blockUser = blockUser,
                reportRoom = false,
                reportReason = null
            )
        }
    }
}
