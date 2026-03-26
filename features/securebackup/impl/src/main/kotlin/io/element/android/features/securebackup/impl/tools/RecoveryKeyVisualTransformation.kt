/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.tools

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * 恢复密钥视觉转换器
 *
 * 用于在文本字段中格式化显示恢复密钥。
 * 将恢复密钥按每4个字符为一组进行分组显示，便于用户阅读。
 */
class RecoveryKeyVisualTransformation : VisualTransformation {
    /**
     * 过滤并转换文本
     *
     * 将恢复密钥按4个字符分组，添加空格分隔。
     *
     * @param text 原始文本
     * @return 转换后的文本和偏移映射
     */
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(
                text.text
                    .chunked(4)
                    .joinToString(separator = " ")
            ),
            offsetMapping = RecoveryKeyOffsetMapping(text.text),
        )
    }

    /**
     * 恢复密钥偏移映射
     *
     * 处理原始文本和转换后文本之间的偏移映射关系。
     *
     * @property text 原始文本
     */
    class RecoveryKeyOffsetMapping(private val text: String) : OffsetMapping {
        /**
         * 将原始偏移转换为转换后的偏移
         *
         * @param offset 原始文本中的偏移
         * @return 转换后文本中的偏移
         */
        override fun originalToTransformed(offset: Int): Int {
            if (offset == 0) return 0
            val numberOfChunks = offset / 4
            return if (offset == text.length && offset % 4 == 0) {
                offset + numberOfChunks - 1
            } else {
                offset + numberOfChunks
            }
        }

        /**
         * 将转换后的偏移转换为原始偏移
         *
         * @param offset 转换后文本中的偏移
         * @return 原始文本中的偏移
         */
        override fun transformedToOriginal(offset: Int): Int {
            val numberOfChunks = offset / 5
            return offset - numberOfChunks
        }
    }
}
