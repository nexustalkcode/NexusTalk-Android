/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.utils.messagesummary

import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent

/**
 * 消息摘要格式化器接口
 *
 * 负责将消息内容格式化为摘要文本，用于在列表等视图中显示消息预览。
 * 不同类型的消息内容会被格式化为不同的摘要文本。
 *
 * @see DefaultMessageSummaryFormatter 默认实现
 * @see TimelineItem 时间线项
 * @see TimelineItemEventContent 时间线事件内容
 */
interface MessageSummaryFormatter {
    fun format(event: TimelineItem.Event): String {
        return format(event.content)
    }
    fun format(content: TimelineItemEventContent): String
}
