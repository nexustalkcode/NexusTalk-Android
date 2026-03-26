/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.customreaction

import io.element.android.emojibasebindings.EmojibaseStore
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 自定义反应状态数据类
 *
 * @property target 自定义反应的目标事件
 * @property selectedEmoji 已选择的 emoji 集合
 * @property recentEmojis 最近使用的 emoji 列表
 * @property eventSink 事件处理函数
 */
data class CustomReactionState(
    val target: Target,
    val selectedEmoji: ImmutableSet<String>,
    val recentEmojis: ImmutableList<String>,
    val eventSink: (CustomReactionEvents) -> Unit,
) {
    sealed interface Target {
        data object None : Target
        data class Loading(val event: TimelineItem.Event) : Target
        data class Success(
            val event: TimelineItem.Event,
            val emojibaseStore: EmojibaseStore,
        ) : Target
    }
}
