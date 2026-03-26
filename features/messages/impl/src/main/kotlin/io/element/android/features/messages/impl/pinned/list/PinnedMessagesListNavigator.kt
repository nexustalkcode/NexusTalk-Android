/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo

/**
 * 固定消息列表导航器接口
 *
 * 定义固定消息列表页面中的导航操作接口。
 *
 * @see EventId 事件ID
 * @see TimelineItemDebugInfo 时间线项目调试信息
 */
interface PinnedMessagesListNavigator {
    /**
     * 在时间线中查看消息
     *
     * @param eventId 事件ID
     */
    fun viewInTimeline(eventId: EventId)

    /**
     * 导航到事件调试信息页面
     *
     * @param eventId 事件ID
     * @param debugInfo 调试信息
     */
    fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)

    /**
     * 转发事件
     *
     * @param eventId 事件ID
     */
    fun forwardEvent(eventId: EventId)
}
