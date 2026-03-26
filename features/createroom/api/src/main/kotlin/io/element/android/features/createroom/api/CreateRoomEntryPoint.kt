/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 创建房间功能入口点接口
 *
 * 定义了创建房间功能的入口接口，负责创建和管理创建房间流程的节点。
 */
interface CreateRoomEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个房间节点
     *
     * @param isSpace 是否创建为空间
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口，用于处理房间创建完成事件
     * @return Node 创建的房间节点实例
     */
    fun createNode(
        isSpace: Boolean,
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
        addPeopleCallback: AddPeopleCallback? = null,
    ): Node

    /**
     * 创建房间流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 房间创建完成回调
         *
         * @param roomId 创建的房间 ID
         */
        fun onRoomCreated(roomId: RoomId)
    }

    interface AddPeopleCallback : Plugin {
        fun onAddPeopleShown(roomId: RoomId)
    }
}
