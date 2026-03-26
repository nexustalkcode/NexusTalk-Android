/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.initializer

import android.content.Context
import androidx.startup.Initializer
import io.element.android.features.cachecleaner.impl.CacheCleanerBindings
import io.element.android.libraries.architecture.bindings

/**
 * 缓存清理初始化器。
 *
 * 实现 androidx.startup.Initializer 接口，
 * 在应用启动时自动清理缓存目录。
 * 依赖 CacheCleanerBindings 服务执行实际的缓存清理操作。
 *
 * 此初始化器没有依赖项，会在其他初始化器之前运行。
 */
class CacheCleanerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        context.bindings<CacheCleanerBindings>().cacheCleaner().clearCache()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
