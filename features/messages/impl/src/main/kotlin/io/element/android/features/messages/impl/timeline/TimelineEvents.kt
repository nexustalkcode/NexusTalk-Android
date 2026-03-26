/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import io.element.android.features.messages.impl.timeline.components.MessageShieldData
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.timeline.Timeline
import kotlin.time.Duration

/**
 * 时间线事件密封接口
 *
 * 定义时间线可能发生的用户交互事件。
 */
sealed interface TimelineEvents {
    /**
     * 滚动完成事件
     *
     * @property firstIndex 第一个可见索引
     */
    data class OnScrollFinished(val firstIndex: Int) : TimelineEvents
    /**
     * 聚焦到事件
     *
     * @property eventId 事件 ID
     * @property debounce 防抖时间
     */
    data class FocusOnEvent(val eventId: EventId, val debounce: Duration = Duration.ZERO) : TimelineEvents
    /** 清除焦点请求状态 */
    data object ClearFocusRequestState : TimelineEvents
    /** 事件聚焦渲染完成 */
    data object OnFocusEventRender : TimelineEvents
    /** 跳转到实时 */
    data object JumpToLive : TimelineEvents

    /** 隐藏盾牌对话框 */
    data object HideShieldDialog : TimelineEvents

    /**
     * 来自时间线项目的事件
     */
    sealed interface EventFromTimelineItem : TimelineEvents

    /**
     * 计算验证用户发送失败
     *
     * @property event 时间线项目事件
     */
    data class ComputeVerifiedUserSendFailure(val event: TimelineItem.Event) : EventFromTimelineItem
    /**
     * 显示盾牌对话框
     *
     * @property messageShieldData 消息盾牌数据
     */
    data class ShowShieldDialog(val messageShieldData: MessageShieldData) : EventFromTimelineItem
    /**
     * 加载更多
     *
     * @property direction 翻页方向
     */
    data class LoadMore(val direction: Timeline.PaginationDirection) : EventFromTimelineItem
    /**
     * 打开线程
     *
     * @property threadRootEventId 线程根事件 ID
     * @property focusedEvent 聚焦事件（可选）
     */
    data class OpenThread(val threadRootEventId: ThreadId, val focusedEvent: EventId?) : EventFromTimelineItem

    /**
     * 导航到前一个或后继房间
     */
    data class NavigateToPredecessorOrSuccessorRoom(val roomId: RoomId) : EventFromTimelineItem

    /**
     * 来自投票项目的事件
     */
    sealed interface TimelineItemPollEvents : EventFromTimelineItem

    /**
     * 选择投票答案
     *
     * @property pollStartId 投票开始事件 ID
     * @property answerId 答案 ID
     */
    data class SelectPollAnswer(
        val pollStartId: EventId,
        val answerId: String
    ) : TimelineItemPollEvents

    /**
     * 结束投票
     *
     * @property pollStartId 投票开始事件 ID
     */
    data class EndPoll(
        val pollStartId: EventId,
    ) : TimelineItemPollEvents

    /**
     * 编辑投票
     *
     * @property pollStartId 投票开始事件 ID
     */
    data class EditPoll(
        val pollStartId: EventId,
    ) : TimelineItemPollEvents
}
