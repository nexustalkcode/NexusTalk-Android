/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.declineandblock

import io.element.android.libraries.architecture.AsyncAction

/**
 * 拒绝并封禁状态数据类
 *
 * 表示拒绝邀请并选择是否封禁用户界面的当前状态。
 *
 * @property reportRoom 是否举报房间
 * @property reportReason 举报原因
 * @property blockUser 是否封禁用户
 * @property declineAction 拒绝操作的异步状态
 * @property eventSink 事件处理函数
 */
data class DeclineAndBlockState(
    val reportRoom: Boolean,
    val reportReason: String,
    val blockUser: Boolean,
    val declineAction: AsyncAction<Unit>,
    val eventSink: (DeclineAndBlockEvents) -> Unit
) {
    /** 是否可以执行拒绝操作 */
    val canDecline = blockUser || reportRoom && reportReason.isNotEmpty()
}
