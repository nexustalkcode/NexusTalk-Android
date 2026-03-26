/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.cachecleaner.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.cachecleaner.api.CacheCleaner
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.di.CacheDirectory
import io.element.android.libraries.di.annotations.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

/**
 * CacheCleaner 的默认实现
 *
 * 提供实际的缓存清理功能，清理临时解密的内容缓存目录。
 * 使用协程在后台线程执行清理操作，不阻塞主线程。
 *
 * 清理的子目录包括：
 * - temp/media: 临时媒体文件缓存
 * - temp/voice: 临时语音消息缓存
 *
 * @property coroutineScope 应用级协程作用域
 * @property dispatchers 协程调度器
 * @property cacheDir 缓存目录
 * @see CacheCleaner 缓存清理接口
 */
@ContributesBinding(AppScope::class)
class DefaultCacheCleaner(
    @AppCoroutineScope
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    @CacheDirectory private val cacheDir: File,
) : CacheCleaner {
    companion object {
        /** 要清理的缓存子目录列表 */
        val SUBDIRS_TO_CLEANUP = listOf("temp/media", "temp/voice")
    }

    /**
     * 清理缓存目录
     *
     * 遍历要清理的子目录，删除现有文件并重新创建空目录。
     * 使用协程在 IO 线程执行，不会阻塞主线程。
     */
    override fun clearCache() {
        coroutineScope.launch(dispatchers.io) {
            runCatchingExceptions {
                SUBDIRS_TO_CLEANUP.forEach {
                    File(cacheDir.path, it).apply {
                        if (exists()) {
                            if (!deleteRecursively()) error("Failed to delete recursively cache directory $this")
                        }
                        if (!mkdirs()) error("Failed to create cache directory $this")
                    }
                }
            }.onFailure {
                Timber.e(it, "Failed to clear cache")
            }
        }
    }
}
