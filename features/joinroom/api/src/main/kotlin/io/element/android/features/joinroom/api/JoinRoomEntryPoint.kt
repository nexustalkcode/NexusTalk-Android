/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import im.vector.app.features.analytics.plan.JoinedRoom
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import java.util.Optional

/**
 * 加入房间功能入口点接口
 *
 * 定义了加入房间功能的入口接口，负责创建和管理加入房间流程的节点。
 * 该接口继承自 FeatureEntryPoint，是整个加入房间功能的统一入口点。
 */
interface JoinRoomEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个加入房间节点
     *
     * @param parentNode 父节点，用于构建节点层级关系
     * @param buildContext 构建上下文，包含构建所需的各种信息
     * @param inputs 输入参数，包含房间 ID、别名、描述等信息
     * @return Node 加入房间节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: Inputs,
    ): Node

    /**
     * 加入房间输入参数
     *
     * 定义了进入加入房间界面所需的所有输入参数，
     * 这些参数通常来自上一个界面或导航传递的数据。
     *
     * @property roomId 房间的唯一标识符
     * @property roomIdOrAlias 房间 ID 或别名，用于定位房间
     * @property roomDescription 房间描述信息（可选），包含房间名称、主题等
     * @property serverNames 服务器名称列表，用于房间发现
     * @property trigger 加入房间的触发方式，用于分析统计
     */
    data class Inputs(
        val roomId: RoomId,
        val roomIdOrAlias: RoomIdOrAlias,
        val roomDescription: Optional<RoomDescription>,
        val serverNames: List<String>,
        val trigger: JoinedRoom.Trigger,
    ) : NodeInputs
}
