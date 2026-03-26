/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.pin

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.lockscreen.impl.pin.model.PinEntry
import io.element.android.features.lockscreen.impl.setup.pin.validation.SetupPinFailure

/**
 * 设置 PIN 码状态预览参数提供者
 *
 * 用于在预览中提供不同状态的设置 PIN 码界面。
 */
open class SetupPinStateProvider : PreviewParameterProvider<SetupPinState> {
    override val values: Sequence<SetupPinState>
        get() = sequenceOf(
            aSetupPinState(),
            aSetupPinState(
                choosePinEntry = PinEntry.createEmpty(4).fillWith("12")
            ),
            aSetupPinState(
                choosePinEntry = PinEntry.createEmpty(4).fillWith("1789"),
                isConfirmationStep = true,
            ),
            aSetupPinState(
                choosePinEntry = PinEntry.createEmpty(4).fillWith("1789"),
                confirmPinEntry = PinEntry.createEmpty(4).fillWith("1788"),
                isConfirmationStep = true,
                creationFailure = SetupPinFailure.PinsDoNotMatch
            ),
            aSetupPinState(
                choosePinEntry = PinEntry.createEmpty(4).fillWith("1111"),
                creationFailure = SetupPinFailure.ForbiddenPin
            ),
        )
}

/**
 * 创建设置 PIN 码状态的辅助函数
 */
fun aSetupPinState(
    choosePinEntry: PinEntry = PinEntry.createEmpty(4),
    confirmPinEntry: PinEntry = PinEntry.createEmpty(4),
    isConfirmationStep: Boolean = false,
    creationFailure: SetupPinFailure? = null,
) = SetupPinState(
    choosePinEntry = choosePinEntry,
    confirmPinEntry = confirmPinEntry,
    isConfirmationStep = isConfirmationStep,
    setupPinFailure = creationFailure,
    appName = "Element",
    eventSink = {}
)
