/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.outgoing

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.verifysession.impl.outgoing.OutgoingVerificationState.Step
import io.element.android.features.verifysession.impl.ui.aDecimalsSessionVerificationData
import io.element.android.features.verifysession.impl.ui.aEmojisSessionVerificationData
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.verification.VerificationRequest

/**
 * 为发起验证页面预览提供样例状态。
 */
open class OutgoingVerificationStateProvider : PreviewParameterProvider<OutgoingVerificationState> {
    override val values: Sequence<OutgoingVerificationState>
        get() = sequenceOf(
            anOutgoingVerificationState(
                step = Step.Initial,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Initial,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.AwaitingOtherDeviceResponse,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.AwaitingOtherDeviceResponse,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Uninitialized),
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Uninitialized),
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Loading())
            ),
            anOutgoingVerificationState(
                step = Step.Canceled
            ),
            anOutgoingVerificationState(
                step = Step.Ready
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aDecimalsSessionVerificationData(), AsyncData.Uninitialized)
            ),
            anOutgoingVerificationState(
                step = Step.Completed,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Completed,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Loading
            ),
            anOutgoingVerificationState(
                step = Step.Exit
            ),
            // Add other state here
        )
}

/**
 * 构造一份“验证其他用户”请求样例。
 */
internal fun anOutgoingUserVerificationRequest() = VerificationRequest.Outgoing.User(userId = UserId("@alice:example.com"))
/**
 * 构造一份“验证当前会话”请求样例。
 */
internal fun anOutgoingSessionVerificationRequest() = VerificationRequest.Outgoing.CurrentSession

/**
 * 构造一份发起验证页面样例状态。
 */
internal fun anOutgoingVerificationState(
    step: Step = Step.Initial,
    request: VerificationRequest.Outgoing = anOutgoingSessionVerificationRequest(),
    eventSink: (OutgoingVerificationViewEvents) -> Unit = {},
) = OutgoingVerificationState(
    step = step,
    request = request,
    eventSink = eventSink,
)
