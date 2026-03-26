/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.GradientCircularProgressIndicator

/**
 * 自定义品牌启动页面组件
 *
 * 包含：
 * - 100dp x 100dp 的 Logo 占位区域
 * - 30sp 的 "NexusTalk" 文字
 * - 底部圆形进度条
 */
@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgCanvasDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Spacer(modifier = Modifier.weight(0.7f))
        // Logo - 100dp x 100dp
        Image(
            painter = painterResource(id = io.element.android.appicon.element.R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // NexusTalk 文字 - 30sp
        Text(
            text = stringResource(io.element.android.appconfig.R.string.app_name),
            style = ElementTheme.typography.fontHeadingLgBold.copy(
                fontSize = 30.sp
            ),
            color = ElementTheme.colors.textPrimary,
        )

        Spacer(modifier = Modifier.weight(1f))

        // 圆形进度条 - 48dp
        GradientCircularProgressIndicator(
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview
@Composable
private fun SplashScreenContentPreview() {
    ElementPreview {
        SplashScreenContent()
    }
}

@PreviewsDayNight
@Composable
private fun SplashScreenContentFullPreview() {
    ElementPreview {
        Column {
            // 默认颜色
            SplashScreenContent()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

