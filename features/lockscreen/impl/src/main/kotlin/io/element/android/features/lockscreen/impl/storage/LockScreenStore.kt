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
 * 锁屏存储接口
 *
 * 提供锁屏相关数据的持久化存储功能，继承自 EncryptedPinCodeStorage。
 */
interface LockScreenStore : EncryptedPinCodeStorage {
    /**
     * 获取剩余 PIN 码尝试次数
     *
     * 当次数为 0 时，PIN 码访问将被锁定一段时间。
     *
     * @return 剩余尝试次数
     */
    suspend fun getRemainingPinCodeAttemptsNumber(): Int

    /**
     * 减少剩余 PIN 码尝试次数
     *
     * 当输入错误 PIN 码时调用。
     */
    suspend fun onWrongPin()

    /**
     * 重置 PIN 码和生物识别访问的尝试计数器
     */
    suspend fun resetCounter()

    /**
     * 检查是否允许生物识别解锁
     *
     * @return true 如果允许生物识别解锁
     */
    fun isBiometricUnlockAllowed(): Flow<Boolean>

    /**
     * 设置是否允许生物识别解锁
     *
     * @param isAllowed 是否允许
     */
    suspend fun setIsBiometricUnlockAllowed(isAllowed: Boolean)
}
