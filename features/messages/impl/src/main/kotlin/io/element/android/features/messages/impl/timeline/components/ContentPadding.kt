/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

/**
 * 内容内边距枚举
 *
 * 定义消息气泡内容的内边距类型。
 * 不同类型的内容（如文本、图片、带标题的图片）需要不同的内边距样式。
 */
enum class ContentPadding {
    Textual,
    Media,
    CaptionedMedia
}
