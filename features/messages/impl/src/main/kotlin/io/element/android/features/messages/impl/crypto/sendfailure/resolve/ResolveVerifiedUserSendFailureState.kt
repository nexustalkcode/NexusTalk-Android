/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.libraries.architecture.AsyncAction

/**
 * 解决已验证用户发送失败状态数据类
 *
 * @property verifiedUserSendFailure 已验证用户发送失败信息
 * @property resolveAction 解决操作的异步状态
 * @property retryAction 重试操作的异步状态
 * @property eventSink 事件处理函数
 */
data class ResolveVerifiedUserSendFailureState(
    val verifiedUserSendFailure: VerifiedUserSendFailure,
    val resolveAction: AsyncAction<Unit>,
    val retryAction: AsyncAction<Unit>,
    val eventSink: (ResolveVerifiedUserSendFailureEvents) -> Unit
)
