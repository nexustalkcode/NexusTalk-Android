/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * Beta 标签组件
 *
 * 这是一个用于标识功能处于 Beta 测试阶段的标签组件。
 * 组件显示"BETA"文本，使用圆角矩形边框和浅色背景样式，
 * 遵循 Element 主题的 info 子主题配色方案。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
/**
 * Beta 标签组件
 *
 * 这是一个用于标识功能处于 Beta 测试阶段的标签组件。
 * 组件显示"BETA"文本，使用圆角矩形边框和浅色背景样式，
 * 遵循 Element 主题的 info 子主题配色方案。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 显示 Beta 标签的 Compose 组件
 *
 * 创建一个带有"BETA"文本的标签，用于标识实验性或测试中的功能。
 * 标签采用圆角矩形设计，使用 Element 主题的 info 子主题配色，
 * 包括边框、背景和文本颜色的统一风格。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @example
 * ```kotlin
 * BetaLabel(modifier = Modifier.padding(8.dp))
 * ```
 *
 * @see [CommonStrings.common_beta] 获取"BETA"文本资源
 */
@Composable
fun BetaLabel(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size = 6.dp)
    Text(
        modifier = modifier
            .border(
                width = 1.dp,
                color = ElementTheme.colors.borderInfoSubtle,
                shape = shape,
            )
            .background(
                color = ElementTheme.colors.bgInfoSubtle,
                shape = shape,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        text = stringResource(CommonStrings.common_beta).uppercase(),
        style = ElementTheme.typography.fontBodySmMedium,
        color = ElementTheme.colors.textInfoPrimary,
    )
}

/**
 * BetaLabel 预览组件
 *
 * 用于在设计预览中展示 BetaLabel 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun BetaLabelPreview() = ElementPreview {
    BetaLabel()
}
