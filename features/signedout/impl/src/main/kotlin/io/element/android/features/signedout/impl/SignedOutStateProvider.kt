/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.signedout.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.sessionstorage.api.LoginType
import io.element.android.libraries.sessionstorage.api.SessionData

/**
 * 为已登出页面预览提供样例状态。
 */
open class SignedOutStateProvider : PreviewParameterProvider<SignedOutState> {
    override val values: Sequence<SignedOutState>
        get() = sequenceOf(
            aSignedOutState(),
            // Add other states here
        )
}

/**
 * 构造一份已登出页面样例状态。
 */
private fun aSignedOutState() = SignedOutState(
    appName = "AppName",
    signedOutSession = aSessionData(),
    eventSink = {},
)

/**
 * 构造一份会话数据样例。
 */
private fun aSessionData(
    sessionId: String = "@alice:server.org",
    isTokenValid: Boolean = false,
): SessionData {
    return SessionData(
        userId = sessionId,
        deviceId = "aDeviceId",
        accessToken = "anAccessToken",
        refreshToken = "aRefreshToken",
        homeserverUrl = "aHomeserverUrl",
        oidcData = null,
        loginTimestamp = null,
        isTokenValid = isTokenValid,
        loginType = LoginType.UNKNOWN,
        passphrase = null,
        sessionPath = "/a/path/to/a/session",
        cachePath = "/a/path/to/a/cache",
        position = 0,
        lastUsageIndex = 0,
        userDisplayName = null,
        userAvatarUrl = null,
    )
}
