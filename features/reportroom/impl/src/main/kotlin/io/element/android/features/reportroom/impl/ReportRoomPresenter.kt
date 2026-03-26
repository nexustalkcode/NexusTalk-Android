/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

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
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 举报房间页面的 Presenter 类
 *
 * 负责处理举报房间的业务逻辑，包括管理界面状态和处理用户事件
 * 使用依赖注入机制创建，使用 Compose 的响应式状态管理
 *
 * @property roomId 要举报的房间ID
 * @property reportRoom 执行举报操作的接口实例
 */
@AssistedInject
class ReportRoomPresenter(
    @Assisted private val roomId: RoomId,
    private val reportRoom: ReportRoom,
) : Presenter<ReportRoomState> {
    /**
     * 用于创建 [ReportRoomPresenter] 实例的工厂接口
     *
     * 实现依赖注入的工厂模式，支持 AssistedInject
     */
    @AssistedFactory
    fun interface Factory {
        /**
         * 创建举报房间的 Presenter 实例
         *
         * @param roomId 要举报的房间ID
         * @return 返回新的 [ReportRoomPresenter] 实例
         */
        fun create(roomId: RoomId): ReportRoomPresenter
    }

    /**
     * 生成并返回举报房间的界面状态
     *
     * 使用 Compose 的 @Composable 注解，在状态变化时自动重组界面
     *
     * @return 返回 [ReportRoomState]，包含界面的当前状态
     */
    @Composable
    override fun present(): ReportRoomState {
        // 举报原因文本，支持状态保存
        var reason by rememberSaveable { mutableStateOf("") }

        // 是否离开房间的开关状态，支持状态保存
        var leaveRoom by rememberSaveable { mutableStateOf(false) }

        // 举报操作的异步状态（未初始化/加载中/成功/失败）
        var reportAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        // 用于启动协程的协程作用域
        val coroutineScope = rememberCoroutineScope()

        /**
         * 处理用户事件的函数
         *
         * 根据不同的事件类型执行相应的业务逻辑
         *
         * @param event 用户触发的事件
         */
        fun handleEvent(event: ReportRoomEvents) {
            when (event) {
                // 举报事件：执行举报操作
                ReportRoomEvents.Report -> coroutineScope.reportRoom(reason, leaveRoom, reportAction)

                // 切换离开房间状态
                ReportRoomEvents.ToggleLeaveRoom -> {
                    leaveRoom = !leaveRoom
                }

                // 更新举报原因
                is ReportRoomEvents.UpdateReason -> {
                    reason = event.reason
                }

                // 清除举报操作状态
                ReportRoomEvents.ClearReportAction -> {
                    reportAction.value = AsyncAction.Uninitialized
                }
            }
        }

        // 返回构建的状态对象
        return ReportRoomState(
            reason = reason,
            leaveRoom = leaveRoom,
            reportAction = reportAction.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 执行举报房间的协程函数
     *
     * 在协程作用域中执行举报操作，并更新操作状态
     *
     * @param reason 举报原因
     * @param shouldLeave 是否在举报后离开房间
     * @param action 用于存储操作状态的 MutableState
     */
    private fun CoroutineScope.reportRoom(
        reason: String,
        shouldLeave: Boolean,
        action: MutableState<AsyncAction<Unit>>
    ) = launch {
        // 检查之前的操作状态，如果是离开房间失败，则不再重复举报
        val previousFailure = action.value as? AsyncAction.Failure
        val shouldReport = previousFailure?.error !is ReportRoom.Exception.LeftRoomFailed

        // 执行举报操作并更新状态
        runUpdatingState(action) {
            reportRoom(
                roomId = roomId,
                shouldReport = shouldReport,
                reason = reason,
                shouldLeave = shouldLeave
            )
        }
    }
}
