/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.element.android.libraries.architecture.Presenter
import timber.log.Timber

private const val incomingCallOverlayTraceTag = "IncomingCallOverlayTrace"

/**
 * 使用 [Presenter] 驱动来电 overlay 宿主。
 *
 * @param presenter 负责提供 [IncomingCallOverlayState] 的展示层状态源。
 * @param modifier 应用于宿主容器的修饰符。
 */
@Composable
fun IncomingCallOverlayHost(
    presenter: Presenter<IncomingCallOverlayState>,
    modifier: Modifier = Modifier,
) {
    IncomingCallOverlayHost(
        state = presenter.present(),
        modifier = modifier,
    )
}

/**
 * 直接使用既有状态渲染来电 overlay 宿主。
 *
 * 该重载适合在 Activity 或测试环境中直接注入状态，而不强制依赖 Presenter。
 *
 * @param state 当前需要展示的 overlay 状态。
 * @param modifier 应用于宿主容器的修饰符。
 */
@Composable
fun IncomingCallOverlayHost(
    state: IncomingCallOverlayState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.isVisible, state.calls.size) {
        Timber.tag(incomingCallOverlayTraceTag).w(
            "IncomingCallOverlayHost composed visible=%s callCount=%s",
            state.isVisible,
            state.calls.size,
        )
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        IncomingCallOverlayView(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
