/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 时间线项目反应数据类
 *
 * 包含一个事件的所有反应信息。
 *
 * @property reactions 聚合反应列表
 */
data class TimelineItemReactions(
    val reactions: ImmutableList<AggregatedReaction>
) {
    val highlightedKeys: ImmutableList<String>
        get() = reactions
            .filter { it.isHighlighted }
            .map { it.key }
            .toImmutableList()
}
