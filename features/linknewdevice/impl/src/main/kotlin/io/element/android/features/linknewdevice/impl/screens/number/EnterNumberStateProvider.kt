/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.linknewdevice.ErrorType

open class EnterNumberStateProvider : PreviewParameterProvider<EnterNumberState> {
    override val values: Sequence<EnterNumberState>
        get() = sequenceOf(
            // 默认状态
            aEnterNumberState(),
            // 仅输入一位数字
            aEnterNumberState(number = "1"),
            // 输入完整两位数字
            aEnterNumberState(number = "12"),
            // 发送中
            aEnterNumberState(number = "12", sendingCode = AsyncAction.Loading),
            // 数字不匹配
            aEnterNumberState(number = "12", sendingCode = AsyncAction.Failure(ErrorType.InvalidCheckCode("Invalid"))),
            // 发送失败
            aEnterNumberState(number = "12", sendingCode = AsyncAction.Failure(Exception("Failed to send code"))),
        )
}

// 便捷创建预览状态
fun aEnterNumberState(
    number: String = "",
    sendingCode: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (EnterNumberEvent) -> Unit = {},
) = EnterNumberState(
    number = number,
    sendingCode = sendingCode,
    eventSink = eventSink,
)
