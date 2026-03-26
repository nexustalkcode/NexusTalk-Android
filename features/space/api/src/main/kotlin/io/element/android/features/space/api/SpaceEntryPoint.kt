/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 空间功能入口点接口
 *
 * 定义了空间（Space）功能的入口接口，负责创建和管理空间详情节点。
 */
interface SpaceEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个空间节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param inputs 输入参数
     * @param callback 回调接口
     * @return Node 空间节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: Inputs,
        callback: Callback
    ): Node

    /**
     * 输入参数数据类
     *
     * @property roomId 空间房间 ID
     */
    data class Inputs(
        val roomId: RoomId
    ) : NodeInputs

    /**
     * 空间流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到指定房间
         *
         * @param roomId 房间 ID
         * @param viaParameters 通过参数列表
         */
        fun navigateToRoom(roomId: RoomId, viaParameters: List<String>)
        /** 导航到房间成员列表 */
        fun navigateToRoomMemberList()
    }
}
