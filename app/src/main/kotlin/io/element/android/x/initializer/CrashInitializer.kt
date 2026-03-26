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
import io.element.android.features.rageshake.impl.crash.VectorUncaughtExceptionHandler
import io.element.android.features.rageshake.impl.di.RageshakeBindings
import io.element.android.libraries.architecture.bindings

/**
 * 崩溃处理初始化器。
 *
 * 实现 androidx.startup.Initializer 接口，
 * 在应用启动时设置全局异常捕获处理器。
 * 使用 VectorUncaughtExceptionHandler 捕获未处理的异常，
 * 并将崩溃信息保存到本地存储以便后续分析。
 *
 * 此初始化器没有依赖项，会在其他初始化器之前运行。
 */
class CrashInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        VectorUncaughtExceptionHandler(
            context.bindings<RageshakeBindings>().preferencesCrashDataStore(),
        ).activate()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
