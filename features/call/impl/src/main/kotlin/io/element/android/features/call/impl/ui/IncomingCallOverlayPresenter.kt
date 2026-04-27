/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import io.element.android.libraries.architecture.Presenter
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber

private const val incomingCallOverlayTraceTag = "IncomingCallOverlayTrace"

/**
 * Incoming-call overlay 的轻量 Presenter。
 *
 * 当前文件只负责把外部准备好的来电列表包成稳定的 UI state，不在这里引入任何具体业务依赖。
 * 后续接入真正的多来电管理器时，只需要在宿主层构造 `calls`，不需要重写这层 UI。
 */
class IncomingCallOverlayPresenter(
    private val calls: List<IncomingCallOverlayCall>,
) : Presenter<IncomingCallOverlayState> {
    @Composable
    override fun present(): IncomingCallOverlayState {
        val state = IncomingCallOverlayState(
            calls = calls.toImmutableList(),
        )
        SideEffect {
            Timber.tag(incomingCallOverlayTraceTag).i(
                "IncomingCallOverlayPresenter.present callCount=%s visible=%s",
                state.calls.size,
                state.isVisible,
            )
        }
        return state
    }
}
