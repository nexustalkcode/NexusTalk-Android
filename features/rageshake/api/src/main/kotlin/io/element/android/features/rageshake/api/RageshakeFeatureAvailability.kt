/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api

import kotlinx.coroutines.flow.Flow

/**
 * 崩溃检测功能可用性接口
 *
 * 用于检查崩溃检测功能是否可用的函数式接口。
 */
fun interface RageshakeFeatureAvailability {
    /**
     * 检查功能是否可用
     *
     * @return Flow<Boolean> 是否可用的布尔值流
     */
    fun isAvailable(): Flow<Boolean>
}
