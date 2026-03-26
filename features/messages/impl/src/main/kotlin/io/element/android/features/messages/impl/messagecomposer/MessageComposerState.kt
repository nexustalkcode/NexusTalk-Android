/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import androidx.compose.runtime.Stable
import io.element.android.emojibasebindings.EmojibaseStore
import io.element.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.textcomposer.model.TextEditorState
import io.element.android.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList

internal const val MAX_MESSAGE_LENGTH = 500

/**
 * 消息编辑器状态数据类
 *
 * 表示消息编辑器的完整状态，包含文本编辑状态、模式、附件选择等功能。
 *
 * @property textEditorState 文本编辑器状态
 * @property isFullScreen 是否全屏模式
 * @property mode 编辑器模式（普通、编辑、回复等）
 * @property showAttachmentSourcePicker 是否显示附件来源选择器
 * @property showTextFormatting 是否显示文本格式工具栏
 * @property canShareLocation 是否可以分享位置
 * @property suggestions 提及建议列表
 * @property resolveMentionDisplay 解析提及显示的函数
 * @property resolveAtRoomMentionDisplay 解析@房间提及显示的函数
 * @property eventSink 事件处理函数
 */
@Stable
data class MessageComposerState(
    val textEditorState: TextEditorState,
    val isFullScreen: Boolean,
    val mode: MessageComposerMode,
    val showAttachmentSourcePicker: Boolean,
    val showTextFormatting: Boolean,
    val showEmojiPicker: Boolean,
    val emojibaseStore: EmojibaseStore?,
    val recentEmojis: ImmutableList<String>,
    val canShareLocation: Boolean,
    val suggestions: ImmutableList<ResolvedSuggestion>,
    val resolveMentionDisplay: (String, String) -> TextDisplay,
    val resolveAtRoomMentionDisplay: () -> TextDisplay,
    val eventSink: (MessageComposerEvent) -> Unit,
)
