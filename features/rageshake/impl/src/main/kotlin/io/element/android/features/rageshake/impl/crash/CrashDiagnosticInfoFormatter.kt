/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.crash

import android.os.Build
import io.element.android.libraries.core.meta.BuildMeta

/**
 * 生成崩溃诊断文本。
 *
 * 将应用版本、设备信息和原始崩溃内容拼接成可随 bug report 一起上报的文本块。
 */
internal fun formatCrashDiagnosticInfo(
    buildMeta: BuildMeta,
    crashInfo: String,
): String = buildString {
    appendLine("App: ${buildMeta.applicationName}")
    appendLine("Version: ${buildMeta.versionName} (${buildMeta.versionCode})")
    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}".trim())
    appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT}, ${Build.VERSION.INCREMENTAL})")
    if (buildMeta.flavorDescription.isNotEmpty()) {
        appendLine("Flavor: ${buildMeta.flavorDescription}")
    }
    if (buildMeta.gitRevision.isNotEmpty()) {
        appendLine("Git revision: ${buildMeta.gitRevision}")
    }
    appendLine()
    appendLine("Crash info:")
    append(crashInfo.ifBlank { "Unavailable" })
}
