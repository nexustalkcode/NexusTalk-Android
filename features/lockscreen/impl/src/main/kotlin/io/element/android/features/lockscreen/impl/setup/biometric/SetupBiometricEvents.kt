/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.biometric

/**
 * 设置生物识别事件密封接口
 *
 * 定义设置生物识别流程中的各种用户交互事件。
 */
sealed interface SetupBiometricEvents {
    /** 允许使用生物识别 */
    data object AllowBiometric : SetupBiometricEvents
    /** 使用 PIN 码代替 */
    data object UsePin : SetupBiometricEvents
}
