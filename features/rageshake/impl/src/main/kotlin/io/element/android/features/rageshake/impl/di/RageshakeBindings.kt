/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.rageshake.impl.crash.PreferencesCrashDataStore

/**
 * Rageshake 依赖绑定接口
 *
 * 定义 Rageshake 模块需要提供的依赖绑定。
 */
@ContributesTo(AppScope::class)
interface RageshakeBindings {
    /**
     * 获取偏好设置崩溃数据存储
     *
     * @return PreferencesCrashDataStore 崩溃数据存储实例
     */
    fun preferencesCrashDataStore(): PreferencesCrashDataStore
}
