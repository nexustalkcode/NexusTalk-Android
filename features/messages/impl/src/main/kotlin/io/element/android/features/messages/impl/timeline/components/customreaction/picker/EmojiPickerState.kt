/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.customreaction.picker

import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import io.element.android.emojibasebindings.Emoji
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import kotlinx.collections.immutable.ImmutableList

/**
 * emoji picker 展示状态。
 *
 * @property categories emoji 分类列表。
 * @property allEmojis 全量 emoji 列表。
 * @property searchQuery 搜索输入框状态。
 * @property isSearchActive 当前是否处于搜索模式。
 * @property searchResults 搜索结果状态。
 * @property eventSink 页面事件分发函数。
 */
@Immutable
data class EmojiPickerState(
    val categories: ImmutableList<EmojiCategory>,
    val allEmojis: ImmutableList<Emoji>,
    val searchQuery: TextFieldState,
    val isSearchActive: Boolean,
    val searchResults: SearchBarResultState<ImmutableList<Emoji>>,
    val eventSink: (EmojiPickerEvents) -> Unit,
)

/**
 * 单个 emoji 分类。
 *
 * @property titleId 分类标题资源。
 * @property icon 分类图标。
 * @property emojis 该分类下的 emoji 列表。
 */
data class EmojiCategory(
    @StringRes val titleId: Int,
    val icon: IconSource,
    val emojis: ImmutableList<Emoji>,
)
