/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.networkmonitor.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp

/** 指示器垂直内边距常量 */
private val INDICATOR_VERTICAL_PADDING = 6.dp

/**
 * 网络连接指示器容器组件
 *
 * 使用 Jetpack Compose 实现网络连接指示器的容器。
 * 当设备离线时显示连接指示器，并提供平滑的动画效果。
 * 容器内部会消耗窗口边距，避免重复的内边距。
 *
 * @param isOnline 是否在线
 * @param modifier 修饰符
 * @param content 子内容组件
 */
@Composable
fun ConnectivityIndicatorContainer(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit = {},
) {
    val isIndicatorVisible = remember { MutableTransitionState(!isOnline) }.apply { targetState = !isOnline }
    Column(modifier = modifier) {
        val statusBarTopPadding = if (LocalInspectionMode.current) {
            // Needed to get valid UI previews
            24.dp
        } else {
            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + INDICATOR_VERTICAL_PADDING
        }
        val target = if (isIndicatorVisible.targetState) statusBarTopPadding else 0.dp
        val topWindowInset by animateDpAsState(
            targetValue = target,
            animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = 1.dp,
            ),
            label = "insets-animation",
        )
        // Display the network indicator with an animation
        AnimatedVisibility(
            visibleState = isIndicatorVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            ConnectivityIndicator(verticalPadding = INDICATOR_VERTICAL_PADDING)
        }
        // Consume the window insets to avoid double padding.
        content(
            Modifier.consumeWindowInsets(PaddingValues(top = topWindowInset))
        )
    }
}
