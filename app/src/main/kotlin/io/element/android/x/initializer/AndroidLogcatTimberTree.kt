/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.initializer

import android.os.Build
import android.util.Log
import timber.log.Timber

private const val DEFAULT_LOG_TAG = "elementx"
private const val LEGACY_TAG_LIMIT = 23

/**
 * 直接写入 Android system log 的 Timber Tree。
 *
 * 现有日志链路会先把 Timber 日志转发到 Rust tracing，再由 SDK 决定是否写入 system log。
 * 在个别 Android 10 华为设备上，文件日志能够正常生成，但 `adb logcat` / Android Studio Logcat 看不到这些应用日志。
 * 这里增加一个仅用于设备兼容兜底的 Tree，让 Kotlin 侧关键 Timber 日志仍然可以直接进入 Logcat，
 * 避免现场排查时只能依赖 rageshake 文件日志。
 */
internal class AndroidLogcatTimberTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val resolvedTag = resolveTag(tag)
        val resolvedMessage = buildString {
            append(message)
            if (t != null) {
                appendLine()
                append(Log.getStackTraceString(t))
            }
        }

        resolvedMessage
            .lineSequence()
            .ifEmpty { sequenceOf("") }
            .forEach { line ->
                Log.println(priority, resolvedTag, line)
            }
    }

    /**
     * 旧系统对 tag 长度更敏感，兜底时主动裁剪，避免兼容层再次吞日志。
     */
    private fun resolveTag(tag: String?): String {
        val candidate = tag?.takeIf { it.isNotBlank() } ?: DEFAULT_LOG_TAG
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            candidate.take(LEGACY_TAG_LIMIT)
        } else {
            candidate
        }
    }
}
