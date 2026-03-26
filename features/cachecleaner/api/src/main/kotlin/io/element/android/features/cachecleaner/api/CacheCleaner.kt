/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.cachecleaner.api

/**
 * 缓存清理接口
 *
 * 定义清理应用缓存的功能接口，主要用于清除临时解密的内容缓存
 *（如媒体文件和语音消息）。
 *
 * 实现类应该在后台线程执行清理操作，并静默处理文件删除错误。
 *
 * @see DefaultCacheCleaner 默认实现
 */
interface CacheCleaner {
    /**
     * 清理缓存子目录
     *
     * 清除存储临时解密内容的缓存目录，包括 media 和 voice 子目录。
     * 如果删除文件时发生错误，将静默失败不会抛出异常。
     */
    fun clearCache()
}
