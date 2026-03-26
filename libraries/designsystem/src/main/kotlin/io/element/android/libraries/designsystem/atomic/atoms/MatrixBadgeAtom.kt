/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * Matrix 徽章原子组件
 *
 * 用于显示 Matrix 房间或用户状态的徽章组件。
 * 支持多种类型：正向（信任）、中性（公开）、负向（不信任）、信息（未加密）。
 * 徽章包含文本和图标，根据类型自动应用相应的颜色主题。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.Badge
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

/**
 * Matrix 徽章原子组件对象
 *
 * 提供 Matrix 徽章组件及其数据结构的命名空间。
 */
object MatrixBadgeAtom {
    /**
     * 徽章数据类
     *
     * 定义徽章组件所需的数据：文本、图标和类型。
     *
     * @property text String 徽章显示的文本内容
     * @property icon ImageVector 徽章显示的图标
     * @property type Type 徽 badge 的类型，决定颜色方案
     */
    data class MatrixBadgeData(
        val text: String,
        val icon: ImageVector,
        val type: Type,
    )

    /**
     * 徽章类型枚举
     *
     * 定义徽 badge 的不同状态类型，每种类型对应不同的颜色方案。
     */
    enum class Type {
        /** 正向类型 - 用于标识信任或已验证状态 */
        Positive,
        /** 中性类型 - 用于标识公开或默认状态 */
        Neutral,
        /** 负向类型 - 用于标识警告或危险状态 */
        Negative,
        /** 信息类型 - 用于标识提示或信息状态 */
        Info,
    }

    /**
     * 显示 Matrix 徽章
     *
     * 根据传入的 [data] 数据渲染徽章组件，
     * 根据徽章类型自动选择相应的背景色、文字色和图标色。
     *
     * @param data MatrixBadgeData 徽章数据，包含文本、图标和类型
     *
     * @return Unit
     *
     * @see MatrixBadgeData 徽章数据类
     * @see Type 徽章类型枚举
     */
    @Composable
    fun View(
        data: MatrixBadgeData,
    ) {
        val backgroundColor = when (data.type) {
            Type.Positive -> ElementTheme.colors.bgBadgeAccent
            Type.Neutral -> ElementTheme.colors.bgBadgeDefault
            Type.Negative -> ElementTheme.colors.bgCriticalSubtle
            Type.Info -> ElementTheme.colors.bgBadgeInfo
        }
        val textColor = when (data.type) {
            Type.Positive -> ElementTheme.colors.textBadgeAccent
            Type.Neutral -> ElementTheme.colors.textPrimary
            Type.Negative -> ElementTheme.colors.textCriticalPrimary
            Type.Info -> ElementTheme.colors.textBadgeInfo
        }
        val iconColor = when (data.type) {
            Type.Positive -> ElementTheme.colors.iconAccentPrimary
            Type.Neutral -> ElementTheme.colors.iconPrimary
            Type.Negative -> ElementTheme.colors.iconCriticalPrimary
            Type.Info -> ElementTheme.colors.iconInfoPrimary
        }
        Badge(
            text = data.text,
            icon = data.icon,
            backgroundColor = backgroundColor,
            iconColor = iconColor,
            textColor = textColor,
        )
    }
}

/**
 * MatrixBadgeAtom 正向类型预览
 */
@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomPositivePreview() = ElementPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Trusted",
            icon = CompoundIcons.Verified(),
            type = MatrixBadgeAtom.Type.Positive,
        )
    )
}

/**
 * MatrixBadgeAtom 中性类型预览
 */
@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomNeutralPreview() = ElementPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Public room",
            icon = CompoundIcons.Public(),
            type = MatrixBadgeAtom.Type.Neutral,
        )
    )
}

/**
 * MatrixBadgeAtom 负向类型预览
 */
@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomNegativePreview() = ElementPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Not trusted",
            icon = CompoundIcons.ErrorSolid(),
            type = MatrixBadgeAtom.Type.Negative,
        )
    )
}

/**
 * MatrixBadgeAtom 信息类型预览
 */
@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomInfoPreview() = ElementPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Not encrypted",
            icon = CompoundIcons.LockOff(),
            type = MatrixBadgeAtom.Type.Info,
        )
    )
}
