/*
 * Copyright (c) 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.tests.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * 这里只补 analytics 测试实际使用到的最小生命周期替身，
 * 让 ScreenTracker 相关单测在不依赖全局 tests:testutils 的情况下继续编译。
 */
class FakeLifecycleOwner(
    initialState: Lifecycle.State = Lifecycle.State.INITIALIZED,
) : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this).apply {
        currentState = initialState
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun givenState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }
}

@Composable
fun withFakeLifecycleOwner(
    lifecycleOwner: LifecycleOwner,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
        content()
    }
}
