/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.logs

import io.element.android.features.rageshake.api.reporter.BugReporter
import io.element.android.libraries.matrix.api.tracing.WriteToFilesConfiguration

/**
 * 为 BugReporter 创建日志文件写入配置的扩展函数
 *
 * 根据 BugReporter 的日志目录创建一个写入配置文件，用于配置日志文件的写入策略。
 * 默认保留最多一周的日志文件（每小时一个，共7*24=168个）。
 *
 * @return WriteToFilesConfiguration 启用的日志文件写入配置
 */
fun BugReporter.createWriteToFilesConfiguration(): WriteToFilesConfiguration {
    return WriteToFilesConfiguration.Enabled(
        directory = logDirectory().absolutePath,
        filenamePrefix = "logs",
        // Keep a maximum of 1 week of log files.
        numberOfFiles = 7 * 24,
    )
}
