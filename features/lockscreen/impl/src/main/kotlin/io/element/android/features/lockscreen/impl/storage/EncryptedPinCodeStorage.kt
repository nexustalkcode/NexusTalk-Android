/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.storage

import kotlinx.coroutines.flow.Flow

/**
 * 加密 PIN 码存储接口
 *
 * 提供加密 PIN 码访问的接口，所有方法都是挂起函数以支持异步 IO 操作。
 */
interface EncryptedPinCodeStorage {
    /**
     * 获取加密的 PIN 码
     *
     * @return 加密的 PIN 码字符串，如果不存在则返回 null
     */
    suspend fun getEncryptedCode(): String?

    /**
     * 保存加密的 PIN 码到持久化存储
     *
     * @param pinCode 加密的 PIN 码字符串
     */
    suspend fun saveEncryptedPinCode(pinCode: String)

    /**
     * 从持久化存储中删除 PIN 码
     */
    suspend fun deleteEncryptedPinCode()

    /**
     * 检查是否存储了 PIN 码
     *
     * @return true 如果存储了 PIN 码
     */
    fun hasPinCode(): Flow<Boolean>
}
