/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 红色指示器原子组件
 *
 * 用于显示状态指示点的圆形组件。
 * 默认使用红色（critical）配色方案，可自定义大小、边框和颜色。
 * 常用于显示未读消息标记、在线状态或提醒指示。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

/**
 * 红色指示器组件
 *
 * 创建一个圆形状态指示器，带有可选的边框。
 * 指示器使用 [ElementTheme.colors.bgCriticalPrimary] 作为默认颜色。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param size Dp 指示器的直径大小，默认为 10.dp
 * @param borderSize Dp 边框的宽度，默认为 1.dp，设为 0.dp 可移除边框
 * @param color Color 指示器的背景颜色，默认为 [ElementTheme.colors.bgCriticalPrimary]
 *
 * @return Unit
 *
 * @see [ElementTheme.colors.bgCriticalPrimary] 默认关键状态背景色
 *
 * @example
 * ```kotlin
 * RedIndicatorAtom(
 *     size = 12.dp,
 *     color = Color.Green
 * )
 * ```
 */
@Composable
fun RedIndicatorAtom(
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    borderSize: Dp = 1.dp,
    color: Color = ElementTheme.colors.bgCriticalPrimary,
) {
    Box(
        modifier = modifier
            .size(size)
            .border(borderSize, ElementTheme.colors.bgCanvasDefault, CircleShape)
            .padding(borderSize / 2)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * RedIndicatorAtom 预览组件
 *
 * 用于在设计预览中展示 RedIndicatorAtom 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun RedIndicatorAtomPreview() = ElementPreview {
    RedIndicatorAtom()
}
