/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.info

import android.content.Context
import io.element.android.libraries.androidutils.system.getVersionCodeFromManifest
import io.element.android.x.BuildConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 记录应用版本和构建信息的工具函数。
 *
 * 将应用版本号、构建类型、Git 提交 SHA、SDK 版本等关键信息
 * 通过 Timber 日志框架输出到日志系统。
 * 用于调试和问题排查时快速获取应用的版本信息。
 *
 * @param context 应用上下文，用于获取版本码等系统信息
 */
fun logApplicationInfo(context: Context) {
    val appVersion = buildString {
        append(BuildConfig.VERSION_NAME)
        append(" (")
        append(context.getVersionCodeFromManifest())
        append(") - ")
        append(BuildConfig.BUILD_TYPE)
        append(" / ")
        append(BuildConfig.FLAVOR)
    }
    // TODO 需要以某种方式获取 SDK 版本
    val sdkVersion = "SDK VERSION (TODO)"
    val date = SimpleDateFormat("MM-dd HH:mm:ss.SSSZ", Locale.US).format(Date())

    Timber.d("----------------------------------------------------------------")
    Timber.d("----------------------------------------------------------------")
    Timber.d(" Application version: $appVersion")
    Timber.d(" Git SHA: ${BuildConfig.GIT_REVISION}")
    Timber.d(" SDK version: $sdkVersion")
    Timber.d(" Local time: $date")
    Timber.d("----------------------------------------------------------------")
    Timber.d("----------------------------------------------------------------\n\n\n\n")
}
