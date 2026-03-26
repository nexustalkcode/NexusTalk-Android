/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import kotlinx.collections.immutable.ImmutableList

/**
 * 消息导航器接口
 *
 * 定义消息功能模块中的导航操作接口，负责处理各种页面跳转逻辑。
 * 包括事件调试信息、转发、举报、编辑投票、附件预览、房间跳转、线程跳转等。
 *
 * @see EventId 事件ID
 * @see RoomId 房间ID
 * @see ThreadId 线程ID
 * @see UserId 用户ID
 * @see Attachment 附件
 */
interface MessagesNavigator {
    /**
     * 导航到事件调试信息页面
     *
     * @param eventId 事件ID（可选）
     * @param debugInfo 事件调试信息
     */
    fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)

    /**
     * 转发事件
     *
     * @param eventId 要转发的事件ID
     */
    fun forwardEvent(eventId: EventId)

    /**
     * 导航到举报消息页面
     *
     * @param eventId 要举报的事件ID
     * @param senderId 发送者用户ID
     */
    fun navigateToReportMessage(eventId: EventId, senderId: UserId)

    /**
     * 导航到编辑投票页面
     *
     * @param eventId 要编辑的投票事件ID
     */
    fun navigateToEditPoll(eventId: EventId)

    /**
     * 导航到附件预览页面
     *
     * @param attachments 要预览的附件列表
     * @param inReplyToEventId 回复的事件ID（可选）
     */
    fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?)

    /**
     * 导航到房间页面
     *
     * @param roomId 目标房间ID
     * @param eventId 事件ID（可选）
     * @param serverNames 服务器名称列表
     */
    fun navigateToRoom(roomId: RoomId, eventId: EventId?, serverNames: List<String>)

    /**
     * 导航到线程页面
     *
     * @param threadRootId 线程根事件ID
     * @param focusedEventId 聚焦的事件ID（可选）
     */
    fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?)

    /**
     * 关闭当前页面
     */
    fun close()
}
