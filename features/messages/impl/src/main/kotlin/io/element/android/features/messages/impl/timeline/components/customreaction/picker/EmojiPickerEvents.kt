/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.customreaction.picker

/**
 * emoji picker 可能触发的交互事件。
 */
sealed interface EmojiPickerEvents {
    /** 切换搜索模式是否激活。 */
    data class ToggleSearchActive(val isActive: Boolean) : EmojiPickerEvents
}
