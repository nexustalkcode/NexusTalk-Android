/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.textcomposer.model.TextEditorState
import io.element.android.libraries.textcomposer.model.aTextEditorStateRich
import io.element.android.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 消息编辑器状态提供者
 *
 * 用于在预览中提供消息编辑器状态的参数提供者，实现了 Compose 的 PreviewParameterProvider 接口。
 * 用于在 Android Studio 的预览功能中显示不同的消息编辑器状态。
 *
 * @see MessageComposerState 消息编辑器状态
 * @see PreviewParameterProvider 预览参数提供者
 */
open class MessageComposerStateProvider : PreviewParameterProvider<MessageComposerState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含消息编辑器状态的序列
     */
    override val values: Sequence<MessageComposerState>
        get() = sequenceOf(
            aMessageComposerState(),
        )
}

/**
 * 创建消息编辑器状态的辅助函数
 *
 * 用于在测试和预览中快速创建消息编辑器状态的工厂函数，提供所有可选参数的默认值。
 *
 * @param textEditorState 文本编辑器状态，默认为富文本状态
 * @param isFullScreen 是否全屏模式
 * @param mode 编辑器模式
 * @param showTextFormatting 是否显示文本格式化工具栏
 * @param showAttachmentSourcePicker 是否显示附件来源选择器
 * @param showEmojiPicker 是否显示表情选择器
 * @param emojibaseStore Emoji数据库存储
 * @param recentEmojis 最近使用的Emoji列表
 * @param canShareLocation 是否可以分享位置
 * @param suggestions 建议列表
 * @param eventSink 事件处理函数
 * @return 消息编辑器状态实例
 */
fun aMessageComposerState(
    textEditorState: TextEditorState = aTextEditorStateRich(),
    isFullScreen: Boolean = false,
    mode: MessageComposerMode = MessageComposerMode.Normal,
    showTextFormatting: Boolean = false,
    showAttachmentSourcePicker: Boolean = false,
    showEmojiPicker: Boolean = false,
    emojibaseStore: io.element.android.emojibasebindings.EmojibaseStore? = null,
    recentEmojis: ImmutableList<String> = persistentListOf(),
    canShareLocation: Boolean = true,
    suggestions: ImmutableList<ResolvedSuggestion> = persistentListOf(),
    eventSink: (MessageComposerEvent) -> Unit = {},
) = MessageComposerState(
    textEditorState = textEditorState,
    isFullScreen = isFullScreen,
    mode = mode,
    showTextFormatting = showTextFormatting,
    showAttachmentSourcePicker = showAttachmentSourcePicker,
    showEmojiPicker = showEmojiPicker,
    emojibaseStore = emojibaseStore,
    recentEmojis = recentEmojis,
    canShareLocation = canShareLocation,
    suggestions = suggestions,
    resolveMentionDisplay = { _, _ -> TextDisplay.Plain },
    resolveAtRoomMentionDisplay = { TextDisplay.Plain },
    eventSink = eventSink,
)
