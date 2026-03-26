/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.cachecleaner.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.cachecleaner.api.CacheCleaner

/**
 * 缓存清理绑定接口
 *
 * 提供缓存清理功能的依赖注入绑定接口。
 * 用于在依赖注入系统中提供 CacheCleaner 实例。
 *
 * @see CacheCleaner 缓存清理接口
 * @see DefaultCacheCleaner 默认实现
 */
@ContributesTo(AppScope::class)
interface CacheCleanerBindings {
    /**
     * 获取缓存清理器实例
     *
     * @return CacheCleaner 实例
     */
    fun cacheCleaner(): CacheCleaner
}
