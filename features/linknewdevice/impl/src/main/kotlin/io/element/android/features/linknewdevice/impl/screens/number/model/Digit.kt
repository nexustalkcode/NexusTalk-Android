/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number.model

import androidx.compose.runtime.Immutable

@Immutable
/**
 * 校验码输入框中的单个数字槽位。
 */
sealed interface Digit {
    /** 当前位置尚未输入数字。 */
    data object Empty : Digit
    /** 当前位置已经填入数字。 */
    data class Filled(val value: Char) : Digit

    /** 将当前数字槽位转换为文本。 */
    fun toText(): String {
        return when (this) {
            is Empty -> ""
            is Filled -> value.toString()
        }
    }
}
