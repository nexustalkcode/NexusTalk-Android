/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 带标签文本分子组件
 *
 * 用于显示标签和对应文本的垂直组合。
 * 标签使用次要文字颜色和较小字号，文本使用主要文字颜色和标准字号。
 * 常用于表单字段、详情列表等需要标签说明的场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 带标签文本组件
 *
 * 创建一个垂直排列的标签和文本组合。
 * 标签显示在文本上方，使用次要文字颜色和较小字号。
 * 支持特殊无障碍处理，可逐字符朗读文本内容。
 *
 * @param label String 标签文本内容，显示在文本上方
 * @param text String 主体文本内容，显示在标签下方
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param spellText Boolean 是否逐字符朗读文本（用于无障碍），默认为 false。设置为 true 时，屏幕阅读器会将文本逐字符朗读，而非整体单词
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontBodySmRegular] 标签文本样式
 * @see [ElementTheme.typography.fontBodyMdRegular] 主体文本样式
 * @see [ElementTheme.colors.textSecondary] 标签文字颜色
 * @see [ElementTheme.colors.textPrimary] 主体文字颜色
 *
 * @example
 * ```kotlin
 * TextWithLabelMolecule(
 *     label = "用户名",
 *     text = "user123"
 * )
 * ```
 */
@Composable
fun TextWithLabelMolecule(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    spellText: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = ElementTheme.typography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary,
        )
        Text(
            modifier = Modifier.semantics {
                if (spellText) {
                    contentDescription = text.toList().joinToString()
                }
            },
            text = text,
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textPrimary,
        )
    }
}
