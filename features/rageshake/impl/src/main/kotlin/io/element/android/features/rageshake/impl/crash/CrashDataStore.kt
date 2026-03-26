/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.crash

import kotlinx.coroutines.flow.Flow

/**
 * 崩溃数据存储接口
 *
 * 定义了崩溃数据的存储操作，用于持久化崩溃信息。
 */
interface CrashDataStore {
    /**
     * 设置崩溃数据
     *
     * 保存崩溃信息和标记应用已崩溃。
     *
     * @param crashData 崩溃数据字符串
     */
    fun setCrashData(crashData: String)

    /**
     * 重置崩溃标志
     *
     * 清除应用已崩溃的标志，但保留崩溃数据。
     */
    suspend fun resetAppHasCrashed()

    /**
     * 检查应用是否崩溃
     *
     * 返回一个 Flow，指示应用是否曾经崩溃过。
     *
     * @return Flow<Boolean> 崩溃状态的布尔值流
     */
    fun appHasCrashed(): Flow<Boolean>

    /**
     * 获取崩溃信息
     *
     * 返回一个 Flow，包含崩溃时的详细信息。
     *
     * @return Flow<String> 崩溃信息字符串流
     */
    fun crashInfo(): Flow<String>

    /**
     * 重置所有崩溃数据
     *
     * 清除所有崩溃相关的数据和标志。
     */
    suspend fun reset()
}
