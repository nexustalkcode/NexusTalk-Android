/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.pin.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * PIN 码输入数据类
 *
 * 表示完整的 PIN 码输入状态，包含多个 PIN 数字。
 *
 * @param digits PIN 数字列表
 */
data class PinEntry(
    val digits: ImmutableList<PinDigit>,
) {
    companion object {
        /**
         * 创建空的 PIN 码输入
         *
         * @param size PIN 码长度
         * @return 空的 PinEntry 实例
         */
        fun createEmpty(size: Int): PinEntry {
            val digits = List(size) { PinDigit.Empty }
            return PinEntry(
                digits = digits.toImmutableList()
            )
        }
    }

    /** PIN 码长度 */
    val size = digits.size

    /**
     * 使用给定文本填充 PIN 码
     *
     * 填充前几位数字，不能超过 PinEntry 的大小
     * 保留末尾的空数字
     *
     * @param text 要填充的文本
     * @return 新的 PinEntry
     */
    fun fillWith(text: String): PinEntry {
        val newDigits = MutableList<PinDigit>(size) { PinDigit.Empty }
        text.forEachIndexed { index, char ->
            if (index < size && char.isDigit()) {
                newDigits[index] = PinDigit.Filled(char)
            }
        }
        return copy(digits = newDigits.toImmutableList())
    }

    /**
     * 删除最后一个已填充的数字
     *
     * @return 新的 PinEntry
     */
    fun deleteLast(): PinEntry {
        if (isEmpty()) return this
        val newDigits = digits.toMutableList()
        newDigits.indexOfLast { it is PinDigit.Filled }.also { lastFilled ->
            newDigits[lastFilled] = PinDigit.Empty
        }
        return copy(digits = newDigits.toImmutableList())
    }

    /**
     * 添加一个数字到第一个空位
     *
     * @param digit 要添加的数字字符
     * @return 新的 PinEntry
     */
    fun addDigit(digit: Char): PinEntry {
        if (isComplete()) return this
        val newDigits = digits.toMutableList()
        newDigits.indexOfFirst { it is PinDigit.Empty }.also { firstEmpty ->
            newDigits[firstEmpty] = PinDigit.Filled(digit)
        }
        return copy(digits = newDigits.toImmutableList())
    }

    /**
     * 清空所有输入
     *
     * @return 新的空 PinEntry
     */
    fun clear(): PinEntry {
        return createEmpty(size)
    }

    /**
     * 检查是否已填写完成
     *
     * @return true 如果所有位置都已填充
     */
    fun isComplete(): Boolean {
        return digits.all { it is PinDigit.Filled }
    }

    /**
     * 检查是否为空
     *
     * @return true 如果所有位置都为空
     */
    fun isEmpty(): Boolean {
        return digits.all { it is PinDigit.Empty }
    }

    /**
     * 转换为文本字符串
     *
     * @return PIN 码的文本表示
     */
    fun toText(): String {
        return digits.joinToString("") {
            it.toText()
        }
    }
}
