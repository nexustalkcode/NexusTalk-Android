/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api

import io.element.android.libraries.textcomposer.model.MessageComposerMode

/**
 * 消息撰写器上下文的可提升状态接口
 *
 * 这是一个可被提升（hoist）的状态接口，主要用于在其他Presenter中获取
 * 消息撰写器的状态信息，例如：撰写器是否在回复线程中、是否正在编辑消息等。
 *
 * @see MessageComposerMode 消息撰写器模式
 */
interface MessageComposerContext {
    /** 消息撰写器当前模式 */
    val composerMode: MessageComposerMode
}
