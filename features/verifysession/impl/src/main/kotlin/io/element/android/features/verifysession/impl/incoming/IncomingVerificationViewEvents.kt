/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.incoming

/**
 * 传入验证页面可能触发的视图事件。
 */
sealed interface IncomingVerificationViewEvents {
    /** 返回上一页或关闭当前流程。 */
    data object GoBack : IncomingVerificationViewEvents
    /** 开始接受当前传入验证。 */
    data object StartVerification : IncomingVerificationViewEvents
    /** 忽略当前验证请求。 */
    data object IgnoreVerification : IncomingVerificationViewEvents
    /** 确认 challenge 一致。 */
    data object ConfirmVerification : IncomingVerificationViewEvents
    /** 拒绝当前 challenge。 */
    data object DeclineVerification : IncomingVerificationViewEvents
}
