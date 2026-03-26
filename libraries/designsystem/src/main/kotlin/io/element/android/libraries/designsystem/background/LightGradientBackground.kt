/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 浅色渐变背景组件
 *
 * 用于加入房间页面的浅色径向渐变背景。
 * 从中心向四周扩散，呈现柔和的渐变效果。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

/**
 * 浅色渐变背景组件
 *
 * 创建一个带有径向渐变效果的浅色背景。
 * 渐变从第一个颜色过渡到第二个颜色，中心点可调整。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param backgroundColor Color 背景基础颜色，默认为 [ElementTheme.colors.bgCanvasDefault]
 * @param firstColor Color 渐变起始颜色，默认为浅绿色 (0x1E0DBD8B)
 * @param secondColor Color 渐变结束颜色，默认为透明蓝色 (0x001273EB)
 * @param ratio Float 渐变中心点比例，默认为 642/775
 *
 * @return Unit
 *
 * @example
 * ```kotlin
 * LightGradientBackground(
 *     modifier = Modifier.fillMaxSize()
 * )
 * ```
 */
@Composable
fun LightGradientBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ElementTheme.colors.bgCanvasDefault,
    firstColor: Color = Color(0x1E0DBD8B),
    secondColor: Color = Color(0x001273EB),
    ratio: Float = 642 / 775f,
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val biggerDimension = size.width * 1.98f
        val gradientShaderBrush = ShaderBrush(
            RadialGradientShader(
                colors = listOf(firstColor, secondColor),
                center = size.center.copy(x = size.width * ratio, y = size.height * ratio),
                radius = biggerDimension / 2f,
                colorStops = listOf(0f, 0.95f)
            )
        )
        drawRect(backgroundColor, size = size)
        drawRect(brush = gradientShaderBrush, size = size)
    }
}

/**
 * LightGradientBackground 预览组件
 *
 * 用于在设计预览中展示 LightGradientBackground 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun LightGradientBackgroundPreview() = ElementPreview {
    LightGradientBackground()
}
