/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 按钮行分子组件
 *
 * 用于水平排列多个按钮的布局组件。
 * 按钮默认均匀分布（SpaceBetween），可根据需要调整水平排列方式。
 * 常用于需要水平排列按钮的场景，如对话框底部操作栏。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.TextButton

/**
 * 按钮行组件
 *
 * 创建一个水平排列按钮的行布局。
 * 宽度填充父容器，支持自定义水平排列方式和垂直对齐方式。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param horizontalArrangement Arrangement.Horizontal 水平排列方式，默认为 Arrangement.SpaceBetween
 * @param verticalAlignment Alignment.Vertical 垂直对齐方式，默认为 Alignment.Top
 * @param content @Composable RowScope.() -> Unit 按钮内容区域，可以放置多个按钮组件
 *
 * @return Unit
 *
 * @see [Arrangement.SpaceBetween] 两端对齐
 * @see [Arrangement.spacedBy] 带间距的排列
 *
 * @example
 * ```kotlin
 * ButtonRowMolecule {
 *     TextButton(text = "取消", onClick = { /* 处理取消 */ })
 *     TextButton(text = "确认", onClick = { /* 处理确认 */ })
 * }
 * ```
 */
@Composable
fun ButtonRowMolecule(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        content()
    }
}

/**
 * ButtonRowMolecule 预览组件
 *
 * 用于在设计预览中展示 ButtonRowMolecule 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun ButtonRowMoleculePreview() = ElementPreview {
    ButtonRowMolecule {
        TextButton(text = "Button 1", onClick = {})
        TextButton(text = "Button 2", onClick = {})
    }
}
