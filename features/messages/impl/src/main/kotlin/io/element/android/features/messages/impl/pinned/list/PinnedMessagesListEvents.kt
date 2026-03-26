/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.timeline.model.TimelineItem

/**
 * 固定消息列表事件密封接口
 *
 * 定义了固定消息列表界面中用户交互产生的事件类型。
 * 用于处理用户在固定消息列表中的各种操作。
 *
 * @see TimelineItemAction 时间线操作类型
 * @see TimelineItem.Event 时间线事件
 */
sealed interface PinnedMessagesListEvents {
    /**
     * 处理时间线操作事件
     *
     * 当用户从操作列表中选择某个操作时触发。
     * 包含用户选择的操作类型和对应的目标事件。
     *
     * @property action 用户选择的时间线操作（如查看源码、转发、取消置顶等）
     * @property event 目标时间线事件，即用户长按操作的事件
     */
    data class HandleAction(val action: TimelineItemAction, val event: TimelineItem.Event) : PinnedMessagesListEvents
}
