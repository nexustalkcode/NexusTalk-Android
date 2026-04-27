/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.incoming

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.verifysession.impl.incoming.IncomingVerificationState.Step
import io.element.android.features.verifysession.impl.ui.aDecimalsSessionVerificationData
import io.element.android.features.verifysession.impl.ui.aEmojisSessionVerificationData
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.core.FlowId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.verification.SessionVerificationRequestDetails
import io.element.android.libraries.matrix.api.verification.VerificationRequest

/**
 * 为传入验证页面预览提供样例状态。
 */
open class IncomingVerificationStateProvider : PreviewParameterProvider<IncomingVerificationState> {
    override val values: Sequence<IncomingVerificationState>
        get() = sequenceOf(
            anIncomingVerificationState(),
            anIncomingVerificationState(step = aStepInitial(isWaiting = false), verificationRequest = anIncomingSessionVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = false), verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = true), verificationRequest = anIncomingSessionVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = true), verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = false)),
            anIncomingVerificationState(
                step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = false),
                verificationRequest = anIncomingUserVerificationRequest()
            ),
            anIncomingVerificationState(step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = true)),
            anIncomingVerificationState(
                step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = true),
                verificationRequest = anIncomingUserVerificationRequest()
            ),
            anIncomingVerificationState(step = Step.Verifying(data = aDecimalsSessionVerificationData(), isWaiting = false)),
            anIncomingVerificationState(step = Step.Completed),
            anIncomingVerificationState(step = Step.Completed, verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = Step.Failure),
            anIncomingVerificationState(step = Step.Canceled),
            // Add other state here
        )
}

/**
 * 构造一份初始步骤样例。
 */
internal fun aStepInitial(
    isWaiting: Boolean = false,
) = Step.Initial(
    deviceDisplayName = "Element X Android",
    deviceId = DeviceId("ILAKNDNASDLK"),
    formattedSignInTime = "12:34",
    isWaiting = isWaiting,
)

/**
 * 构造一份“其他设备请求验证”的样例请求。
 */
internal fun anIncomingSessionVerificationRequest() = VerificationRequest.Incoming.OtherSession(
    details = SessionVerificationRequestDetails(
        senderProfile = MatrixUser(
            userId = UserId("@alice:example.com"),
            displayName = "Alice",
            avatarUrl = null,
        ),
        flowId = FlowId("1234"),
        deviceId = DeviceId("ILAKNDNASDLK"),
        deviceDisplayName = "a device name",
        firstSeenTimestamp = 0,
    )
)

/**
 * 构造一份“用户请求验证”的样例请求。
 */
internal fun anIncomingUserVerificationRequest() = VerificationRequest.Incoming.User(
    details = SessionVerificationRequestDetails(
        senderProfile = MatrixUser(
            userId = UserId("@alice:example.com"),
            displayName = "Alice",
            avatarUrl = null,
        ),
        flowId = FlowId("1234"),
        deviceId = DeviceId("ILAKNDNASDLK"),
        deviceDisplayName = "a device name",
        firstSeenTimestamp = 0,
    )
)

/**
 * 构造一份传入验证页面样例状态。
 */
internal fun anIncomingVerificationState(
    step: Step = aStepInitial(),
    verificationRequest: VerificationRequest.Incoming = anIncomingSessionVerificationRequest(),
    eventSink: (IncomingVerificationViewEvents) -> Unit = {},
) = IncomingVerificationState(
    step = step,
    request = verificationRequest,
    eventSink = eventSink,
)
