/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roommembermoderation.api

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 房间成员管理可能触发的用户事件。
 */
interface RoomMemberModerationEvents {
    /** 显示指定成员可执行的操作列表。 */
    data class ShowActionsForUser(val user: MatrixUser) : RoomMemberModerationEvents
    /** 对指定成员执行某个管理动作。 */
    data class ProcessAction(val action: ModerationAction, val targetUser: MatrixUser) : RoomMemberModerationEvents
}
