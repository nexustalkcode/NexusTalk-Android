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
 * 接受/拒绝邀请状态数据类
 *
 * 表示接受或拒绝房间邀请的界面状态，包含接受和拒绝操作的异步状态。
 *
 * @property acceptAction 接受邀请的异步操作状态
 * @property declineAction 拒绝邀请的异步操作状态
 * @property eventSink 事件处理函数
 */
data class AcceptDeclineInviteState(
    val acceptAction: AsyncAction<RoomId>,
    val declineAction: AsyncAction<RoomId>,
    val eventSink: (AcceptDeclineInviteEvents) -> Unit,
)
