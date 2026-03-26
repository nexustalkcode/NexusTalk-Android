/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import androidx.compose.runtime.Immutable

/**
 * 时间线项目分组位置密封接口
 *
 * 用于渲染同一发送者的连续事件的属性。
 * 决定消息气泡的样式（是否显示头像、名称等）。
 *
 * 时间线中的可能序列：
 *
 * 仅一个事件：
 * - [None]
 *
 * 两个事件：
 * - [First]
 * - [Last]
 *
 * 多个事件：
 * - [First]
 * - [Middle]（如需要可重复）
 * - [Last]
 */
@Immutable
sealed interface TimelineItemGroupPosition {
    /**
     * The event is part of a group of events from the same sender and is the first sent Event.
     */
    data object First : TimelineItemGroupPosition

    /**
     * The event is part of a group of events from the same sender and is neither the first nor the last sent Event.
     */
    data object Middle : TimelineItemGroupPosition

    /**
     * The event is part of a group of events from the same sender and is the last sent Event.
     */
    data object Last : TimelineItemGroupPosition

    /**
     * The event is not part of a group of events. Sender of previous event is different, and sender of next event is different.
     */
    data object None : TimelineItemGroupPosition

    /**
     * Return true if the previous sender of the event is a different sender.
     */
    fun isNew(): Boolean = when (this) {
        First, None -> true
        else -> false
    }
}
