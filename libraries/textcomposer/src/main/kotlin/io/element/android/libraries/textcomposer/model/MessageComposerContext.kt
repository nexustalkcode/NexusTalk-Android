/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.model

/**
 * 消息 composer 上下文契约。
 *
 * 这里抽出的是可被多个 feature 共享的最小状态，只关心当前 composer 的模式，
 * 避免 location、poll 等功能为了读取 composerMode 反向依赖整个 messages feature。
 */
interface MessageComposerContext {
    val composerMode: MessageComposerMode
}
