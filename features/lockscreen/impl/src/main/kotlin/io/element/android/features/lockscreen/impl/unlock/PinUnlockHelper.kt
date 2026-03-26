/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import dev.zacsweers.metro.Inject
import io.element.android.features.lockscreen.impl.biometric.BiometricAuthenticatorManager
import io.element.android.features.lockscreen.impl.biometric.DefaultBiometricUnlockCallback
import io.element.android.features.lockscreen.impl.pin.DefaultPinCodeManagerCallback
import io.element.android.features.lockscreen.impl.pin.PinCodeManager

/**
 * PIN 解锁辅助类
 *
 * 提供解锁效果的辅助 composable，用于处理生物识别和 PIN 码验证的回调。
 */
@Inject
class PinUnlockHelper(
    private val biometricAuthenticatorManager: BiometricAuthenticatorManager,
    private val pinCodeManager: PinCodeManager
) {
    /**
     * 解锁效果组合
     *
     * 当生物识别或 PIN 码验证成功时触发回调。
     *
     * @param onUnlock 解锁回调
     */
    @Composable
    fun OnUnlockEffect(onUnlock: () -> Unit) {
        val latestOnUnlock by rememberUpdatedState(onUnlock)
        DisposableEffect(Unit) {
            val biometricUnlockCallback = object : DefaultBiometricUnlockCallback() {
                override fun onBiometricAuthenticationSuccess() {
                    latestOnUnlock()
                }
            }
            val pinCodeVerifiedCallback = object : DefaultPinCodeManagerCallback() {
                override fun onPinCodeVerified() {
                    latestOnUnlock()
                }
            }
            biometricAuthenticatorManager.addCallback(biometricUnlockCallback)
            pinCodeManager.addCallback(pinCodeVerifiedCallback)
            onDispose {
                biometricAuthenticatorManager.removeCallback(biometricUnlockCallback)
                pinCodeManager.removeCallback(pinCodeVerifiedCallback)
            }
        }
    }
}
