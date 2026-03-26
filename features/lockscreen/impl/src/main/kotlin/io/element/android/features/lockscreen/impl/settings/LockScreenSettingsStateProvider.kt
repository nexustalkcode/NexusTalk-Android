/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.settings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 锁屏设置状态预览参数提供者
 *
 * 用于在预览中提供不同状态的锁屏设置。
 */
open class LockScreenSettingsStateProvider : PreviewParameterProvider<LockScreenSettingsState> {
    override val values: Sequence<LockScreenSettingsState>
        get() = sequenceOf(
            aLockScreenSettingsState(),
            aLockScreenSettingsState(isLockMandatory = true),
            aLockScreenSettingsState(showRemovePinConfirmation = true),
        )
}

/**
 * 创建锁屏设置状态的辅助函数
 */
fun aLockScreenSettingsState(
    isLockMandatory: Boolean = false,
    isBiometricEnabled: Boolean = false,
    showRemovePinConfirmation: Boolean = false,
    showToggleBiometric: Boolean = true,
) = LockScreenSettingsState(
    showRemovePinOption = isLockMandatory,
    isBiometricEnabled = isBiometricEnabled,
    showRemovePinConfirmation = showRemovePinConfirmation,
    showToggleBiometric = showToggleBiometric,
    eventSink = {}
)
