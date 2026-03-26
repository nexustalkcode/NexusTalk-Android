/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.di.annotations.ApplicationContext

/**
 * 画中画支持提供者接口
 *
 * 用于检查当前设备是否支持画中画功能。
 * 画中画功能需要 Android 8.0 (API 26) 及以上版本，并且设备需要支持此功能。
 *
 * @see DefaultPipSupportProvider 默认实现
 */
interface PipSupportProvider {
    /**
     * 检查设备是否支持画中画功能
     *
     * @return Boolean 如果支持画中画则返回 true，否则返回 false
     */
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.O)
    fun isPipSupported(): Boolean
}

/**
 * 画中画支持提供者默认实现
 *
 * 通过检查 Android 系统版本和设备功能来确定是否支持画中画模式。
 *
 * @param context Android 上下文，用于访问系统服务
 *
 * @see PipSupportProvider 画中画支持提供者接口
 */
@ContributesBinding(AppScope::class)
class DefaultPipSupportProvider(
    @ApplicationContext private val context: Context,
) : PipSupportProvider {
    /**
     * 检查设备是否支持画中画功能
     *
     * 需要 Android 8.0 (API 26) 及以上版本，并且设备需要具有画中画系统功能。
     *
     * @return Boolean 如果支持画中画则返回 true，否则返回 false
     */
    override fun isPipSupported(): Boolean {
        val isSupportedByTheOs = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            context.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE).orFalse()
        return isSupportedByTheOs
    }
}
