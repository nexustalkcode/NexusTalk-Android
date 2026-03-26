/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * !!! 警告 !!!
 * 
 * 这是一个自动生成的文件。
 * 请勿手动编辑。
 */

@file:Suppress("all")
package io.element.android.compound.tokens.generated

import androidx.compose.ui.graphics.Color

/**
 * 此类包含Compound主题的所有语义令牌。
 */
data class SemanticColors(
    /** 强调色或品牌操作的背景颜色。状态: 悬停 */
    val bgAccentHovered: Color,
    /** 强调色或品牌操作的背景颜色。状态: 按下 */
    val bgAccentPressed: Color,
    /** 强调色或品牌操作的背景颜色。状态: 默认 */
    val bgAccentRest: Color,
    /** 强调色或品牌操作的背景颜色。状态: 选中 */
    val bgAccentSelected: Color,
    /** 主要操作的背景颜色。状态: 禁用。 */
    val bgActionPrimaryDisabled: Color,
    /** 主要操作的背景颜色。状态: 悬停。 */
    val bgActionPrimaryHovered: Color,
    /** 主要操作的背景颜色。状态: 按下。 */
    val bgActionPrimaryPressed: Color,
    /** 主要操作的背景颜色。状态: 默认。 */
    val bgActionPrimaryRest: Color,
    /** 次要操作的背景颜色。状态: 悬停。 */
    val bgActionSecondaryHovered: Color,
    /** 次要操作的背景颜色。状态: 按下。 */
    val bgActionSecondaryPressed: Color,
    /** 次要操作的背景颜色。状态: 默认。 */
    val bgActionSecondaryRest: Color,
    /** 第三级操作的背景颜色。状态: 悬停 */
    val bgActionTertiaryHovered: Color,
    /** 第三级操作的背景颜色。状态: 默认 */
    val bgActionTertiaryRest: Color,
    /** 第三级操作的背景颜色。状态: 选中 */
    val bgActionTertiarySelected: Color,
    /** 徽章强调色背景颜色 */
    val bgBadgeAccent: Color,
    /** 徽章默认背景颜色 */
    val bgBadgeDefault: Color,
    /** 徽章信息背景颜色 */
    val bgBadgeInfo: Color,
    /** 用户界面的默认全局背景。层级: 默认 (层级0) */
    val bgCanvasDefault: Color,
    /** 用户界面的默认全局背景。层级: 层级1。 */
    val bgCanvasDefaultLevel1: Color,
    /** 禁用元素的默认背景。无最低对比度要求。 */
    val bgCanvasDisabled: Color,
    /** 高对比度背景颜色，适用于危险状态。状态: 悬停。 */
    val bgCriticalHovered: Color,
    /** 高对比度背景颜色，适用于危险状态。状态: 默认。 */
    val bgCriticalPrimary: Color,
    /** 默认柔和的危险状态表面。状态: 默认。 */
    val bgCriticalSubtle: Color,
    /** 默认柔和的危险状态表面。状态: 悬停。 */
    val bgCriticalSubtleHovered: Color,
    /** 装饰性背景 (1, 青柠色) 适用于头像和用户名。 */
    val bgDecorative1: Color,
    /** 装饰性背景 (2, 青色) 适用于头像和用户名。 */
    val bgDecorative2: Color,
    /** 装饰性背景 (3, 紫红色) 适用于头像和用户名。 */
    val bgDecorative3: Color,
    /** 装饰性背景 (4, 紫色) 适用于头像和用户名。 */
    val bgDecorative4: Color,
    /** 装饰性背景 (5, 粉色) 适用于头像和用户名。 */
    val bgDecorative5: Color,
    /** 装饰性背景 (6, 橙色) 适用于头像和用户名。 */
    val bgDecorative6: Color,
    /** 信息元素的柔和背景颜色。状态: 默认。 */
    val bgInfoSubtle: Color,
    /** 中等对比度表面。层级: 默认 (层级2)。 */
    val bgSubtlePrimary: Color,
    /** 低对比度表面。层级: 默认 (层级1)。 */
    val bgSubtleSecondary: Color,
    /** 较低对比度表面。层级: 层级0。 */
    val bgSubtleSecondaryLevel0: Color,
    /** 成功状态元素的柔和背景颜色。状态: 默认。 */
    val bgSuccessSubtle: Color,
    /** 强调色边框，用于消息高亮的关键线 */
    val borderAccentSubtle: Color,
    /** 高对比度边框，适用于危险状态。状态: 悬停。 */
    val borderCriticalHovered: Color,
    /** 高对比度边框，适用于危险状态。状态: 默认。 */
    val borderCriticalPrimary: Color,
    /** 危险状态元素的柔和边框颜色。 */
    val borderCriticalSubtle: Color,
    /** 用于禁用元素的边框。无最低对比度要求。 */
    val borderDisabled: Color,
    /** 用于焦点状态轮廓。 */
    val borderFocused: Color,
    /** 信息元素的柔和边框颜色。 */
    val borderInfoSubtle: Color,
    /** 可访问交互元素边框的默认对比度。状态: 悬停。 */
    val borderInteractiveHovered: Color,
    /** 可访问交互元素边框的默认对比度。状态: 默认。 */
    val borderInteractivePrimary: Color,
    /** ⚠️ 不可访问交互元素边框的最低对比度，<3:1。仅用于非关键边框。请勿完全依赖它们。状态: 默认。 */
    val borderInteractiveSecondary: Color,
    /** 成功状态元素的柔和边框颜色。 */
    val borderSuccessSubtle: Color,
    /** 超级按钮和发送按钮的背景渐变节点 */
    val gradientActionStop1: Color,
    /** 超级按钮和发送按钮的背景渐变节点 */
    val gradientActionStop2: Color,
    /** 超级按钮和发送按钮的背景渐变节点 */
    val gradientActionStop3: Color,
    /** 超级按钮和发送按钮的背景渐变节点 */
    val gradientActionStop4: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop1: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop2: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop3: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop4: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop5: Color,
    /** 柔和的背景渐变节点，适用于信息 */
    val gradientInfoStop6: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop1: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop2: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop3: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop4: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop5: Color,
    /** 柔和的背景渐变节点，适用于消息高亮和光晕效果 */
    val gradientSubtleStop6: Color,
    /** 品牌渐变按钮的开始颜色 */
    val gradientBrandStop1: Color,
    /** 品牌渐变按钮的结束颜色 */
    val gradientBrandStop2: Color,
    /** 最高对比度的可访问强调色图标。 */
    val iconAccentPrimary: Color,
    /** 最低对比度的可访问强调色图标。 */
    val iconAccentTertiary: Color,
    /** 高对比度图标，适用于危险状态。状态: 默认。 */
    val iconCriticalPrimary: Color,
    /** 用于禁用元素中的图标。无最低对比度要求。 */
    val iconDisabled: Color,
    /** 高对比度图标，适用于信息元素。 */
    val iconInfoPrimary: Color,
    /** 高对比度实心背景（如主色、强调色或危险操作）上的最高对比度图标颜色。 */
    val iconOnSolidPrimary: Color,
    /** 最高对比度图标。 */
    val iconPrimary: Color,
    /** 主图标的半透明版本。请参阅其预期用途。 */
    val iconPrimaryAlpha: Color,
    /** ⚠️ 不可访问的最低对比度图标，<3:1。仅用于非关键图标。请勿完全依赖它们。 */
    val iconQuaternary: Color,
    /** 次级图标的半透明版本。请参阅其预期用途。 */
    val iconQuaternaryAlpha: Color,
    /** 较低对比度图标。 */
    val iconSecondary: Color,
    /** 次级图标的半透明版本。请参阅其预期用途。 */
    val iconSecondaryAlpha: Color,
    /** 高对比度图标，适用于成功状态元素。 */
    val iconSuccessPrimary: Color,
    /** 最低对比度的可访问图标。 */
    val iconTertiary: Color,
    /** 三级图标的半透明版本。请参阅其预期用途。 */
    val iconTertiaryAlpha: Color,
    /** 纯操作强调色文本颜色。 */
    val textActionAccent: Color,
    /** 纯操作默认文本颜色。 */
    val textActionPrimary: Color,
    /** 徽章强调色文本颜色 */
    val textBadgeAccent: Color,
    /** 徽章信息文本颜色 */
    val textBadgeInfo: Color,
    /** 危险纯操作文本颜色。 */
    val textCriticalPrimary: Color,
    /** 装饰性文本颜色 (1, 青柠色) 适用于头像和用户名。 */
    val textDecorative1: Color,
    /** 装饰性文本颜色 (2, 青色) 适用于头像和用户名。 */
    val textDecorative2: Color,
    /** 装饰性文本颜色 (3, 紫红色) 适用于头像和用户名。 */
    val textDecorative3: Color,
    /** 装饰性文本颜色 (4, 紫色) 适用于头像和用户名。 */
    val textDecorative4: Color,
    /** 装饰性文本颜色 (5, 粉色) 适用于头像和用户名。 */
    val textDecorative5: Color,
    /** 装饰性文本颜色 (6, 橙色) 适用于头像和用户名。 */
    val textDecorative6: Color,
    /** 用于禁用元素中的常规文本。无最低对比度要求。 */
    val textDisabled: Color,
    /** 信息元素强调色文本颜色。 */
    val textInfoPrimary: Color,
    /** 外部链接文本颜色。 */
    val textLinkExternal: Color,
    /** 用于高对比度实心背景（如主色、强调色或危险操作）上的文本颜色。 */
    val textOnSolidPrimary: Color,
    /** 最高对比度文本。 */
    val textPrimary: Color,
    /** 最低对比度文本。 */
    val textSecondary: Color,
    /** 成功状态元素强调色文本颜色。 */
    val textSuccessPrimary: Color,
    /** 浅色主题为true，深色主题为false。 */
    val isLight: Boolean,
)
