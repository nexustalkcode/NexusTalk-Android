/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roommembermoderation.api

import io.element.android.libraries.matrix.api.room.powerlevels.RoomPermissions

/**
 * 房间成员管理能力集合。
 */
data class RoomMemberModerationPermissions(
    val canKick: Boolean,
    val canBan: Boolean,
) {
    /**
     * 是否允许解封用户。
     *
     * 由于没有独立的 unban 权限，这里要求同时具备 kick 和 ban 权限。
     */
    val canUnban = canBan && canKick

    companion object {
        val DEFAULT = RoomMemberModerationPermissions(
            canKick = false,
            canBan = false,
        )
    }
}

/**
 * 从房间权级中计算成员管理能力。
 */
fun RoomPermissions.roomMemberModerationPermissions(): RoomMemberModerationPermissions {
    return RoomMemberModerationPermissions(
        canKick = canOwnUserKick(),
        canBan = canOwnUserBan(),
    )
}
