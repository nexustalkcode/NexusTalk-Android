/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 页眉页脚页面组件
 *
 * 用于构建通用页面的基础组件。
 * 包含顶部栏（可选）、页眉（可选）、主体内容和页脚（可选）。
 * 主体内容支持滚动，是页面布局的基础骨架。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 页眉页脚页面组件
 *
 * 创建一个标准页面布局，包含顶部栏、页眉、主体内容和页脚。
 * 支持内容滚动和窗口边距处理。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param contentPadding PaddingValues 内容区域内边距，默认为 PaddingValues(20.dp)
 * @param containerColor Color 容器背景颜色，默认为 [ElementTheme.colors.bgCanvasDefault]
 * @param isScrollable Boolean 内容是否可滚动，默认为 false
 * @param background @Composable () -> Unit 可选的背景组件
 * @param topBar @Composable () -> Unit 可选的顶部栏组件
 * @param header @Composable () -> Unit 可选的页眉组件
 * @param footer @Composable () -> Unit 可选的页脚组件
 * @param content @Composable () -> Unit 页面主体内容
 *
 * @return Unit
 *
 * @see [Scaffold] 脚手架组件
 *
 * @example
 * ```kotlin
 * HeaderFooterPage(
 *     header = {
 *         Text("页面标题")
 *     },
 *     content = {
 *         // 页面内容
 *     },
 *     footer = {
 *         Button("提交")
 *     }
 * )
 * ```
 */
@Suppress("NAME_SHADOWING")
@Composable
fun HeaderFooterPage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    containerColor: Color = ElementTheme.colors.bgCanvasDefault,
    isScrollable: Boolean = false,
    background: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        containerColor = containerColor,
    ) { insetsPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val contentInsetsPadding = remember(insetsPadding, layoutDirection) {
            PaddingValues(
                start = insetsPadding.calculateStartPadding(layoutDirection),
                top = insetsPadding.calculateTopPadding(),
                end = insetsPadding.calculateEndPadding(layoutDirection),
            )
        }
        val footerInsetsPadding = remember(insetsPadding, layoutDirection) {
            PaddingValues(
                start = insetsPadding.calculateStartPadding(layoutDirection),
                end = insetsPadding.calculateEndPadding(layoutDirection),
                bottom = insetsPadding.calculateBottomPadding(),
            )
        }
        Box {
            background()

            // Render in a Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = contentPadding)
                    .consumeWindowInsets(insetsPadding)
                    .imePadding(),
            ) {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .run {
                            if (isScrollable) {
                                verticalScroll(rememberScrollState())
                                    // Make sure the scrollable content takes the full available height
                                    .height(IntrinsicSize.Max)
                            } else {
                                Modifier
                            }
                        }
                        // Apply insets here so if the content is scrollable it can get below the top app bar if needed
                        .padding(contentInsetsPadding)
                        .weight(1f, fill = true),
                ) {
                    // Header
                    header()
                    Box {
                        content()
                    }
                }

                // Footer
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth()
                        .padding(footerInsetsPadding)
                ) {
                    footer()
                }
            }
        }
    }
}

/**
 * HeaderFooterPage 预览组件
 *
 * 用于在设计预览中展示 HeaderFooterPage 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun HeaderFooterPagePreview() = ElementPreview {
    HeaderFooterPage(
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Content",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        },
        header = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Header",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        },
        footer = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Footer",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        }
    )
}

/**
 * HeaderFooterPage 可滚动预览组件
 *
 * 用于在设计预览中展示 HeaderFooterPage 组件的可滚动状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun HeaderFooterPageScrollablePreview() = ElementPreview {
    HeaderFooterPage(
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Content",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        },
        header = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Header",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        },
        footer = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Footer",
                    style = ElementTheme.typography.fontHeadingXlBold
                )
            }
        },
        isScrollable = true,
    )
}
