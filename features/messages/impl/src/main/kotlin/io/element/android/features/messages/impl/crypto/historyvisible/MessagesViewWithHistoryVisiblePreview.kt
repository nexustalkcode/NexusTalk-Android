/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

import androidx.compose.runtime.Composable
import io.element.android.features.messages.impl.MessagesView
import io.element.android.features.messages.impl.aMessagesState
import io.element.android.features.messages.impl.messagecomposer.aMessageComposerState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.textcomposer.model.aTextEditorStateMarkdown

/**
 * 带有历史可见性预览的消息视图
 *
 * 这是一个预览 Composable 函数，用于在 Android Studio 的预览模式下
 * 显示包含历史可见性警告的完整消息界面。
 *
 * 此预览用于开发和调试目的，帮助开发者直观地查看：
 * - 历史可见性警告在消息界面中的位置和样式
 * - 警告与消息 composer 的交互效果
 * - 不同状态下界面的响应
 *
 * 预览配置：
 * - 使用空的消息composer状态
 * - 启用历史可见性警告（showAlert = true）
 *
 * @see MessagesView 完整消息视图
 * @see HistoryVisibleStateView 历史可见性状态视图
 * @see HistoryVisibleStateProvider 预览参数提供者
 */
@PreviewsDayNight
@Composable
internal fun MessagesViewWithHistoryVisiblePreview() = ElementPreview {
    MessagesView(
        state = aMessagesState(
            composerState = aMessageComposerState(
                textEditorState = aTextEditorStateMarkdown(
                    initialText = "",
                    initialFocus = false,
                )
            ),
            historyVisibleState = aHistoryVisibleState(showAlert = true),
        ),
        onBackClick = {},
        onRoomDetailsClick = {},
        onEventContentClick = { _, _ -> false },
        onUserDataClick = {},
        onLinkClick = { _, _ -> },
        onSendLocationClick = {},
        onCreatePollClick = {},
        onJoinCallClick = {},
        onViewAllPinnedMessagesClick = {},
        knockRequestsBannerView = {}
    )
}
