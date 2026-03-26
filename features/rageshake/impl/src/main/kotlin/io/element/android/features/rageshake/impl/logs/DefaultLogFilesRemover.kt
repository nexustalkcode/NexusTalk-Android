/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.logs

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.rageshake.api.logs.LogFilesRemover
import io.element.android.features.rageshake.impl.reporter.DefaultBugReporter
import java.io.File

/**
 * 默认日志文件删除器
 *
 * LogFilesRemover 接口的实现，使用 DefaultBugReporter 删除日志文件。
 *
 * @property bugReporter 问题报告器
 */
@ContributesBinding(AppScope::class)
class DefaultLogFilesRemover(
    private val bugReporter: DefaultBugReporter,
) : LogFilesRemover {
    /**
     * 执行日志文件删除
     *
     * 根据谓词条件删除日志文件。
     *
     * @param predicate 用于过滤要删除的文件的谓词函数
     */
    override suspend fun perform(predicate: (File) -> Boolean) {
        bugReporter.deleteAllFiles(predicate)
    }
}
