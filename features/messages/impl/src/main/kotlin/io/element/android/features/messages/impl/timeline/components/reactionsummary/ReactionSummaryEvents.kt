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

/**
 * reaction summary 底部弹层可能触发的事件。
 */
sealed interface ReactionSummaryEvents {
    /** 清空当前展示目标并关闭弹层。 */
    data object Clear : ReactionSummaryEvents
    /** 展示指定事件和 reaction key 的汇总详情。 */
    data class ShowReactionSummary(val eventId: EventId, val reactions: List<AggregatedReaction>, val selectedKey: String) : ReactionSummaryEvents
}
