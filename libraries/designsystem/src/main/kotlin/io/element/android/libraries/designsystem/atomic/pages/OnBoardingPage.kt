/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 入引导页面组件
 *
 * 用于构建新用户引导页面的组件。
 * 包含可选背景图、主体内容和可选底部。
 * 采用系统栏内边距，适配各种设备屏幕。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 *
 * @see <a href="https://www.figma.com/file/o9p34zmiuEpZRyvZXJZAYL/FTUE?type=design&node-id=133-5427&t=5SHVppfYzjvkEywR-0">Figma 设计稿</a>
 */
package io.element.android.libraries.designsystem.atomic.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 入引导页面组件
 *
 * 创建一个新用户引导页面。
 * 支持背景图渲染、内容对齐方式和底部组件。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param renderBackground Boolean 是否渲染背景图，默认为 true
 * @param contentAlignment Alignment.Horizontal 内容的水平对齐方式，默认为 Alignment.CenterHorizontally
 * @param footer @Composable () -> Unit 可选的底部组件
 * @param content @Composable () -> Unit 页面主体内容
 *
 * @return Unit
 *
 * @see [R.drawable.onboarding_bg] 引导页背景图资源
 *
 * @example
 * ```kotlin
 * OnBoardingPage(
 *     renderBackground = true,
 *     contentAlignment = Alignment.CenterHorizontally
 * ) {
 *     Text("欢迎使用")
 * }
 * ```
 */
@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    renderBackground: Boolean = true,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    footer: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // BG
        if (renderBackground) {
            Image(
                modifier = Modifier
                    .size(700.dp,800.dp)
                    .offset(130.dp,(-300).dp),
                painter = painterResource(id = R.drawable.bg_onboarding_bg),
                contentDescription = null,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(all = 20.dp),
        ) {
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = contentAlignment,
            ) {
                content()
            }
            // Footer
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                footer()
            }
        }
    }
}

/**
 * OnBoardingPage 预览组件
 *
 * 用于在设计预览中展示 OnBoardingPage 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun OnBoardingPagePreview() = ElementPreview {
    OnBoardingPage(
        content = {
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
