/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom

/**
 * 修改房间成员角色页面入口接口。
 */
fun interface ChangeRoomMemberRolesEntryPoint : FeatureEntryPoint {
    /**
     * 创建修改成员角色节点。
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node

    /**
     * 供外层流程等待角色修改完成的节点代理。
     */
    interface NodeProxy {
        val roomId: RoomId
        suspend fun waitForCompletion(): Boolean
    }
}

/**
 * 修改成员角色页面的列表类型。
 */
enum class ChangeRoomMemberRolesListType {
    SelectNewOwnersWhenLeaving,
    Admins,
    Moderators
}
