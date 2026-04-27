/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav.di

import io.element.android.libraries.matrix.api.MatrixClient

/**
 * 创建 Session 级依赖图的工厂接口。
 */
interface SessionGraphFactory {
    /**
     * 基于当前 MatrixClient 创建 Session 图。
     */
    fun create(client: MatrixClient): Any
}
