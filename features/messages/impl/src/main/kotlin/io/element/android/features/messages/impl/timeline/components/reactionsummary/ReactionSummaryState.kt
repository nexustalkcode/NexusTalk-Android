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
 * reaction summary 底部弹层展示状态。
 *
 * @property target 当前选中的 reaction 汇总目标；为 `null` 时表示弹层关闭。
 * @property eventSink 页面事件分发函数。
 */
data class ReactionSummaryState(
    val target: Summary?,
    val eventSink: (ReactionSummaryEvents) -> Unit
) {
    /**
     * 当前 reaction 汇总目标。
     *
     * @property reactions 当前事件的聚合 reaction 列表。
     * @property selectedKey 当前默认高亮的 reaction key。
     * @property selectedEventId 当前对应的事件 ID。
     */
    data class Summary(
        val reactions: ImmutableList<AggregatedReaction>,
        val selectedKey: String,
        val selectedEventId: EventId
    )
}
