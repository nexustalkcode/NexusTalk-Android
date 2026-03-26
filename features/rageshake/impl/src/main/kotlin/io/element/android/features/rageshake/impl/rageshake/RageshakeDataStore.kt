/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.rageshake

import kotlinx.coroutines.flow.Flow

/**
 * 摇一摇数据存储接口
 *
 * 定义了摇一摇偏好设置数据的存储操作。
 */
interface RageshakeDataStore {
    /**
     * 获取是否启用
     *
     * 返回一个 Flow，指示摇一摇功能是否启用。
     *
     * @return Flow<Boolean> 启用状态的布尔值流
     */
    fun isEnabled(): Flow<Boolean>

    /**
     * 设置是否启用
     *
     * 设置摇一摇功能的启用状态。
     *
     * @param isEnabled 是否启用
     */
    suspend fun setIsEnabled(isEnabled: Boolean)

    /**
     * 获取灵敏度
     *
     * 返回一个 Flow，包含当前的灵敏度设置。
     *
     * @return Flow<Float> 灵敏度值的浮点数流
     */
    fun sensitivity(): Flow<Float>

    /**
     * 设置灵敏度
     *
     * 设置摇一摇检测的灵敏度。
     *
     * @param sensitivity 灵敏度值（0到1之间）
     */
    suspend fun setSensitivity(sensitivity: Float)

    /**
     * 重置所有数据
     *
     * 清除所有摇一摇相关的偏好设置。
     */
    suspend fun reset()
}
