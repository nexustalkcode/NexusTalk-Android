/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 消息编辑器配置 (Message Composer Configuration)
 *
 * 此对象包含消息编辑器（输入框）相关的配置项。
 * 控制消息编辑功能的各项特性，如富文本编辑等。
 */
object MessageComposerConfig {
    /**
     * 是否启用富文本编辑功能。启用后，用户可以在发送消息时使用粗体、斜体、链接等格式化功能。
     * 这基于Matrix协议的 m.emote 消息格式扩展。
     */
    const val ENABLE_RICH_TEXT_EDITING = true
}
