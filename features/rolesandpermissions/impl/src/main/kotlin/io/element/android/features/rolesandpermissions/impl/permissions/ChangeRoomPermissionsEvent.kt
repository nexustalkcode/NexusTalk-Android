/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.permissions

/**
 * 修改房间权限页面可能触发的事件。
 */
interface ChangeRoomPermissionsEvent {
    /** 修改某个权限项对应的最低角色。 */
    data class ChangeMinimumRoleForAction(val action: RoomPermissionType, val role: SelectableRole) : ChangeRoomPermissionsEvent
    /** 保存当前权限修改。 */
    data object Save : ChangeRoomPermissionsEvent
    /** 退出当前页面。 */
    data object Exit : ChangeRoomPermissionsEvent
    /** 清理挂起对话框或保存状态。 */
    data object ResetPendingActions : ChangeRoomPermissionsEvent
}
