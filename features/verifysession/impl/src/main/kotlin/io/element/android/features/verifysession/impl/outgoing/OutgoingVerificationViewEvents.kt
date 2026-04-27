/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.outgoing

/**
 * 发起验证页面可能触发的视图事件。
 */
sealed interface OutgoingVerificationViewEvents {
    /** 发起验证请求。 */
    data object RequestVerification : OutgoingVerificationViewEvents
    /** 开始 SAS 验证。 */
    data object StartSasVerification : OutgoingVerificationViewEvents
    /** 确认 challenge 一致。 */
    data object ConfirmVerification : OutgoingVerificationViewEvents
    /** 拒绝当前 challenge。 */
    data object DeclineVerification : OutgoingVerificationViewEvents
    /** 取消当前验证流程。 */
    data object Cancel : OutgoingVerificationViewEvents
    /** 把流程重置为初始状态。 */
    data object Reset : OutgoingVerificationViewEvents
}
