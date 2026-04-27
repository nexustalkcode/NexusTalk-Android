/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.core.coroutine

import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

/**
 * 包装一个挂起任务，保证其整体执行至少持续指定时长。
 */
fun suspendWithMinimumDuration(
    minimumDurationMillis: Long = 500,
    block: suspend () -> Unit
) = suspend {
    val duration = measureTimeMillis {
        block()
    }
    delay(minimumDurationMillis - duration)
}
