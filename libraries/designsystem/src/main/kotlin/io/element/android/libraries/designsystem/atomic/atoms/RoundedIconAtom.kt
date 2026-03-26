/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 圆角图标原子组件
 *
 * 用于显示带圆角背景容器的图标组件。
 * 图标位于圆角矩形背景中央，支持不同尺寸规格。
 * 适用于用户头像、状态图标或功能入口等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.temporaryColorBgSpecial

/**
 * 圆角图标尺寸规格枚举
 *
 * 定义 RoundedIconAtom 组件的不同尺寸规格。
 */
enum class RoundedIconAtomSize {
    /** 中等尺寸，容器 30dp，图标 16dp */
    Medium,
    /** 大尺寸，容器 36dp，图标 24dp */
    Big,
}

/**
 * 圆角图标组件
 *
 * 创建一个带圆角背景的图标组件。
 * 图标位于圆角矩形背景中央，根据 [size] 参数选择不同的尺寸。
 * [resourceId] 和 [imageVector] 是互斥参数，只能使用其中一个。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param size RoundedIconAtomSize 图标尺寸规格，默认为 RoundedIconAtomSize.Big
 * @param resourceId Int? 图标资源 ID，与 [imageVector] 互斥，默认为 null
 * @param imageVector ImageVector? 图标向量，与 [resourceId] 互斥，默认为 null
 * @param tint Color 图标的着色颜色，默认为 [ElementTheme.colors.iconSecondary]
 * @param backgroundTint Color 背景容器的着色颜色，默认为 [ElementTheme.colors.temporaryColorBgSpecial]
 *
 * @return Unit
 *
 * @see RoundedIconAtomSize 尺寸规格枚举
 * @see [ElementTheme.colors.iconSecondary] 默认图标着色
 * @see [ElementTheme.colors.temporaryColorBgSpecial] 默认背景着色
 *
 * @example
 * ```kotlin
 * RoundedIconAtom(
 *     imageVector = CompoundIcons.HomeSolid(),
 *     size = RoundedIconAtomSize.Big
 * )
 * ```
 */
@Composable
fun RoundedIconAtom(
    modifier: Modifier = Modifier,
    size: RoundedIconAtomSize = RoundedIconAtomSize.Big,
    resourceId: Int? = null,
    imageVector: ImageVector? = null,
    tint: Color = ElementTheme.colors.iconSecondary,
    backgroundTint: Color = ElementTheme.colors.temporaryColorBgSpecial,
) {
    Box(
        modifier = modifier
            .size(size.toContainerSize())
            .background(
                color = backgroundTint,
                shape = RoundedCornerShape(size.toCornerSize())
            )
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(size.toIconSize()),
            tint = tint,
            resourceId = resourceId,
            imageVector = imageVector,
            contentDescription = null,
        )
    }
}

private fun RoundedIconAtomSize.toContainerSize(): Dp {
    return when (this) {
        RoundedIconAtomSize.Medium -> 30.dp
        RoundedIconAtomSize.Big -> 36.dp
    }
}

private fun RoundedIconAtomSize.toCornerSize(): Dp {
    return when (this) {
        RoundedIconAtomSize.Medium -> 8.dp
        RoundedIconAtomSize.Big -> 8.dp
    }
}

private fun RoundedIconAtomSize.toIconSize(): Dp {
    return when (this) {
        RoundedIconAtomSize.Medium -> 16.dp
        RoundedIconAtomSize.Big -> 24.dp
    }
}

/**
 * RoundedIconAtom 预览组件
 *
 * 用于在设计预览中展示 RoundedIconAtom 组件的不同尺寸状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun RoundedIconAtomPreview() = ElementPreview {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RoundedIconAtom(
            size = RoundedIconAtomSize.Medium,
            imageVector = CompoundIcons.HomeSolid(),
        )
        RoundedIconAtom(
            size = RoundedIconAtomSize.Big,
            imageVector = CompoundIcons.HomeSolid(),
        )
    }
}
