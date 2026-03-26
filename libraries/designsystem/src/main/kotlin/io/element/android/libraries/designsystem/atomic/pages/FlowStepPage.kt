/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 流程步骤页面组件
 *
 * 用于构建流程引导页面的通用组件。
 * 包含顶部导航栏（可选返回按钮）、图标标题副标题头部、内容区域和按钮底部。
 * 适用于登录注册、设置向导等多步骤流程页面。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar

/**
 * 流程步骤页面组件
 *
 * 创建一个包含标准流程布局的页面。
 * 支持返回按钮、滚动内容、图标标题副标题头部和按钮底部。
 *
 * @param iconStyle BigIcon.Style 大图标样式
 * @param title String 标题文本
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param isScrollable Boolean 内容是否可滚动，默认为 false
 * @param onBackClick (() -> Unit)? 返回按钮点击回调，为 null 时不显示返回按钮
 * @param subTitle String? 副标题文本，默认为 null
 * @param buttons @Composable ColumnScope.() -> Unit 底部按钮区域内容
 * @param content @Composable () -> Unit 页面主体内容
 *
 * @return Unit
 *
 * @see [HeaderFooterPage] 页眉页脚基础组件
 * @see [IconTitleSubtitleMolecule] 图标标题副标题组件
 * @see [ButtonColumnMolecule] 按钮列组件
 *
 * @example
 * ```kotlin
 * FlowStepPage(
 *     title = "验证身份",
 *     subTitle = "请输入验证码",
 *     iconStyle = BigIcon.Style.Success,
 *     onBackClick = { /* 返回 */ },
 *     buttons = {
 *         Button(text = "确认", onClick = { /* 确认 */ })
 *     }
 * ) {
 *     // 验证码输入组件
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowStepPage(
    iconStyle: BigIcon.Style,
    title: String,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    subTitle: String? = null,
    buttons: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    BackHandler(enabled = onBackClick != null) {
        onBackClick?.invoke()
    }
    HeaderFooterPage(
        modifier = modifier,
        isScrollable = isScrollable,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (onBackClick != null) {
                        BackButton(onClick = onBackClick)
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(bottom = 16.dp),
                title = title,
                subTitle = subTitle,
                iconStyle = iconStyle,
            )
        },
        content = content,
        footer = {
            ButtonColumnMolecule(
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                buttons()
            }
        }
    )
}

/**
 * FlowStepPage 预览组件
 *
 * 用于在设计预览中展示 FlowStepPage 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun FlowStepPagePreview() = ElementPreview {
    FlowStepPage(
        onBackClick = {},
        title = "Title",
        subTitle = "Subtitle",
        iconStyle = BigIcon.Style.Default(CompoundIcons.Computer()),
        buttons = {
            TextButton(text = "A button", onClick = { })
            Button(text = "Continue", onClick = { })
        }
    ) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Content",
                style = ElementTheme.typography.fontHeadingXlBold
            )
        }
    }
}
