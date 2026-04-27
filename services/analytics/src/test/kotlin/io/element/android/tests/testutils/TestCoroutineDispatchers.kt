/*
 * Copyright (c) 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.tests.testutils

import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * analytics 单模块化后，当前工作区里的全局 tests:testutils 还未接回项目图。
 * 这里先在本模块测试源码里提供最小 dispatcher helper，避免继续被仓库级缺口阻塞。
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun testCoroutineDispatchers(): CoroutineDispatchers {
    val dispatcher = UnconfinedTestDispatcher()
    return CoroutineDispatchers(
        io = dispatcher,
        computation = dispatcher,
        main = dispatcher,
    )
}
