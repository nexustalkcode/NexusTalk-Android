/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.location.api.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 静态地图占位符组件
 *
 * 在静态地图加载过程中或加载失败时显示的占位符。
 * 显示模糊的背景地图图片，可选的加载指示器，以及在可重新加载时显示的重试按钮。
 *
 * @param showProgress 是否显示加载进度指示器
 * @param canReload 是否可以重新加载地图
 * @param contentDescription 图片的无障碍描述
 * @param width 占位符宽度
 * @param height 占位符高度
 * @param onLoadMapClick 点击重新加载地图的回调
 * @param modifier 修饰符
 */
@Composable
internal fun StaticMapPlaceholder(
    showProgress: Boolean,
    canReload: Boolean,
    contentDescription: String?,
    width: Dp,
    height: Dp,
    onLoadMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = width, height = height)
            .then(if (showProgress) Modifier else Modifier.clickable(onClick = onLoadMapClick))
    ) {
        Image(
            painter = painterResource(id = R.drawable.blurred_map),
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(width = width, height = height)
        )
        if (showProgress) {
            CircularProgressIndicator()
        } else if (canReload) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = CompoundIcons.Restart(),
                    contentDescription = null
                )
                Text(text = stringResource(id = CommonStrings.action_static_map_load))
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun StaticMapPlaceholderPreview() = ElementPreview {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            true to false,
            false to true,
            false to false,
        ).forEach { (showProgress, canReload) ->
            StaticMapPlaceholder(
                showProgress = showProgress,
                canReload = canReload,
                contentDescription = null,
                width = 400.dp,
                height = 200.dp,
                onLoadMapClick = {},
            )
        }
    }
}
