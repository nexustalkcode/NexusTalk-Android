/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.biometric

import androidx.biometric.BiometricPrompt

/**
 * 生物识别解锁错误类
 *
 * 封装 [BiometricPrompt.AuthenticationCallback] 的错误信息。
 *
 * @param code 错误代码
 * @param message 错误消息
 */
class BiometricUnlockError(val code: Int, message: String) : Exception(message) {
    /**
     * 此错误是否禁用生物识别认证（临时或永久）
     */
    val isAuthDisabledError: Boolean get() = code in LOCKOUT_ERROR_CODES

    /**
     * 此错误是否永久禁用生物识别认证
     */
    val isAuthPermanentlyDisabledError: Boolean get() = code == BiometricPrompt.ERROR_LOCKOUT_PERMANENT

    companion object {
        /** 锁定错误代码数组 */
        private val LOCKOUT_ERROR_CODES = arrayOf(BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT)
    }
}
