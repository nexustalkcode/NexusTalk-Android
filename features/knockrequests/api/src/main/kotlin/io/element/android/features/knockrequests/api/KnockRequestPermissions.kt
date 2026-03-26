/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.api

import io.element.android.libraries.matrix.api.room.powerlevels.RoomPermissions

/**
 * 敲门请求权限数据类
 *
 * 表示用户对敲门请求的操作权限。
 *
 * @property canAccept 是否可以接受敲门请求
 * @property canDecline 是否可以拒绝敲门请求
 * @property canBan 是否可以封禁请求者
 */
data class KnockRequestPermissions(
    val canAccept: Boolean,
    val canDecline: Boolean,
    val canBan: Boolean,
) {
    /** 是否有任何权限 */
    val hasAny = canAccept || canDecline || canBan

    companion object {
        /** 默认敲门请求权限 */
        val DEFAULT = KnockRequestPermissions(
            canAccept = false,
            canDecline = false,
            canBan = false,
        )
    }
}

/**
 * 从房间权限转换为敲门请求权限
 *
 * @return KnockRequestPermissions 敲门请求权限
 */
fun RoomPermissions.knockRequestPermissions(): KnockRequestPermissions {
    return KnockRequestPermissions(
        canAccept = canOwnUserInvite(),
        canDecline = canOwnUserKick(),
        canBan = canOwnUserBan(),
    )
}
