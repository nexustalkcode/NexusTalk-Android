/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.acceptdecline

import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents

/**
 * 内部接受/拒绝邀请事件接口
 *
 * 扩展自 AcceptDeclineInviteEvents，添加了内部使用的事件。
 * 用于清除操作状态等内部逻辑。
 */
sealed interface InternalAcceptDeclineInviteEvents : AcceptDeclineInviteEvents {
    /** 清除接受操作状态事件 */
    data object ClearAcceptActionState : InternalAcceptDeclineInviteEvents
    /** 清除拒绝操作状态事件 */
    data object ClearDeclineActionState : InternalAcceptDeclineInviteEvents
}
