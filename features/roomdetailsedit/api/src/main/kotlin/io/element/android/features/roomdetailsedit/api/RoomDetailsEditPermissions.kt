/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.api

import io.element.android.libraries.matrix.api.room.StateEventType
import io.element.android.libraries.matrix.api.room.powerlevels.RoomPermissions

/**
 * 房间详情编辑权限数据类
 *
 * 封装了用户在房间详情编辑页面中的各种权限状态
 *
 * @property canEditName 是否可以编辑房间名称
 * @property canEditTopic 是否可以编辑房间主题
 * @property canEditAvatar 是否可以编辑房间头像
 */
data class RoomDetailsEditPermissions(
    /** 是否可以编辑房间名称 */
    val canEditName: Boolean,
    /** 是否可以编辑房间主题 */
    val canEditTopic: Boolean,
    /** 是否可以编辑房间头像 */
    val canEditAvatar: Boolean,
) {
    /** 是否具有任意编辑权限 */
    val hasAny = canEditName ||
        canEditTopic ||
        canEditAvatar

    companion object {
        /** 默认权限配置，所有编辑权限默认为false */
        val DEFAULT = RoomDetailsEditPermissions(
            canEditName = false,
            canEditTopic = false,
            canEditAvatar = false,
        )
    }
}

/**
 * 将 [RoomPermissions] 转换为 [RoomDetailsEditPermissions] 的扩展函数
 *
 * 根据房间权限级别判断用户是否有权限编辑房间的名称、主题和头像
 *
 * @return 包含编辑权限的 [RoomDetailsEditPermissions] 对象
 */
fun RoomPermissions.roomDetailsEditPermissions(): RoomDetailsEditPermissions {
    return RoomDetailsEditPermissions(
        canEditName = canOwnUserSendState(StateEventType.RoomName),
        canEditTopic = canOwnUserSendState(StateEventType.RoomTopic),
        canEditAvatar = canOwnUserSendState(StateEventType.RoomAvatar),
    )
}
