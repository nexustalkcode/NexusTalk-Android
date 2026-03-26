/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api.acceptdecline

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 创建默认的接受/拒绝邀请状态
 *
 * 用于预览和测试生成 AcceptDeclineInviteState 实例。
 *
 * @param acceptAction 接受邀请的异步操作状态，默认为未初始化
 * @param declineAction 拒绝邀请的异步操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return AcceptDeclineInviteState 实例
 */
fun anAcceptDeclineInviteState(
    acceptAction: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    declineAction: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    eventSink: (AcceptDeclineInviteEvents) -> Unit = {},
) = AcceptDeclineInviteState(
    acceptAction = acceptAction,
    declineAction = declineAction,
    eventSink = eventSink,
)
