/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roommembermoderation.api

import androidx.compose.runtime.Immutable

@Immutable
/**
 * 房间成员管理 UI 需要的最小状态接口。
 */
interface RoomMemberModerationState {
    val permissions: RoomMemberModerationPermissions
    val eventSink: (RoomMemberModerationEvents) -> Unit
}

/**
 * 单个成员管理动作的可用状态。
 */
data class ModerationActionState(
    val action: ModerationAction,
    val isEnabled: Boolean,
)

/**
 * 房间成员可执行的管理动作。
 */
sealed interface ModerationAction {
    data object DisplayProfile : ModerationAction
    data object KickUser : ModerationAction
    data object BanUser : ModerationAction
    data object UnbanUser : ModerationAction
}
