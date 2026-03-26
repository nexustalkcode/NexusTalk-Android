/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.logs

import java.io.File

/**
 * 日志文件删除器接口
 *
 * 定义了删除日志文件的操作接口，用于清理过期的日志文件。
 */
interface LogFilesRemover {
    /**
     * 执行日志文件删除操作
     *
     * 根据谓词条件删除日志文件。默认情况下会删除所有文件。
     *
     * @param predicate 用于过滤要删除的文件的谓词函数。默认删除所有文件。
     */
    suspend fun perform(predicate: (File) -> Boolean = { true })
}
