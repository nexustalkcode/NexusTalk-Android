/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * Element 标志原子组件
 *
 * 用于显示 Element 应用标志的组件，支持不同尺寸规格。
 * 组件包含圆角矩形边框、阴影效果和模糊背景层，
 * 并根据主题模式（浅色/深色）自动调整颜色方案。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.modifiers.blurCompat
import io.element.android.libraries.designsystem.modifiers.blurredShapeShadow
import io.element.android.libraries.designsystem.modifiers.canUseBlurMaskFilter
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

/**
 * Element 标志组件
 *
 * 显示 Element 应用标志的 Compose 组件，支持不同尺寸规格。
 * 组件包含多层视觉效果：圆角矩形边框、阴影、模糊背景层和标志图标。
 * 根据传入的 [size] 参数选择不同的尺寸规格，自动适配浅色/深色主题。
 *
 * @param size ElementLogoAtomSize 标志尺寸规格，决定组件的整体大小、圆角半径和阴影配置
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param useBlurredShadow Boolean 是否使用模糊阴影效果，默认为检测设备是否支持模糊滤镜，默认为 true
 * @param darkTheme Boolean 是否使用深色主题，默认为当前主题设置的相反值，默认为 false
 *
 * @return Unit
 *
 * @see ElementLogoAtomSize.Medium 中等尺寸规格
 * @see ElementLogoAtomSize.Large 大尺寸规格
 *
 * @example
 * ```kotlin
 * ElementLogoAtom(size = ElementLogoAtomSize.Medium)
 * ElementLogoAtom(size = ElementLogoAtomSize.Large, darkTheme = true)
 * ```
 */
@Composable
fun ElementLogoAtom(
    size: ElementLogoAtomSize,
    modifier: Modifier = Modifier,
    useBlurredShadow: Boolean = canUseBlurMaskFilter(),
    darkTheme: Boolean = ElementTheme.isLightTheme.not(),
) {
    val blur = if (darkTheme) 160.dp else 24.dp
    val shadowColor = if (darkTheme) size.shadowColorDark else size.shadowColorLight
    val logoShadowColor = if (darkTheme) size.logoShadowColorDark else size.logoShadowColorLight
    val backgroundColor = if (darkTheme) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.4f)
    val borderColor = if (darkTheme) Color.White.copy(alpha = 0.89f) else Color.White
    Box(
        modifier = modifier
            .size(size.outerSize)
            .border(size.borderWidth, borderColor, RoundedCornerShape(size.cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        if (useBlurredShadow) {
            Box(
                Modifier
                    .size(size.outerSize)
                    .blurredShapeShadow(
                        color = shadowColor,
                        cornerRadius = size.cornerRadius,
                        blurRadius = size.shadowRadius,
                        offsetY = 8.dp,
                    )
            )
        } else {
            Box(
                Modifier
                    .size(size.outerSize)
                    .shadow(
                        elevation = size.shadowRadius,
                        shape = RoundedCornerShape(size.cornerRadius),
                        clip = false,
                        ambientColor = shadowColor
                    )
            )
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(size.cornerRadius))
                .size(size.outerSize)
                .background(backgroundColor)
                .blurCompat(blur)
        )
        Image(
            modifier = Modifier
                .size(size.logoSize)
                // Do the same double shadow than on Figma...
                .shadow(
                    elevation = 35.dp,
                    clip = false,
                    shape = CircleShape,
                    ambientColor = logoShadowColor,
                )
                .shadow(
                    elevation = 35.dp,
                    clip = false,
                    shape = CircleShape,
                    ambientColor = Color(0x80000000),
                ),
            painter = painterResource(id = R.drawable.element_logo),
            contentDescription = null
        )
    }
}

/**
 * Element 标志尺寸规格密封类
 *
 * 定义 ElementLogoAtom 组件的不同尺寸规格。
 * 每个规格包含完整的尺寸参数配置：
 * - outerSize: 组件外框尺寸
 * - logoSize: 标志图标尺寸
 * - cornerRadius: 圆角半径
 * - borderWidth: 边框宽度
 * - logoShadowColorDark/Light: 标志阴影颜色（深色/浅色主题）
 * - shadowColorDark/Light: 背景阴影颜色（深色/浅色主题）
 * - shadowRadius: 阴影半径
 *
 * @param outerSize Dp 组件外框尺寸
 * @param logoSize Dp 标志图标尺寸
 * @param cornerRadius Dp 圆角半径
 * @param borderWidth Dp 边框宽度
 * @param logoShadowColorDark Color 深色主题下的标志阴影颜色
 * @param logoShadowColorLight Color 浅色主题下的标志阴影颜色
 * @param shadowColorDark Color 深色主题下的背景阴影颜色
 * @param shadowColorLight Color 浅色主题下的背景阴影颜色
 * @param shadowRadius Dp 阴影半径
 */
sealed class ElementLogoAtomSize(
    val outerSize: Dp,
    val logoSize: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val logoShadowColorDark: Color,
    val logoShadowColorLight: Color,
    val shadowColorDark: Color,
    val shadowColorLight: Color,
    val shadowRadius: Dp,
) {
    /**
     * 中等尺寸规格
     *
     * 适用于中等大小的标志显示场景。
     * 整体尺寸为 120dp，标志图标为 83.5dp，圆角为 33dp。
     */
    data object Medium : ElementLogoAtomSize(
        outerSize = 120.dp,
        logoSize = 83.5.dp,
        cornerRadius = 33.dp,
        borderWidth = 0.38.dp,
        logoShadowColorDark = Color(0x4D000000),
        logoShadowColorLight = Color(0x66000000),
        shadowColorDark = Color.Black.copy(alpha = 0.4f),
        shadowColorLight = Color(0x401B1D22),
        shadowRadius = 32.dp,
    )

    /**
     * 大尺寸规格
     *
     * 适用于大尺寸的标志显示场景，如登录页面或欢迎页面。
     * 整体尺寸为 158dp，标志图标为 110dp，圆角为 44dp。
     */
    data object Large : ElementLogoAtomSize(
        outerSize = 158.dp,
        logoSize = 110.dp,
        cornerRadius = 44.dp,
        borderWidth = 0.5.dp,
        logoShadowColorDark = Color(0x4D000000),
        logoShadowColorLight = Color(0x66000000),
        shadowColorDark = Color.Black,
        shadowColorLight = Color(0x801B1D22),
        shadowRadius = 60.dp,
    )
}

@Composable
@PreviewsDayNight
internal fun ElementLogoAtomMediumPreview() = ElementPreview {
    ContentToPreview(ElementLogoAtomSize.Medium)
}

@Composable
@PreviewsDayNight
internal fun ElementLogoAtomLargePreview() = ElementPreview {
    ContentToPreview(ElementLogoAtomSize.Large)
}

@Composable
@PreviewsDayNight
internal fun ElementLogoAtomMediumNoBlurShadowPreview() = ElementPreview {
    ContentToPreview(ElementLogoAtomSize.Medium, useBlurredShadow = false)
}

@Composable
@PreviewsDayNight
internal fun ElementLogoAtomLargeNoBlurShadowPreview() = ElementPreview {
    ContentToPreview(ElementLogoAtomSize.Large, useBlurredShadow = false)
}

@ExcludeFromCoverage
@Composable
private fun ContentToPreview(elementLogoAtomSize: ElementLogoAtomSize, useBlurredShadow: Boolean = true) {
    Box(
        Modifier
            .size(elementLogoAtomSize.outerSize + elementLogoAtomSize.shadowRadius * 2)
            .background(ElementTheme.colors.bgSubtlePrimary),
        contentAlignment = Alignment.Center
    ) {
        ElementLogoAtom(elementLogoAtomSize, useBlurredShadow = useBlurredShadow)
    }
}
