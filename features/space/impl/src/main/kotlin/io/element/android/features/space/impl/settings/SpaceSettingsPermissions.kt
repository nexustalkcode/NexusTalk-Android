/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.settings

import io.element.android.features.roomdetailsedit.api.RoomDetailsEditPermissions
import io.element.android.features.roomdetailsedit.api.roomDetailsEditPermissions
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyPermissions
import io.element.android.features.securityandprivacy.api.securityAndPrivacyPermissions
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.room.powerlevels.RoomPermissions
import io.element.android.libraries.matrix.api.room.powerlevels.canEditRolesAndPermissions

/**
 * Space 设置页可见权限集合。
 */
data class SpaceSettingsPermissions(
    val editDetailsPermissions: RoomDetailsEditPermissions,
    val canEditRolesAndPermissions: Boolean,
    val securityAndPrivacyPermissions: SecurityAndPrivacyPermissions,
) {
    /**
     * 判断当前至少有一个设置入口可见。
     */
    fun hasAny(joinRule: JoinRule?): Boolean {
        return editDetailsPermissions.hasAny ||
            canEditRolesAndPermissions ||
            securityAndPrivacyPermissions.hasAny(isSpace = true, joinRule = joinRule)
    }

    companion object {
        val DEFAULT = SpaceSettingsPermissions(
            editDetailsPermissions = RoomDetailsEditPermissions.DEFAULT,
            canEditRolesAndPermissions = false,
            securityAndPrivacyPermissions = SecurityAndPrivacyPermissions.DEFAULT,
        )
    }
}

/**
 * 将通用房间权限转换为空间设置页使用的权限模型。
 */
fun RoomPermissions.spaceSettingsPermissions(): SpaceSettingsPermissions {
    return SpaceSettingsPermissions(
        editDetailsPermissions = roomDetailsEditPermissions(),
        canEditRolesAndPermissions = canEditRolesAndPermissions(),
        securityAndPrivacyPermissions = securityAndPrivacyPermissions(),
    )
}
