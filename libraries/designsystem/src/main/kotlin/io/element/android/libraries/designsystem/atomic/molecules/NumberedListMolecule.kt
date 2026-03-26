/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 编号列表分子组件
 *
 * 用于显示带编号的列表项。
 * 每个列表项包含一个圆形编号框和对应的文本内容。
 * 适用于步骤说明、条款列表等需要编号的场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.modifiers.squareSize
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 编号列表项组件
 *
 * 创建一个带编号的列表项。
 * 编号显示在圆形边框内，文本内容在编号右侧。
 *
 * @param index Int 编号数字，从 1 开始
 * @param text AnnotatedString 列表项文本内容，支持富文本
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontBodyMdRegular] 文本样式
 * @see [ElementTheme.colors.textSecondary] 编号和文字颜色
 *
 * @example
 * ```kotlin
 * NumberedListMolecule(
 *     index = 1,
 *     text = "第一步操作说明".toAnnotatedString()
 * )
 * ```
 */
@Composable
fun NumberedListMolecule(
    index: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ItemNumber(index = index)
        Text(text = text, style = ElementTheme.typography.fontBodyMdRegular, color = ElementTheme.colors.textPrimary)
    }
}

/**
 * 编号圆框组件
 *
 * NumberedListMolecule 的内部组件，用于显示带边框的圆形编号。
 *
 * @param index Int 编号数字
 */
@Composable
private fun ItemNumber(
    index: Int,
) {
    val color = ElementTheme.colors.textSecondary
    Box(
        modifier = Modifier
            .border(1.dp, color, CircleShape)
            .squareSize()
    ) {
        Text(
            modifier = Modifier.padding(1.5.dp),
            text = index.toString(),
            style = ElementTheme.typography.fontBodySmRegular,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}
