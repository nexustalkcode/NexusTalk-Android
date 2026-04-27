/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 固定长度的数字输入模型。
 *
 * @property digits 当前所有数字槽位。
 */
data class Number(
    val digits: ImmutableList<Digit>,
) {
    companion object {
        /** 创建指定长度的空数字模型。 */
        fun createEmpty(size: Int): Number {
            val digits = List(size) { Digit.Empty }
            return Number(
                digits = digits.toImmutableList()
            )
        }
    }

    val size = digits.size

    /**
     * 用给定文本填充前几个数字槽位。
     *
     * 只接受数字字符，且不会超过当前模型的固定长度。
     */
    fun fillWith(text: String): Number {
        val newDigits = MutableList<Digit>(size) { Digit.Empty }
        text.forEachIndexed { index, char ->
            if (index < size && char.isDigit()) {
                newDigits[index] = Digit.Filled(char)
            }
        }
        return copy(digits = newDigits.toImmutableList())
    }

    /** 返回当前已经填充的数字个数。 */
    fun length(): Int {
        return digits.count { it is Digit.Filled }
    }

    /** 把当前数字模型拼接成纯文本。 */
    fun toText(): String {
        return digits.joinToString("") {
            it.toText()
        }
    }

    /** 判断当前数字模型是否已经全部填满。 */
    fun isComplete(): Boolean {
        return digits.all { it is Digit.Filled }
    }
}
