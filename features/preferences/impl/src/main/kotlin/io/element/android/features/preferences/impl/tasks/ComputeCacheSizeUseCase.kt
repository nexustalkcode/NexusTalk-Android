/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.tasks

import android.content.Context
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.androidutils.file.getSizeOfFiles
import io.element.android.libraries.androidutils.filesize.FileSizeFormatter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.matrix.api.MatrixClient
import kotlinx.coroutines.withContext

/**
 * 计算缓存大小用例接口
 */
interface ComputeCacheSizeUseCase {
    /**
     * 计算当前缓存大小
     *
     * @return 格式化的缓存大小字符串
     */
    suspend operator fun invoke(): String
}

/**
 * 默认计算缓存大小用例实现
 *
 * 负责计算应用当前使用的缓存总大小，包括 Matrix 缓存和应用缓存目录。
 *
 * @property context 应用上下文
 * @property matrixClient Matrix 客户端
 * @property coroutineDispatchers 协程调度器
 * @property fileSizeFormatter 文件大小格式化器
 */
@ContributesBinding(SessionScope::class)
class DefaultComputeCacheSizeUseCase(
    @ApplicationContext private val context: Context,
    private val matrixClient: MatrixClient,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val fileSizeFormatter: FileSizeFormatter,
) : ComputeCacheSizeUseCase {
    override suspend fun invoke(): String = withContext(coroutineDispatchers.io) {
        var cumulativeSize = 0L
        cumulativeSize += matrixClient.getCacheSize()
        // - 4096 to not include the size fo the folder
        cumulativeSize += (context.cacheDir.getSizeOfFiles() - 4096).coerceAtLeast(0)
        fileSizeFormatter.format(cumulativeSize)
    }
}
