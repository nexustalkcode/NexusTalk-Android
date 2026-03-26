/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 日落主题页面组件
 *
 * 用于显示日落渐变背景效果的特殊页面。
 * 自动应用与当前主题相反的颜色方案，
 * 适用于迁移页面、特殊状态页面等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.annotations.CoreColorToken
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.internal.DarkColorTokens
import io.element.android.compound.tokens.generated.internal.LightColorTokens
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.withColoredPeriod
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 日落主题页面组件
 *
 * 创建一个具有日落渐变背景效果的页面。
 * 标题带有彩色句号装饰，支持加载状态显示。
 *
 * @param isLoading Boolean 是否显示加载状态
 * @param title String 标题文本
 * @param subtitle String 副标题文本
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param overallContent @Composable () -> Unit 页面整体内容
 *
 * @return Unit
 *
 * @see [withColoredPeriod] 带彩色句号的文本处理
 *
 * @example
 * ```kotlin
 * SunsetPage(
 *     isLoading = false,
 *     title = "迁移完成",
 *     subtitle = "您的数据已成功迁移",
 *     overallContent = {
 *         Button("完成", onClick = { /* 完成 */ })
 *     }
 * )
 * ```
 */
@Composable
fun SunsetPage(
    isLoading: Boolean,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    overallContent: @Composable () -> Unit,
) {
    ElementTheme(
        // Always use the opposite value of the current theme
        darkTheme = ElementTheme.isLightTheme,
        applySystemBarsUpdate = false,
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            SunsetBackground()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = BiasAbsoluteAlignment(
                        horizontalBias = 0f,
                        verticalBias = -0.05f
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = ElementTheme.colors.iconPrimary
                            )
                        } else {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = withColoredPeriod(title),
                            style = ElementTheme.typography.fontHeadingXlBold,
                            textAlign = TextAlign.Center,
                            color = ElementTheme.colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.widthIn(max = 360.dp),
                            text = subtitle,
                            style = ElementTheme.typography.fontBodyLgRegular,
                            textAlign = TextAlign.Center,
                            color = ElementTheme.colors.textPrimary,
                        )
                    }
                }
                overallContent()
            }
        }
    }
}

/**
 * 日落背景组件
 *
 * SunsetPage 的内部组件，用于绘制日落渐变背景。
 * 根据当前主题自动选择相反的颜色方案。
 */
@OptIn(CoreColorToken::class)
@Composable
private fun SunsetBackground() {
    Column(modifier = Modifier.fillMaxSize()) {
        // The top background colors are the opposite of the current theme ones
        val topBackgroundColor = if (ElementTheme.isLightTheme) {
            DarkColorTokens.colorThemeBg
        } else {
            LightColorTokens.colorThemeBg
        }
        // The bottom background colors follow the current theme
        val bottomBackgroundColor = if (ElementTheme.isLightTheme) {
            LightColorTokens.colorThemeBg
        } else {
            // The dark background color doesn't 100% match the image, so we use a custom color
            Color(0xFF121418)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .background(topBackgroundColor)
        )
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.bg_migration),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(bottomBackgroundColor)
        )
    }
}

/**
 * SunsetPage 预览组件
 *
 * 用于在设计预览中展示 SunsetPage 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun SunsetPagePreview() = ElementPreview {
    SunsetPage(
        isLoading = true,
        title = "Title with a green period.",
        subtitle = "Subtitle",
        overallContent = {}
    )
}
