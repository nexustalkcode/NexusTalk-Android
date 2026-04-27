/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.signedout.impl

import io.element.android.libraries.sessionstorage.api.SessionData

/**
 * 已登出页面展示状态。
 *
 * @property appName 当前应用名。
 * @property signedOutSession 当前已登出会话信息。
 * @property eventSink 页面事件分发函数。
 */
data class SignedOutState(
    val appName: String,
    val signedOutSession: SessionData?,
    val eventSink: (SignedOutEvents) -> Unit,
)
