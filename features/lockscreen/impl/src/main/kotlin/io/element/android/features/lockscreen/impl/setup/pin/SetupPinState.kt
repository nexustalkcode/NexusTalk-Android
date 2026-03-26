/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.pin

import io.element.android.features.lockscreen.impl.pin.model.PinEntry
import io.element.android.features.lockscreen.impl.setup.pin.validation.SetupPinFailure

/**
 * 设置 PIN 码状态数据类
 *
 * 表示设置 PIN 码流程的当前状态，包含 PIN 码输入和确认步骤。
 *
 * @property choosePinEntry 选择的 PIN 码输入状态
 * @property confirmPinEntry 确认的 PIN 码输入状态
 * @property isConfirmationStep 是否处于确认步骤
 * @property setupPinFailure 设置失败的原因
 * @property appName 应用名称
 * @property eventSink 事件处理函数
 */
data class SetupPinState(
    val choosePinEntry: PinEntry,
    val confirmPinEntry: PinEntry,
    val isConfirmationStep: Boolean,
    val setupPinFailure: SetupPinFailure?,
    val appName: String,
    val eventSink: (SetupPinEvents) -> Unit
) {
    /** 当前激活的 PIN 码输入框 */
    val activePinEntry = if (isConfirmationStep) {
        confirmPinEntry
    } else {
        choosePinEntry
    }
}
