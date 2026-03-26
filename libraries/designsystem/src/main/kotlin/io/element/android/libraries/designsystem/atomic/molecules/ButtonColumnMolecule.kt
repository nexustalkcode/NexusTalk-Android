/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 按钮列分子组件
 *
 * 用于垂直排列多个按钮的布局组件。
 * 按钮在列中居中对齐，间距为 16dp。
 * 常用于表单提交、确认对话框等需要垂直排列按钮的场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.OutlinedButton
import io.element.android.libraries.designsystem.theme.components.TextButton

/**
 * 按钮列组件
 *
 * 创建一个垂直排列按钮的列布局。
 * 所有按钮宽度填充父容器，水平居中对齐。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param content @Composable ColumnScope.() -> Unit 按钮内容区域，可以放置多个 Button、OutlinedButton 或 TextButton
 *
 * @return Unit
 *
 * @see [Button] 实心按钮组件
 * @see [OutlinedButton] 边框按钮组件
 * @see [TextButton] 文字按钮组件
 *
 * @example
 * ```kotlin
 * ButtonColumnMolecule {
 *     Button(text = "确认", onClick = { /* 处理确认 */ })
 *     OutlinedButton(text = "取消", onClick = { /* 处理取消 */ })
 * }
 * ```
 */
@Composable
fun ButtonColumnMolecule(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()
    }
}

/**
 * ButtonColumnMolecule 预览组件
 *
 * 用于在设计预览中展示 ButtonColumnMolecule 组件的默认状态。
 * 此预览函数支持日夜两种主题模式，展示三种不同类型的按钮。
 */
@PreviewsDayNight
@Composable
internal fun ButtonColumnMoleculePreview() = ElementPreview {
    ButtonColumnMolecule {
        Button(text = "Button", onClick = {}, modifier = Modifier.fillMaxWidth())
        OutlinedButton(text = "OutlinedButton", onClick = {}, modifier = Modifier.fillMaxWidth())
        TextButton(text = "TextButton", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
