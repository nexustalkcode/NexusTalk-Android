/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock

import io.element.android.features.lockscreen.impl.biometric.BiometricAuthenticator
import io.element.android.features.lockscreen.impl.biometric.BiometricUnlockError
import io.element.android.features.lockscreen.impl.pin.model.PinEntry
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData

/**
 * PIN 解锁状态数据类
 *
 * 表示 PIN 码解锁界面的当前状态，包含 PIN 输入、生物识别解锁和账户退出等状态。
 *
 * @property pinEntry PIN 码输入的异步状态
 * @property showWrongPinTitle 是否显示 PIN 码错误提示
 * @property remainingAttempts 剩余尝试次数
 * @property showSignOutPrompt 是否显示退出登录提示
 * @property signOutAction 退出登录操作的异步状态
 * @property showBiometricUnlock 是否显示生物识别解锁选项
 * @property isUnlocked 是否已解锁
 * @property biometricUnlockResult 生物识别解锁结果
 * @property eventSink 事件处理函数
 */
data class PinUnlockState(
    val pinEntry: AsyncData<PinEntry>,
    val showWrongPinTitle: Boolean,
    val remainingAttempts: AsyncData<Int>,
    val showSignOutPrompt: Boolean,
    val signOutAction: AsyncAction<Unit>,
    val showBiometricUnlock: Boolean,
    val isUnlocked: Boolean,
    val biometricUnlockResult: BiometricAuthenticator.AuthenticationResult?,
    val eventSink: (PinUnlockEvents) -> Unit
) {
    /** 退出提示是否可取消 */
    val isSignOutPromptCancellable = when (remainingAttempts) {
        is AsyncData.Success -> remainingAttempts.data > 0
        else -> true
    }

    /** 生物识别解锁错误消息 */
    val biometricUnlockErrorMessage = when {
        biometricUnlockResult is BiometricAuthenticator.AuthenticationResult.Failure &&
            biometricUnlockResult.error is BiometricUnlockError &&
            biometricUnlockResult.error.isAuthDisabledError -> {
            biometricUnlockResult.error.message
        }
        else -> null
    }
    /** 是否显示生物识别解锁错误 */
    val showBiometricUnlockError = biometricUnlockErrorMessage != null
}
