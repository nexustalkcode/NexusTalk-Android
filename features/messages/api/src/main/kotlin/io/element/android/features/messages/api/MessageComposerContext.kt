/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api

/**
 * 消息输入框上下文类型别名。
 *
 * 复用文本编辑器库中的 [io.element.android.libraries.textcomposer.model.MessageComposerContext] 定义，
 * 以便 feature 层对外暴露稳定的 API 入口。
 */
typealias MessageComposerContext = io.element.android.libraries.textcomposer.model.MessageComposerContext
