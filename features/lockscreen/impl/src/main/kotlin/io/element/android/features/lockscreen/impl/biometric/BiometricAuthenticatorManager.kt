/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.biometric

import androidx.compose.runtime.Composable

/**
 * 生物识别认证管理器接口
 *
 * 提供设备生物识别能力查询和认证器创建功能。
 */
interface BiometricAuthenticatorManager {
    /**
     * 设备是否已启用安全保护（如 PIN、图案或密码）
     */
    val isDeviceSecured: Boolean

    /**
     * 设备是否有可用的生物识别硬件且用户已录入至少一个生物特征
     */
    val hasAvailableAuthenticator: Boolean

    /**
     * 添加生物识别认证回调
     *
     * @param callback 要添加的回调
     */
    fun addCallback(callback: BiometricAuthenticator.Callback)

    /**
     * 移除生物识别认证回调
     *
     * @param callback 要移除的回调
     */
    fun removeCallback(callback: BiometricAuthenticator.Callback)

    /**
     * 创建一个用于解锁应用的生物识别认证器（带记忆）
     *
     * @return 生物识别认证器实例
     */
    @Composable
    fun rememberUnlockBiometricAuthenticator(): BiometricAuthenticator

    /**
     * 创建一个用于确认的生物识别认证器（带记忆）
     *
     * @return 生物识别认证器实例
     */
    @Composable
    fun rememberConfirmBiometricAuthenticator(): BiometricAuthenticator
}
