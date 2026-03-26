/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api

/**
 * 位置服务接口
 *
 * 定义了位置服务的基本功能，用于检查位置服务是否可用。
 */
interface LocationService {
    /**
     * 检查位置服务是否可用
     *
     * @return Boolean 如果位置服务可用返回 true，否则返回 false
     */
    fun isServiceAvailable(): Boolean
}
