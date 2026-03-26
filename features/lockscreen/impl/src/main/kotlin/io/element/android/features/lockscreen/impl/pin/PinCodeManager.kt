/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.pin

import kotlinx.coroutines.flow.Flow

/**
 * PIN 码管理器接口
 *
 * 管理 PIN 码的核心接口，实现类负责加密存储 PIN 码。
 */
interface PinCodeManager {
    /**
     * PIN 码管理事件回调接口
     */
    interface Callback {
        /**
         * PIN 码验证成功时调用
         */
        fun onPinCodeVerified()

        /**
         * PIN 码创建成功时调用
         */
        fun onPinCodeCreated()

        /**
         * PIN 码删除时调用
         */
        fun onPinCodeRemoved()
    }

    /**
     * 注册回调以接收 PIN 码管理事件
     */
    fun addCallback(callback: Callback)

    /**
     * 取消注册回调
     */
    fun removeCallback(callback: Callback)

    /**
     * 检查是否存在 PIN 码
     *
     * @return true 如果存在 PIN 码
     */
    fun hasPinCode(): Flow<Boolean>

    /**
     * 获取已保存 PIN 码的长度
     *
     * @return PIN 码长度
     */
    suspend fun getPinCodeSize(): Int

    /**
     * 创建新的加密 PIN 码
     *
     * @param pinCode 明文 PIN 码
     */
    suspend fun createPinCode(pinCode: String)

    /**
     * 验证 PIN 码
     *
     * @param pinCode 要验证的 PIN 码
     * @return true 如果 PIN 码正确
     */
    suspend fun verifyPinCode(pinCode: String): Boolean

    /**
     * 删除之前创建的 PIN 码
     */
    suspend fun deletePinCode()

    /**
     * 获取剩余尝试次数
     *
     * @return 锁定前的剩余尝试次数
     */
    suspend fun getRemainingPinCodeAttemptsNumber(): Int
}
