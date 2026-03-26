/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import android.net.Uri
import io.element.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.textcomposer.model.Suggestion

/**
 * 消息编辑器事件密封接口
 *
 * 定义消息编辑器中的各种用户交互事件类型。
 *
 * @see Uri URI
 * @see MessageComposerMode 消息编辑器模式
 * @see Suggestion 建议
 * @see ResolvedSuggestion 已解析的建议
 */
sealed interface MessageComposerEvent {
    /** 切换全屏状态 */
    data object ToggleFullScreenState : MessageComposerEvent

    /** 发送消息 */
    data object SendMessage : MessageComposerEvent

    /**
     * 发送URI
     *
     * @property uri 要发送的URI
     */
    data class SendUri(val uri: Uri) : MessageComposerEvent

    /** 关闭特殊模式 */
    data object CloseSpecialMode : MessageComposerEvent

    /**
     * 设置编辑器模式
     *
     * @property composerMode 编辑器模式
     */
    data class SetMode(val composerMode: MessageComposerMode) : MessageComposerEvent

    /** 添加附件 */
    data object AddAttachment : MessageComposerEvent

    /** 关闭附件菜单 */
    data object DismissAttachmentMenu : MessageComposerEvent

    /**
     * 选择附件来源密封接口
     */
    sealed interface PickAttachmentSource : MessageComposerEvent {
        /** 从相册选择 */
        data object FromGallery : PickAttachmentSource
        /** 从文件选择 */
        data object FromFiles : PickAttachmentSource
        /** 拍照 */
        data object PhotoFromCamera : PickAttachmentSource
        /** 录像 */
        data object VideoFromCamera : PickAttachmentSource
        /** 位置 */
        data object Location : PickAttachmentSource
        /** 投票 */
        data object Poll : PickAttachmentSource
    }

    /**
     * 切换文本格式化
     *
     * @property enabled 是否启用
     */
    data class ToggleTextFormatting(val enabled: Boolean) : MessageComposerEvent

    /**
     * 错误事件
     *
     * @property error 错误信息
     */
    data class Error(val error: Throwable) : MessageComposerEvent

    /**
     * 输入提示事件
     *
     * @property isTyping 是否正在输入
     */
    data class TypingNotice(val isTyping: Boolean) : MessageComposerEvent

    /**
     * 收到建议事件
     *
     * @property suggestion 建议内容
     */
    data class SuggestionReceived(val suggestion: Suggestion?) : MessageComposerEvent

    /**
     * 插入建议事件
     *
     * @property resolvedSuggestion 已解析的建议
     */
    data class InsertSuggestion(val resolvedSuggestion: ResolvedSuggestion) : MessageComposerEvent

    /** 保存草稿 */
    data object SaveDraft : MessageComposerEvent

    /** 切换表情选择器 */
    data object ToggleEmojiPicker : MessageComposerEvent

    /**
     * 插入表情事件
     *
     * @property emoji 表情符号
     */
    data class InsertEmoji(val emoji: String) : MessageComposerEvent
}
