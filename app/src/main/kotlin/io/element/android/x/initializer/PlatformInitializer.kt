/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.initializer

import android.content.Context
import android.system.Os
import androidx.startup.Initializer
import io.element.android.features.rageshake.api.logs.createWriteToFilesConfiguration
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.tracing.TracingConfiguration
import io.element.android.x.di.AppBindings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

private const val ELEMENT_X_TARGET = "elementx"

/**
 * 平台初始化器。
 *
 * 实现 androidx.startup.Initializer 接口，
 * 在应用启动时完成以下初始化工作：
 * - 配置并初始化 Timber 日志系统
 * - 设置应用追踪配置（Logging、Sentry 等）
 * - 初始化平台特定服务
 * - 配置 Rust 堆栈回溯环境变量
 *
 * 依赖 AppBindings 获取追踪服务、平台服务、错误报告器等组件。
 */
class PlatformInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val appBindings = context.bindings<AppBindings>()
        val tracingService = appBindings.tracingService()
        val platformService = appBindings.platformService()
        val bugReporter = appBindings.bugReporter()
        Timber.plant(tracingService.createTimberTree(ELEMENT_X_TARGET))
        val preferencesStore = appBindings.preferencesStore()
        val featureFlagService = appBindings.featureFlagService()
        val logLevel = runBlocking { preferencesStore.getTracingLogLevelFlow().first() }
        val tracingConfiguration = TracingConfiguration(
            writesToLogcat = runBlocking { featureFlagService.isFeatureEnabled(FeatureFlags.PrintLogsToLogcat) },
            writesToFilesConfiguration = bugReporter.createWriteToFilesConfiguration(),
            logLevel = logLevel,
            extraTargets = listOf(ELEMENT_X_TARGET),
            traceLogPacks = runBlocking { preferencesStore.getTracingLogPacksFlow().first() },
            sdkSentryDsn = appBindings.sentrySdkDsn()?.value?.takeIf { it.isNotBlank() },
        )
        bugReporter.setCurrentTracingLogLevel(logLevel.name)
        platformService.init(tracingConfiguration)
        // 同时设置 Rust 堆栈回溯的环境变量
        Os.setenv("RUST_BACKTRACE", "1", true)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = mutableListOf()
}
