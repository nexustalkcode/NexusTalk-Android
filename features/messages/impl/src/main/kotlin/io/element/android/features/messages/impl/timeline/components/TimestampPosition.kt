/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

/**
 * 时间戳位置枚举
 *
 * 定义时间戳在消息气泡中的显示位置。
 */
enum class TimestampPosition {
    /**
     * 时间戳覆盖在时间线事件内容上（例如图片）。
     */
    Overlay,

    /**
     * 时间戳与时间线事件内容对齐（例如文本）。
     */
    Aligned,

    /**
     * 时间戳始终显示在时间线事件内容下方（例如投票）。
     */
    Below;

    companion object {
        /**
         * 时间线事件内容的默认时间戳位置。
         */
        val Default: TimestampPosition = Aligned
    }
}
