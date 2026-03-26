/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 占位符原子组件
 *
 * 用于显示加载状态或内容占位的矩形组件。
 * 组件采用圆角矩形设计，可自定义宽度、高度和背景颜色，
 * 常用于骨架屏加载效果或布局占位。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.placeholderBackground

/**
 * 占位符组件
 *
 * 创建一个指定尺寸的圆角矩形占位框。
 * 圆角半径自动设置为高度的一半，形成胶囊形状。
 *
 * @param width Dp 占位框的宽度
 * @param height Dp 占位框的高度
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param color Color 占位框的背景颜色，默认为 [ElementTheme.colors.placeholderBackground]
 *
 * @return Unit
 *
 * @see [ElementTheme.colors.placeholderBackground] 默认占位背景色
 *
 * @example
 * ```kotlin
 * PlaceholderAtom(
 *     width = 80.dp,
 *     height = 12.dp,
 *     modifier = Modifier.padding(8.dp)
 * )
 * ```
 */
@Composable
fun PlaceholderAtom(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    color: Color = ElementTheme.colors.placeholderBackground,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                color = color,
                shape = RoundedCornerShape(size = height / 2)
            )
    )
}

/**
 * PlaceholderAtom 预览组件
 *
 * 用于在设计预览中展示 PlaceholderAtom 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun PlaceholderAtomPreview() = ElementPreview {
    // Use a Red background to see the shape
    Box(modifier = Modifier.background(color = Color.Red)) {
        PlaceholderAtom(
            width = 80.dp,
            height = 12.dp
        )
    }
}
