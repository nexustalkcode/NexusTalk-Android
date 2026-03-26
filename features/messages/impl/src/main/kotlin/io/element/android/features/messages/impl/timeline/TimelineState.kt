/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import androidx.compose.runtime.Immutable
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.element.android.features.messages.impl.timeline.components.MessageShieldData
import io.element.android.features.messages.impl.timeline.model.NewEventState
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.element.android.features.messages.impl.typing.TypingNotificationState
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.room.tombstone.PredecessorRoom
import io.element.android.libraries.matrix.api.timeline.Timeline
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration

/**
 * 时间线状态数据类
 *
 * 表示消息时间线的完整状态，包含时间线项目、房间信息、焦点请求状态等信息。
 *
 * @property timelineItems 时间线项目列表
 * @property timelineRoomInfo 时间线房间信息
 * @property timelineMode 时间线模式
 * @property renderReadReceipts 是否渲染已读回执
 * @property newEventState 新事件状态
 * @property isLive 是否为实时模式
 * @property focusRequestState 焦点请求状态
 * @property messageShieldDialogData 消息盾牌对话框数据（如果不为 null，将在对话框中渲染信息）
 * @property resolveVerifiedUserSendFailureState 解决验证用户发送失败状态
 * @property displayThreadSummaries 是否显示线程摘要
 * @property eventSink 事件处理函数
 */
data class TimelineState(
    val timelineItems: ImmutableList<TimelineItem>,
    val timelineRoomInfo: TimelineRoomInfo,
    val timelineMode: Timeline.Mode,
    val renderReadReceipts: Boolean,
    val newEventState: NewEventState,
    val isLive: Boolean,
    val focusRequestState: FocusRequestState,
    // If not null, info will be rendered in a dialog
    val messageShieldDialogData: MessageShieldData?,
    val resolveVerifiedUserSendFailureState: ResolveVerifiedUserSendFailureState,
    val displayThreadSummaries: Boolean,
    val eventSink: (TimelineEvents) -> Unit,
) {
    private val lastTimelineEvent = timelineItems.firstOrNull { it is TimelineItem.Event } as? TimelineItem.Event
    val latestCallNotifyEventId = timelineItems
        .firstOrNull { item ->
            item is TimelineItem.Event &&
                item.content is TimelineItemRtcNotificationContent &&
                item.eventId != null
        }
        ?.let { (it as TimelineItem.Event).eventId }
    /** 是否有任何事件 */
    val hasAnyEvent = lastTimelineEvent != null
    /** 焦点事件 ID */
    val focusedEventId = focusRequestState.eventId()

    /**
     * 检查是否为最后一条发出的消息
     *
     * @param uniqueId 唯一 ID
     * @return 是否为最后一条发出的消息
     */
    fun isLastOutgoingMessage(uniqueId: UniqueId): Boolean {
        return isLive && lastTimelineEvent != null && lastTimelineEvent.isMine && lastTimelineEvent.id == uniqueId
    }

    fun isLatestCallNotify(eventId: EventId?): Boolean {
        return eventId != null && eventId == latestCallNotifyEventId
    }
}

/**
 * 焦点请求状态密封接口
 *
 * 表示事件焦点请求的不同状态。
 */
@Immutable
sealed interface FocusRequestState {
    /** 无状态 */
    data object None : FocusRequestState
    /**
     * 请求中状态
     *
     * @property eventId 事件 ID
     * @property debounce 防抖时间
     */
    data class Requested(val eventId: EventId, val debounce: Duration) : FocusRequestState
    /**
     * 加载中状态
     *
     * @property eventId 事件 ID
     */
    data class Loading(val eventId: EventId) : FocusRequestState
    /**
     * 成功状态
     *
     * @property eventId 事件 ID
     * @property index 索引（默认为 -1）
     * @property rendered 是否已渲染（用于判断事件是否已渲染）
     */
    data class Success(
        val eventId: EventId,
        val index: Int = -1,
        // This is used to know if the event has been rendered yet.
        val rendered: Boolean = false,
    ) : FocusRequestState {
        /** 是否已索引 */
        val isIndexed
            get() = index != -1
    }

    /**
     * 失败状态
     *
     * @property throwable 异常
     */
    data class Failure(val throwable: Throwable) : FocusRequestState

    /**
     * 获取事件 ID
     *
     * @return 事件 ID（如果适用）
     */
    fun eventId(): EventId? {
        return when (this) {
            is Requested -> eventId
            is Loading -> eventId
            is Success -> eventId
            else -> null
        }
    }
}

/**
 * 时间线房间信息数据类
 *
 * 表示时间线所需的房间信息。
 *
 * @property isDm 是否为直接消息
 * @property name 房间名称
 * @property userHasPermissionToSendMessage 用户是否有发送消息的权限
 * @property userHasPermissionToSendReaction 用户是否有发送反应的权限
 * @property roomCallState 房间通话状态
 * @property pinnedEventIds 固定事件 ID 列表
 * @property typingNotificationState 打字通知状态
 * @property predecessorRoom 前一个房间
 */
data class TimelineRoomInfo(
    val isDm: Boolean,
    val name: String?,
    val userHasPermissionToSendMessage: Boolean,
    val userHasPermissionToSendReaction: Boolean,
    val roomCallState: RoomCallState,
    val pinnedEventIds: ImmutableList<EventId>,
    val typingNotificationState: TypingNotificationState,
    val predecessorRoom: PredecessorRoom?,
)
