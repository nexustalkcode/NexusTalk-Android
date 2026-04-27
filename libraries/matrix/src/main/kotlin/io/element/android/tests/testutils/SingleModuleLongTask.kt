/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.tests.testutils

/**
 * matrix 单模块过渡期，本地为并入主源码集的 fake 提供一个零成本的长任务包装。
 * 这里保持 inline，允许既有 fake 在 lambda 中继续使用非局部 return。
 */
inline suspend fun <T> simulateLongTask(block: () -> T): T = block()
