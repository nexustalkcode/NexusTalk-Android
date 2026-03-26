/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.biometric

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 设置生物识别状态预览参数提供者
 *
 * 用于在预览中提供不同状态的设置生物识别界面。
 */
open class SetupBiometricStateProvider : PreviewParameterProvider<SetupBiometricState> {
    override val values: Sequence<SetupBiometricState>
        get() = sequenceOf(
            aSetupBiometricState(),
        )
}

/**
 * 创建设置生物识别状态的辅助函数
 */
fun aSetupBiometricState(
    isBiometricSetupDone: Boolean = false,
) = SetupBiometricState(
    isBiometricSetupDone = isBiometricSetupDone,
    eventSink = {}
)
