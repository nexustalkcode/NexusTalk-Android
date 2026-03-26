/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.logout.api.direct.aDirectLogoutState
import io.element.android.libraries.architecture.AsyncData

/**
 * 选择自验证方式状态预览参数提供者
 *
 * 该类继承自 PreviewParameterProvider，用于在 Jetpack Compose 预览中提供
 * 不同状态的 ChooseSelfVerificationModeState 示例数据。
 *
 * @see ChooseSelfVerificationModeState
 */
class ChooseSelfVerificationModeStateProvider :
    PreviewParameterProvider<ChooseSelfVerificationModeState> {
    override val values = sequenceOf(
        aChooseSelfVerificationModeState(
            buttonsState = AsyncData.Success(
                aButtonsState(canUseAnotherDevice = false, canEnterRecoveryKey = true),
            ),
        ),
        aChooseSelfVerificationModeState(
            buttonsState = AsyncData.Success(
                aButtonsState(canUseAnotherDevice = false, canEnterRecoveryKey = false),
            ),
        ),
        aChooseSelfVerificationModeState(
            buttonsState = AsyncData.Success(
                aButtonsState(canUseAnotherDevice = true, canEnterRecoveryKey = true),
            ),
        ),
        aChooseSelfVerificationModeState(
            buttonsState = AsyncData.Success(
                aButtonsState(canUseAnotherDevice = true, canEnterRecoveryKey = false),
            ),
        ),
        aChooseSelfVerificationModeState(
            buttonsState = AsyncData.Loading(),
        ),
    )
}

/**
 * 创建测试用的选择自验证方式状态
 *
 * @param buttonsState 按钮状态，默认为成功状态的默认按钮状态
 * @return 包含默认值的 ChooseSelfVerificationModeState 实例
 */
fun aChooseSelfVerificationModeState(
    buttonsState: AsyncData<ChooseSelfVerificationModeState.ButtonsState> = AsyncData.Success(aButtonsState()),
) = ChooseSelfVerificationModeState(
    buttonsState = buttonsState,
    directLogoutState = aDirectLogoutState(),
    eventSink = {},
)

/**
 * 创建测试用的按钮状态
 *
 * @param canUseAnotherDevice 是否可以使用另一台设备验证，默认为 true
 * @param canEnterRecoveryKey 是否可以输入恢复密钥，默认为 true
 * @return ButtonsState 实例
 */
fun aButtonsState(
    canUseAnotherDevice: Boolean = true,
    canEnterRecoveryKey: Boolean = true,
) = ChooseSelfVerificationModeState.ButtonsState(
    canUseAnotherDevice = canUseAnotherDevice,
    canEnterRecoveryKey = canEnterRecoveryKey,
)
