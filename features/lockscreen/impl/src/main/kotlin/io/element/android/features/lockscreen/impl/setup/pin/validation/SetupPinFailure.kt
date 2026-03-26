/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.pin.validation

/**
 * 设置 PIN 码失败原因密封接口
 *
 * 定义设置 PIN 码过程中可能的失败原因。
 */
sealed interface SetupPinFailure {
    /** PIN 码被禁用（如过于简单） */
    data object ForbiddenPin : SetupPinFailure
    /** 两次输入的 PIN 码不匹配 */
    data object PinsDoNotMatch : SetupPinFailure
}
