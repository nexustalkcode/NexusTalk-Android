/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.pin.model

import androidx.compose.runtime.Immutable

/**
 * PIN 码数字密封接口
 *
 * 表示 PIN 码中单个数字的状态，可以是空或已填充。
 */
@Immutable
sealed interface PinDigit {
    /** 空状态，表示该位置尚未输入数字 */
    data object Empty : PinDigit
    /** 已填充状态
     * @param value 输入的数字字符
     */
    data class Filled(val value: Char) : PinDigit

    /**
     * 转换为文本
     *
     * @return 空字符串或数字字符
     */
    fun toText(): String {
        return when (this) {
            is Empty -> ""
            is Filled -> value.toString()
        }
    }
}
