/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 渐变色工具类
 *
 * 提供 Element 主题中各种渐变色方案的访问接口。
 * 包含操作渐变、微妙渐变和信息渐变三种类型。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import io.element.android.compound.theme.ElementTheme

/**
 * 获取操作渐变色列表
 *
 * 返回用于按钮、链接等交互元素的渐变色。
 *
 * @return List<Color> 包含4个颜色的渐变色列表
 */
@Composable
@ReadOnlyComposable
fun gradientActionColors(): List<Color> = listOf(
    ElementTheme.colors.gradientActionStop1,
    ElementTheme.colors.gradientActionStop2,
    ElementTheme.colors.gradientActionStop3,
    ElementTheme.colors.gradientActionStop4,
)

/**
 * 获取微妙渐变色列表
 *
 * 返回用于背景、装饰等微妙场景的渐变色。
 *
 * @return List<Color> 包含6个颜色的渐变色列表
 */
@Composable
@ReadOnlyComposable
fun gradientSubtleColors(): List<Color> = listOf(
    ElementTheme.colors.gradientSubtleStop1,
    ElementTheme.colors.gradientSubtleStop2,
    ElementTheme.colors.gradientSubtleStop3,
    ElementTheme.colors.gradientSubtleStop4,
    ElementTheme.colors.gradientSubtleStop5,
    ElementTheme.colors.gradientSubtleStop6,
)

/**
 * 获取信息渐变色列表
 *
 * 返回用于提示、信息展示场景的渐变色。
 *
 * @return List<Color> 包含6个颜色的渐变色列表
 */
@Composable
@ReadOnlyComposable
fun gradientInfoColors(): List<Color> = listOf(
    ElementTheme.colors.gradientInfoStop1,
    ElementTheme.colors.gradientInfoStop2,
    ElementTheme.colors.gradientInfoStop3,
    ElementTheme.colors.gradientInfoStop4,
    ElementTheme.colors.gradientInfoStop5,
    ElementTheme.colors.gradientInfoStop6,
)
