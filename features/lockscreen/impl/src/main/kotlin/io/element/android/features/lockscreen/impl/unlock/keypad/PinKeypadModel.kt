/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock.keypad

import androidx.compose.runtime.Immutable

/**
 * PIN 键盘模型密封接口
 *
 * 定义 PIN 键盘上的按键类型。
 */
@Immutable
sealed interface PinKeypadModel {
    /** 空位（占位符） */
    data object Empty : PinKeypadModel
    /** 删除键 */
    data object Back : PinKeypadModel
    /** 数字键
     * @param number 数字字符
     */
    data class Number(val number: Char) : PinKeypadModel
}
