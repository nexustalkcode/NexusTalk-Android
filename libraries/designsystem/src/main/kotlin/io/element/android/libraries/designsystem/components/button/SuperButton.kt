/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * SuperButton - 具有渐变效果的按钮组件
 *
 * 该组件是 Element Android 设计系统中的核心按钮组件之一，结合了 Material 3 基础组件
 * 和 Element 品牌视觉设计。与标准按钮相比，SuperButton 使用渐变着色器（ShaderBrush）
 * 实现品牌化的视觉效果，包括渐变边框和带有透明度的渐变背景。
 *
 * 主要特性：
 * - 支持多种按钮尺寸（Large、Medium、Small 以及对应的 LowPadding 变体）
 * - 完整的启用/禁用状态支持，禁用状态下显示灰色边框和文字
 * - 内置渐变边框效果，根据 Element 品牌色彩生成
 * - 按钮背景带有 4% 透明度的渐变叠加效果
 * - 支持自定义按钮形状，默认为胶囊形状（50dp 圆角）
 * - 内置涟漪点击效果，提供良好的交互反馈
 *
 * 适用场景：
 * - 需要醒目视觉效果的主要操作按钮
 * - 品牌化要求较高的交互元素
 * - 需要与 Element 设计语言保持一致的产品界面
 */

package io.element.android.libraries.designsystem.components.button

// ==================== Compose 基础布局组件 ====================
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
// ==================== Compose 修饰符和交互组件 ====================
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
// ==================== Compose 形状和样式组件 ====================
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
// ==================== Compose 交互效果组件 ====================
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
// ==================== Compose 运行时组件 ====================
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
// ==================== Compose UI 基础组件 ====================
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
// ==================== Compose 图形和着色器组件 ====================
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
// ==================== Compose 单位组件 ====================
import androidx.compose.ui.unit.dp
// ==================== Element Android 主题和颜色组件 ====================
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.colors.gradientActionColors
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.lowHorizontalPaddingValue

/**
 * SuperButton - 创建具有渐变效果的按钮组件
 *
 * 此函数创建一个符合 Element 设计语言的按钮组件，通过自定义 ShaderBrush 实现渐变效果。
 * 与标准的 Material 3 按钮相比，SuperButton 具有以下特点：
 * - 使用 LinearGradientShader 创建垂直渐变边框效果
 * - 背景带有 4% 透明度的渐变叠加，增强视觉层次感
 * - 支持完整的启用/禁用状态切换，禁用时使用灰色调
 * - 内置涟漪效果，提供良好的交互反馈
 *
 * 设计决策说明：
 * - 使用 remember 缓存 contentPadding 和 shaderBrush，避免重组时重复计算
 * - 使用自定义 ShaderBrush 而非简单的渐变颜色，支持动态尺寸调整
 * - 使用 CompositionLocalProvider 提供文字颜色和样式，保持内容组件的简洁性
 *
 * @param onClick 点击事件处理函数，当用户点击按钮时调用
 * @param modifier 修饰符，用于调整按钮的布局和外观属性
 * @param shape 按钮形状，默认为 50dp 圆角的胶囊形状，支持自定义形状以满足特殊设计需求
 * @param buttonSize 按钮尺寸枚举，控制按钮大小和内边距
 * @param enabled 启用状态，控制按钮的交互性和视觉效果
 * @param content 按钮内容的可组合函数，通常放置 Text 组件显示按钮文字
 */
@Composable
fun SuperButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    buttonSize: ButtonSize = ButtonSize.Large,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    // ==================== 内边距计算 ====================
    // 根据按钮尺寸计算合适的内边距
    // 使用 remember 缓存计算结果，当 buttonSize 变化时重新计算
    // 不同尺寸对应不同的水平和垂直内边距，确保按钮内容布局合理
    val contentPadding = remember(buttonSize) {
        when (buttonSize) {
            ButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 13.dp)
            ButtonSize.LargeLowPadding -> PaddingValues(horizontal = lowHorizontalPaddingValue, vertical = 13.dp)
            ButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 9.dp)
            ButtonSize.MediumLowPadding -> PaddingValues(horizontal = lowHorizontalPaddingValue, vertical = 9.dp)
            ButtonSize.Small -> PaddingValues(horizontal = 16.dp, vertical = 5.dp)
        }
    }

    // ==================== 渐变颜色和着色器创建 ====================
    // 获取 Element 品牌渐变颜色
    val colors = gradientActionColors()

    // 创建自定义渐变着色器
    // 使用 remember 缓存，当 colors 变化时自动重建着色器
    // LinearGradientShader 创建垂直渐变，从顶部 (0,0) 到 (0, size.height)
    // 这种垂直渐变符合常见的按钮渐变设计趋势
    val shaderBrush = remember(colors) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    from = Offset(0f, 0f),
                    to = Offset(0f, size.height),
                    colors = colors,
                )
            }
        }
    }

    // ==================== 边框样式设置 ====================
    // 根据启用状态选择边框样式
    // 启用状态：使用渐变着色器创建品牌化边框效果
    // 禁用状态：使用 ElementTheme 定义的禁用边框颜色（灰色）
    val border = if (enabled) {
        BorderStroke(1.dp, shaderBrush)
    } else {
        BorderStroke(1.dp, ElementTheme.colors.borderDisabled)
    }

    // 获取背景颜色
    val backgroundColor = ElementTheme.colors.bgCanvasDefault

    // ==================== 按钮主体布局 ====================
    // 使用 Box 作为按钮的主体容器
    // 修饰符链按照从外到内的顺序应用，每个修饰符都会修改后续修饰符的行为
    Box(
        modifier = modifier
            // 1. minimumInteractiveComponentSize：确保按钮满足 Material 3 规定的最小可交互尺寸
            //    这对于触摸操作至关重要，防止按钮太小而难以点击
            .minimumInteractiveComponentSize()
            // 2. graphicsLayer：设置形状并允许非矩形裁剪
            //    clip = false 表示在 graphicsLayer 层面不进行裁剪，将裁剪延迟到 clip() 修饰符
            .graphicsLayer(shape = shape, clip = false)
            // 3. clip：应用形状裁剪，使背景和边框符合指定的圆角形状
            .clip(shape)
            // 4. border：应用边框效果，根据 enabled 状态选择渐变或灰色边框
            .border(border, shape)
            // 5. drawBehind：自定义绘制，在背景层绘制两个矩形
            //    - 第一个 drawRect 绘制纯色背景
            //    - 第二个 drawRect 绘制 4% 透明度的渐变效果，增强视觉层次感
            .drawBehind {
                drawRect(backgroundColor)
                drawRect(brush = shaderBrush, alpha = 0.04f)
            }
            // 6. clickable：添加点击交互功能
            //    - enabled：控制是否响应点击事件
            //    - interactionSource：管理交互状态，用于获取按压状态
            //    - indication：涟漪效果，提供视觉反馈
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            )
            // 7. padding：应用基于按钮尺寸计算的内边距
            //    确保按钮内容与边框之间有适当的间距
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        // ==================== 文字颜色和样式提供 ====================
        // 使用 CompositionLocalProvider 提供局部作用域的颜色和样式
        // 这种方式允许内容组件无需显式传递参数即可获取样式信息
        CompositionLocalProvider(
            // LocalContentColor：根据启用状态切换文字颜色
            // 启用时使用主文字颜色 (textPrimary)
            // 禁用时使用禁用文字颜色 (textDisabled)
            LocalContentColor provides if (enabled) ElementTheme.colors.textPrimary else ElementTheme.colors.textDisabled,
            // LocalTextStyle：设置统一的文字样式
            // fontBodyLgMedium 是 Element 品牌字体规范中的中等字重、大号字号
            LocalTextStyle provides ElementTheme.typography.fontBodyLgMedium,
        ) {
            // 渲染按钮内容
            content()
        }
    }
}

/**
 * SuperButton 组件预览函数
 *
 * 此函数用于在 Android Studio 的 Compose 预览中展示 SuperButton 组件的各种状态和尺寸变体。
 * 通过 @PreviewsDayNight 注解，组件会在日间模式和夜间模式下分别渲染，
 * 确保组件在不同主题下的视觉效果符合设计要求。
 *
 * 预览内容说明：
 * 1. 第一部分展示所有按钮尺寸的正常启用状态
 *    - Large：标准大尺寸按钮，适用于主要操作
 *    - LargeLowPadding：减少水平内边距的大尺寸按钮
 *    - Medium：中等尺寸按钮，适用于一般操作
 *    - MediumLowPadding：减少水平内边距的中等尺寸按钮
 *    - Small：小尺寸按钮，适用于次要操作或密集布局
 *
 * 2. HorizontalDivider 分隔线，用于区分正常状态和禁用状态
 *
 * 3. 第二部分展示所有按钮尺寸的禁用状态
 *    禁用状态下的按钮显示灰色边框和文字，表示当前不可交互
 *
 * 设计考量：
 * - 使用 Column 布局垂直排列所有预览项
 * - 每个按钮添加 10.dp 的外边距，确保预览项之间有适当间距
 * - 使用空 Lambda {} 作为 onClick 参数，避免预览时的点击事件
 */
@PreviewsDayNight
@Composable
internal fun SuperButtonPreview() {
    ElementPreview {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ==================== 正常启用状态预览 ====================
            // Large 尺寸按钮预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Large,
                onClick = {},
            ) {
                Text("Super button!")
            }

            // LargeLowPadding 尺寸按钮预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.LargeLowPadding,
                onClick = {},
            ) {
                Text("Super LargeLowPadding")
            }

            // Medium 尺寸按钮预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Medium,
                onClick = {},
            ) {
                Text("Super button!")
            }

            // MediumLowPadding 尺寸按钮预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.MediumLowPadding,
                onClick = {},
            ) {
                Text("Super MediumLowPadding")
            }

            // Small 尺寸按钮预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Small,
                onClick = {},
            ) {
                Text("Super button!")
            }

            // ==================== 分隔线 ====================
            HorizontalDivider()

            // ==================== 禁用状态预览 ====================
            // Large 尺寸禁用状态预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Large,
                enabled = false,
                onClick = {},
            ) {
                Text("Super button!")
            }

            // Medium 尺寸禁用状态预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Medium,
                enabled = false,
                onClick = {},
            ) {
                Text("Super button!")
            }

            // Small 尺寸禁用状态预览
            SuperButton(
                modifier = Modifier.padding(10.dp),
                buttonSize = ButtonSize.Small,
                enabled = false,
                onClick = {},
            ) {
                Text("Super button!")
            }
        }
    }
}
