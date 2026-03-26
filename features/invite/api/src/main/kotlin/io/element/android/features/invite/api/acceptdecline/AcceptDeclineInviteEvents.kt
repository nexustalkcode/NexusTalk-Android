/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api.acceptdecline

import io.element.android.features.invite.api.InviteData

/**
 * 接受/拒绝邀请事件接口
 *
 * 定义了处理房间邀请接受和拒绝操作的事件。
 * 用于在 UI 层和业务逻辑层之间传递用户交互事件。
 */
interface AcceptDeclineInviteEvents {
    /**
     * 接受邀请事件
     *
     * @property invite 邀请数据
     */
    data class AcceptInvite(val invite: InviteData) : AcceptDeclineInviteEvents

    /**
     * 拒绝邀请事件
     *
     * @property invite 邀请数据
     * @property blockUser 是否同时封禁发送邀请的用户
     * @property shouldConfirm 是否需要显示确认对话框
     */
    data class DeclineInvite(val invite: InviteData, val blockUser: Boolean, val shouldConfirm: Boolean) : AcceptDeclineInviteEvents
}
