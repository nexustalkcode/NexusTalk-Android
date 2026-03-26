/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 撰写框警告分子组件
 *
 * 用于在消息撰写框区域显示警告信息的组件。
 * 支持多种警告级别（默认、信息、关键），带有渐变背景和可选图标。
 * 常用于身份验证变更提示、敏感操作警告等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.toAnnotatedString
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 警告级别枚举
 *
 * 定义 ComposerAlertMolecule 组件的不同警告级别。
 * 每种级别对应不同的颜色方案和图标。
 */
enum class ComposerAlertLevel {
    /** 默认级别 - 使用信息相关的颜色 */
    Default,
    /** 信息级别 - 用于一般性提示信息 */
    Info,
    /** 关键级别 - 用于需要特别注意的警告或错误 */
    Critical
}

/**
 * 撰写框警告组件
 *
 * 创建一个带有警告信息的组件，包含可选头像或图标、文本内容和提交按钮。
 * 组件顶部有一条彩色分割线，下方使用渐变背景。
 *
 * @param avatar AvatarData? 可选的用户头像数据，为 null 时显示图标（如果 showIcon 为 true）
 * @param content AnnotatedString 警告文本内容，支持富文本
 * @param onSubmitClick () -> Unit 点击提交按钮时的回调函数
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param level ComposerAlertLevel 警告级别，默认为 Default
 * @param showIcon Boolean 是否显示图标（当 avatar 为 null 时），默认为 false
 * @param submitText String 提交按钮文本，默认为"确定"
 *
 * @return Unit
 *
 * @see [ComposerAlertLevel] 警告级别枚举
 * @see [Avatar] 头像组件
 * @see [Button] 按钮组件
 *
 * @example
 * ```kotlin
 * ComposerAlertMolecule(
 *     avatar = avatarData,
 *     content = "用户身份已变更".toAnnotatedString(),
 *     level = ComposerAlertLevel.Info,
 *     onSubmitClick = { /* 处理确认 */ }
 * )
 * ```
 */
@Composable
fun ComposerAlertMolecule(
    avatar: AvatarData?,
    content: AnnotatedString,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    level: ComposerAlertLevel = ComposerAlertLevel.Default,
    showIcon: Boolean = false,
    submitText: String = stringResource(CommonStrings.action_ok),
) {
    Column(
        modifier.fillMaxWidth()
    ) {
        val lineColor = when (level) {
            ComposerAlertLevel.Default -> ElementTheme.colors.borderInfoSubtle
            ComposerAlertLevel.Info -> ElementTheme.colors.borderInfoSubtle
            ComposerAlertLevel.Critical -> ElementTheme.colors.borderCriticalSubtle
        }

        val startColor = when (level) {
            ComposerAlertLevel.Default -> ElementTheme.colors.bgInfoSubtle
            ComposerAlertLevel.Info -> ElementTheme.colors.bgInfoSubtle
            ComposerAlertLevel.Critical -> ElementTheme.colors.bgCriticalSubtle
        }

        val textColor = when (level) {
            ComposerAlertLevel.Default -> ElementTheme.colors.textPrimary
            ComposerAlertLevel.Info -> ElementTheme.colors.textInfoPrimary
            ComposerAlertLevel.Critical -> ElementTheme.colors.textCriticalPrimary
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(lineColor)
        )
        val brush = Brush.verticalGradient(
            listOf(startColor, ElementTheme.colors.bgCanvasDefault),
        )
        Box(
            modifier = Modifier
                .background(brush)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (avatar != null) {
                        Avatar(
                            avatarData = avatar,
                            avatarType = AvatarType.User,
                        )
                    } else if (showIcon) {
                        val icon = when (level) {
                            ComposerAlertLevel.Default -> CompoundIcons.Info()
                            ComposerAlertLevel.Info -> CompoundIcons.Info()
                            ComposerAlertLevel.Critical -> CompoundIcons.Error()
                        }
                        val iconTint = when (level) {
                            ComposerAlertLevel.Default -> ElementTheme.colors.iconPrimary
                            ComposerAlertLevel.Info -> ElementTheme.colors.iconInfoPrimary
                            ComposerAlertLevel.Critical -> ElementTheme.colors.iconCriticalPrimary
                        }
                        Icon(
                            imageVector = icon,
                            tint = iconTint,
                            contentDescription = null,
                        )
                    }
                    Text(
                        text = content,
                        modifier = Modifier.weight(1f),
                        style = ElementTheme.typography.fontBodyMdRegular,
                        color = textColor,
                        textAlign = TextAlign.Start,
                    )
                }
                Button(
                    text = submitText,
                    size = ButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSubmitClick,
                )
            }
        }
    }
}


/**
 * ComposerAlertMolecule 预览组件
 *
 * 用于在设计预览中展示 ComposerAlertMolecule 组件的各种状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun ComposerAlertMoleculePreview(
    @PreviewParameter(ComposerAlertMoleculeParamsProvider::class) params: ComposerAlertMoleculeParams,
) = ElementPreview {
    ComposerAlertMolecule(
        avatar = params.avatar,
        content = "Alice's verified identity has changed. Learn more".toAnnotatedString(),
        level = params.level,
        showIcon = params.showIcon,
        onSubmitClick = {},
    )
}
