/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 颜色工具类
 *
 * 提供基于布尔值状态的颜色转换功能，用于根据启用/禁用状态返回相应的颜色值。
 * 这些扩展函数简化了 Compose UI 中根据状态显示不同颜色的逻辑。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.element.android.compound.theme.ElementTheme

/**
 * 将布尔值转换为启用状态的主文本颜色
 *
 * 根据布尔值的状态，返回主文本颜色或禁用状态的文本颜色。
 * 当值为 true 时返回 primary 主题色，false 时返回禁用状态颜色。
 *
 * @receiver this Boolean 要转换的布尔值，表示启用状态
 * @return Color 根据状态返回的文本颜色，启用时为 [ElementTheme.colors.textPrimary]，禁用时为 [ElementTheme.colors.textDisabled]
 *
 * @example
 * ```kotlin
 * val isEnabled = true
 * val color = isEnabled.toEnabledColor() // 返回 ElementTheme.colors.textPrimary
 * ```
 */
@Composable
fun Boolean.toEnabledColor(): Color {
    return if (this) {
        ElementTheme.colors.textPrimary
    } else {
        ElementTheme.colors.textDisabled
    }
}

/**
 * 将布尔值转换为启用状态的次要文本颜色
 *
 * 根据布尔值的状态，返回次要文本颜色或禁用状态的文本颜色。
 * 当值为 true 时返回 secondary 主题色，false 时返回禁用状态颜色。
 *
 * @receiver this Boolean 要转换的布尔值，表示启用状态
 * @return Color 根据状态返回的次要文本颜色，启用时为 [ElementTheme.colors.textSecondary]，禁用时为 [ElementTheme.colors.textDisabled]
 *
 * @example
 * ```kotlin
 * val isEnabled = false
 * val color = isEnabled.toSecondaryEnabledColor() // 返回 ElementTheme.colors.textDisabled
 * ```
 */
@Composable
fun Boolean.toSecondaryEnabledColor(): Color {
    return if (this) {
        ElementTheme.colors.textSecondary
    } else {
        ElementTheme.colors.textDisabled
    }
}

/**
 * 将布尔值转换为启用状态的主图标颜色
 *
 * 根据布尔值的状态，返回主图标颜色或禁用状态的图标颜色。
 * 当值为 true 时返回 primary 主题图标色，false 时返回禁用状态颜色。
 *
 * @receiver this Boolean 要转换的布尔值，表示启用状态
 * @return Color 根据状态返回的图标颜色，启用时为 [ElementTheme.colors.iconPrimary]，禁用时为 [ElementTheme.colors.iconDisabled]
 *
 * @example
 * ```kotlin
 * val isInteractive = true
 * val iconColor = isInteractive.toIconEnabledColor() // 返回 ElementTheme.colors.iconPrimary
 * ```
 */
@Composable
fun Boolean.toIconEnabledColor(): Color {
    return if (this) {
        ElementTheme.colors.iconPrimary
    } else {
        ElementTheme.colors.iconDisabled
    }
}

/**
 * 将布尔值转换为启用状态的次要图标颜色
 *
 * 根据布尔值的状态，返回次要图标颜色或禁用状态的图标颜色。
 * 当值为 true 时返回 secondary 主题图标色，false 时返回禁用状态颜色。
 *
 * @receiver this Boolean 要转换的布尔值，表示启用状态
 * @return Color 根据状态返回的次要图标颜色，启用时为 [ElementTheme.colors.iconSecondary]，禁用时为 [ElementTheme.colors.iconDisabled]
 *
 * @example
 * ```kotlin
 * val isActive = false
 * val iconColor = isActive.toIconSecondaryEnabledColor() // 返回 ElementTheme.colors.iconDisabled
 * ```
 */
@Composable
fun Boolean.toIconSecondaryEnabledColor(): Color {
    return if (this) {
        ElementTheme.colors.iconSecondary
    } else {
        ElementTheme.colors.iconDisabled
    }
}
