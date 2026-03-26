/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.location.api.BuildConfig
import io.element.android.features.location.api.LocationService

/**
 * 默认位置服务实现
 *
 * 通过检查 MapTiler API 密钥是否配置来确定位置服务是否可用。
 */
@ContributesBinding(AppScope::class)
class DefaultLocationService : LocationService {
    /**
     * 检查位置服务是否可用
     *
     * 通过判断 MapTiler API 密钥是否已配置来确定服务可用性。
     *
     * @return Boolean 如果 API 密钥已配置且非空则返回 true
     */
    override fun isServiceAvailable(): Boolean {
        return BuildConfig.MAPTILER_API_KEY.isNotEmpty()
    }
}
