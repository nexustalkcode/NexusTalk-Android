/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.timeline.TimelineProvider
import io.element.android.libraries.matrix.api.timeline.getActiveTimeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 转发消息 Presenter
 *
 * 负责处理消息转发功能的业务逻辑和状态管理。
 * 管理消息的转发操作和状态更新。
 *
 * @property eventId 要转发的事件 ID
 * @property timelineProvider 时间线提供者
 * @property sessionCoroutineScope 会话级别的协程作用域
 */
@AssistedInject
class ForwardMessagesPresenter(
    @Assisted eventId: String,
    @Assisted private val timelineProvider: TimelineProvider,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) : Presenter<ForwardMessagesState> {
    private val eventId: EventId = EventId(eventId)

    /**
     * 工厂接口
     *
     * 用于创建 ForwardMessagesPresenter 实例的工厂方法。
     */
    @AssistedFactory
    fun interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param eventId 要转发的事件 ID
         * @param timelineProvider 时间线提供者
         * @return ForwardMessagesPresenter 实例
         */
        fun create(eventId: String, timelineProvider: TimelineProvider): ForwardMessagesPresenter
    }

    private val forwardingActionState: MutableState<AsyncAction<List<RoomId>>> = mutableStateOf(AsyncAction.Uninitialized)

    /**
     * 处理房间选择事件
     *
     * @param roomIds 目标房间 ID 列表
     */
    fun onRoomSelected(roomIds: List<RoomId>) {
        sessionCoroutineScope.forwardEvent(eventId, roomIds)
    }

    /**
     * 生成界面状态
     *
     * @return ForwardMessagesState 转发消息状态
     */
    @Composable
    override fun present(): ForwardMessagesState {
        /**
         * 处理用户事件
         *
         * @param event 转发消息事件
         */
        fun handleEvent(event: ForwardMessagesEvents) {
            when (event) {
                ForwardMessagesEvents.ClearError -> forwardingActionState.value = AsyncAction.Uninitialized
            }
        }

        return ForwardMessagesState(
            forwardAction = forwardingActionState.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 执行转发事件
     *
     * @param eventId 要转发的事件 ID
     * @param roomIds 目标房间 ID 列表
     */
    private fun CoroutineScope.forwardEvent(
        eventId: EventId,
        roomIds: List<RoomId>,
    ) = launch {
        suspend {
            timelineProvider.getActiveTimeline().forwardEvent(eventId, roomIds)
                .onFailure {
                    Timber.e(it, "Error while forwarding event")
                }
                .getOrThrow()
            roomIds
        }.runCatchingUpdatingState(forwardingActionState)
    }
}
