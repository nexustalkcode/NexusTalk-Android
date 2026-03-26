/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.biometric

/**
 * 默认生物识别解锁回调实现
 *
 * 提供生物识别认证事件回调的空实现，用于不需要处理所有事件的场景。
 */
open class DefaultBiometricUnlockCallback : BiometricAuthenticator.Callback {
    override fun onBiometricSetupError() = Unit
    override fun onBiometricAuthenticationSuccess() = Unit
    override fun onBiometricAuthenticationFailed(error: Exception?) = Unit
}
