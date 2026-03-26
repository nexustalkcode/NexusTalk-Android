/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.biometric

/**
 * 设置生物识别状态数据类
 *
 * @property isBiometricSetupDone 生物识别设置是否已完成
 * @property eventSink 事件处理函数
 */
data class SetupBiometricState(
    val isBiometricSetupDone: Boolean,
    val eventSink: (SetupBiometricEvents) -> Unit
)
