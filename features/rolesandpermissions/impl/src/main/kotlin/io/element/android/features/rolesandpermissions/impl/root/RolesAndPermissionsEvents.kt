/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.root

import io.element.android.libraries.matrix.api.room.RoomMember

/**
 * 角色与权限主页可能触发的用户事件。
 */
sealed interface RolesAndPermissionsEvents {
    /** 请求修改自己的角色。 */
    data object ChangeOwnRole : RolesAndPermissionsEvents
    /** 将自己降级到指定角色。 */
    data class DemoteSelfTo(val role: RoomMember.Role) : RolesAndPermissionsEvents
    /** 重置权限配置。 */
    data object ResetPermissions : RolesAndPermissionsEvents
    /** 取消当前挂起动作。 */
    data object CancelPendingAction : RolesAndPermissionsEvents
}
