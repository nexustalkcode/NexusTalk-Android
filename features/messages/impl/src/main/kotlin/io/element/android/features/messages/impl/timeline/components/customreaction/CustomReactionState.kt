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
 * 自定义 reaction 底部弹层展示状态。
 *
 * @property target 当前自定义 reaction 的目标事件和加载状态。
 * @property selectedEmoji 当前事件上已被当前用户选中的 emoji 集合。
 * @property recentEmojis 最近使用的 emoji 列表。
 * @property eventSink 页面事件分发函数。
 */
data class CustomReactionState(
    val target: Target,
    val selectedEmoji: ImmutableSet<String>,
    val recentEmojis: ImmutableList<String>,
    val eventSink: (CustomReactionEvents) -> Unit,
) {
    /**
     * 自定义 reaction 目标的加载状态。
     */
    sealed interface Target {
        /** 当前没有展示任何自定义 reaction 面板。 */
        data object None : Target

        /** 正在为指定事件准备 emojibase 数据。 */
        data class Loading(val event: TimelineItem.Event) : Target

        /** 已经准备好 emojibase 数据，可以显示完整选择器。 */
        data class Success(
            val event: TimelineItem.Event,
            val emojibaseStore: EmojibaseStore,
        ) : Target
    }
}
