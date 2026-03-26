/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runUpdatingState
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 举报消息Presenter
 *
 * 负责管理举报消息界面的业务逻辑，包括：
 * - 收集用户输入的举报原因
 * - 处理是否屏蔽用户的选项
 * - 执行举报内容操作并更新界面状态
 *
 * @property room 已加入的房间实例，用于调用Matrix SDK的举报API
 * @property inputs 举报所需的输入参数，包括事件ID和发送者ID
 * @property snackbarDispatcher 提示信息分发器，用于显示操作结果
 */
@AssistedInject
class ReportMessagePresenter(
    private val room: JoinedRoom,
    @Assisted private val inputs: Inputs,
    private val snackbarDispatcher: SnackbarDispatcher,
) : Presenter<ReportMessageState> {
    /**
     * 举报消息Presenter的输入参数
     *
     * @property eventId 要举报的消息事件ID
     * @property senderId 消息发送者的用户ID
     */
    data class Inputs(
        val eventId: EventId,
        val senderId: UserId,
    )

    /**
     * Presenter工厂接口
     *
     * 用于创建ReportMessagePresenter实例的工厂方法，
     * 配合依赖注入框架使用。
     */
    @AssistedFactory
    interface Factory {
        fun create(inputs: Inputs): ReportMessagePresenter
    }

    @Composable
    override fun present(): ReportMessageState {
        val coroutineScope = rememberCoroutineScope()
        var reason by rememberSaveable { mutableStateOf("") }
        var blockUser by rememberSaveable { mutableStateOf(false) }
        var result: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        fun handleEvent(event: ReportMessageEvents) {
            when (event) {
                is ReportMessageEvents.UpdateReason -> reason = event.reason
                ReportMessageEvents.ToggleBlockUser -> blockUser = !blockUser
                ReportMessageEvents.Report -> coroutineScope.report(inputs.eventId, inputs.senderId, reason, blockUser, result)
                ReportMessageEvents.ClearError -> result.value = AsyncAction.Uninitialized
            }
        }

        return ReportMessageState(
            reason = reason,
            blockUser = blockUser,
            result = result.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.report(
        eventId: EventId,
        userId: UserId,
        reason: String,
        blockUser: Boolean,
        result: MutableState<AsyncAction<Unit>>,
    ) = launch {
        result.runUpdatingState {
            val userIdToBlock = userId.takeIf { blockUser }
            room.reportContent(eventId, reason, userIdToBlock)
                .onSuccess {
                    snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_report_submitted))
                }
        }
    }
}
