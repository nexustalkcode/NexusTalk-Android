/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.tests.testutils.lambda

/**
 * 单模块合并后，不再让主源码去依赖 tests:testutils。
 * 这里提供 matrix fake 当前实际需要的最小 helper 集，默认失败信息也改成可直接定位测试缺口。
 */
fun lambdaError(message: String = "This fake callback should be provided in tests"): Nothing = error(message)

fun <R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: () -> R,
): () -> R {
    if (ensureNeverCalled) {
        return {
            error("This recorded lambda should never be called")
        }
    }
    return block
}

fun <P1, R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: (P1) -> R,
): (P1) -> R {
    if (ensureNeverCalled) {
        return { _ ->
            error("This recorded lambda should never be called")
        }
    }
    return block
}

fun <P1, P2, R> lambdaRecorder(
    ensureNeverCalled: Boolean = false,
    block: (P1, P2) -> R,
): (P1, P2) -> R {
    if (ensureNeverCalled) {
        return { _, _ ->
            error("This recorded lambda should never be called")
        }
    }
    return block
}
