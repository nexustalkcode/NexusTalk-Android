/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.reactionsummary

import io.element.android.features.messages.impl.timeline.model.AggregatedReaction
import io.element.android.libraries.matrix.api.core.EventId
import kotlinx.collections.immutable.ImmutableList

/**
 * 反应摘要状态数据类
 *
 * @property target 反应摘要目标
 * @property eventSink 事件处理函数
 */
data class ReactionSummaryState(
    val target: Summary?,
    val eventSink: (ReactionSummaryEvents) -> Unit
) {
    data class Summary(
        val reactions: ImmutableList<AggregatedReaction>,
        val selectedKey: String,
        val selectedEventId: EventId
    )
}
