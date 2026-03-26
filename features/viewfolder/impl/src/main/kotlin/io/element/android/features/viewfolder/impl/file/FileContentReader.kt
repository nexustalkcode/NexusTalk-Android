/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.runCatchingExceptions
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 文件内容读取接口
 *
 * 定义读取文件内容的功能接口，支持异步加载文件行列表。
 * 该接口采用协程实现，确保文件操作的异步性和非阻塞性。
 *
 * @see DefaultFileContentReader 默认实现
 */
interface FileContentReader {
    /**
     * 读取文件的行列表
     *
     * @param path 文件路径
     * @return 文件内容行列表的 Result包装
     */
    suspend fun getLines(path: String): Result<List<String>>
}

/**
 * FileContentReader 的默认实现
 *
 * 提供实际的文件内容读取功能，将文件读取为行列表。
 * 使用协程调度器确保在 IO 线程执行文件操作。
 *
 * @property dispatchers 协程调度器
 * @see FileContentReader 文件内容读取接口
 */
@ContributesBinding(AppScope::class)
class DefaultFileContentReader(
    private val dispatchers: CoroutineDispatchers,
) : FileContentReader {
    /**
     * 读取文件内容
     *
     * @param path 文件路径
     * @return 文件行列表
     */
    override suspend fun getLines(path: String): Result<List<String>> = withContext(dispatchers.io) {
        runCatchingExceptions {
            File(path).readLines()
        }
    }
}
