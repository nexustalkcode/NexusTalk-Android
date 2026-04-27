/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.initializer

import android.content.Context
import android.os.Build
import android.system.Os
import androidx.startup.Initializer
import io.element.android.features.rageshake.api.logs.createWriteToFilesConfiguration
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.meta.BuildMeta
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
 * 在应用启动时负责完成以下工作：
 * - 初始化 Timber 日志链路
 * - 初始化 SDK tracing 配置
 * - 配置 bug report 使用的文件日志
 * - 设置 Rust backtrace 环境变量
 */
class PlatformInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val appBindings = context.bindings<AppBindings>()
        val tracingService = appBindings.tracingService()
        val platformService = appBindings.platformService()
        val bugReporter = appBindings.bugReporter()
        val preferencesStore = appBindings.preferencesStore()
        val featureFlagService = appBindings.featureFlagService()
        val buildMeta = appBindings.buildMeta()
        val logLevel = runBlocking { preferencesStore.getTracingLogLevelFlow().first() }
        val writesToLogcat = runBlocking { featureFlagService.isFeatureEnabled(FeatureFlags.PrintLogsToLogcat) }

        Timber.plant(tracingService.createTimberTree(ELEMENT_X_TARGET))
        if (shouldPlantAndroidLogcatFallback(buildMeta = buildMeta, writesToLogcat = writesToLogcat)) {
            // 仅在已复现的华为 Android 10 调试环境种额外 Tree，尽量避免在正常设备上重复写日志。
            Timber.plant(AndroidLogcatTimberTree())
        }

        val tracingConfiguration = TracingConfiguration(
            writesToLogcat = writesToLogcat,
            writesToFilesConfiguration = bugReporter.createWriteToFilesConfiguration(),
            logLevel = logLevel,
            extraTargets = listOf(ELEMENT_X_TARGET),
            traceLogPacks = runBlocking { preferencesStore.getTracingLogPacksFlow().first() },
            sdkSentryDsn = appBindings.sentrySdkDsn()?.value?.takeIf { it.isNotBlank() },
        )
        bugReporter.setCurrentTracingLogLevel(logLevel.name)
        platformService.init(tracingConfiguration)
        Os.setenv("RUST_BACKTRACE", "1", true)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = mutableListOf()
}

/**
 * 华为 Android 10 真机上已经复现过：Rust tracing 文件日志正常，但 system log 不出应用日志。
 * 这里用最小范围的设备门控打开 Logcat fallback，优先保证现场调试可观测性。
 */
private fun shouldPlantAndroidLogcatFallback(
    buildMeta: BuildMeta,
    writesToLogcat: Boolean,
): Boolean {
    return writesToLogcat &&
        buildMeta.isDebuggable &&
        Build.VERSION.SDK_INT == Build.VERSION_CODES.Q &&
        Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)
}
