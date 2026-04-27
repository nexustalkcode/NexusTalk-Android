/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.messages.impl.MessagesView
import io.element.android.features.messages.impl.aMessagesState
import io.element.android.features.messages.impl.messagecomposer.aMessageComposerState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.textcomposer.model.aTextEditorStateMarkdown

/**
 * 带有身份变更预览的消息视图
 *
 * 这是一个预览 Composable 函数，用于在 Android Studio 的预览模式下
 * 显示包含身份变更警告的完整消息界面。
 *
 * 此预览用于开发和调试目的，帮助开发者直观地查看：
 * - 身份变更警告在消息界面中的位置和样式
 * - 警告与消息 composer 的交互效果
 * - 不同身份违规类型界面的响应
 *
 * @param identityChangeState 身份变更状态，通过预览参数提供者传入
 *
 * @see MessagesView 完整消息视图
 * @see IdentityChangeStateView 身份变更状态视图
 * @see IdentityChangeStateProvider 预览参数提供者
 */
@PreviewsDayNight
@Composable
internal fun MessagesViewWithIdentityChangePreview(
    @PreviewParameter(IdentityChangeStateProvider::class) identityChangeState: IdentityChangeState
) = ElementPreview {
    MessagesView(
        state = aMessagesState(
            composerState = aMessageComposerState(
                textEditorState = aTextEditorStateMarkdown(
                    initialText = "",
                    initialFocus = false,
                )
            ),
            identityChangeState = identityChangeState,
        ),
        onBackClick = {},
        onRoomDetailsClick = {},
        onEventContentClick = { _, _ -> false },
        onUserDataClick = {},
        onLinkClick = { _, _ -> },
        onSendLocationClick = {},
        onCreatePollClick = {},
        onJoinCallClick = {},
        onStartVoiceCallClick = {},
        onStartVideoCallClick = {},
        onViewAllPinnedMessagesClick = {},
        knockRequestsBannerView = {}
    )
}
