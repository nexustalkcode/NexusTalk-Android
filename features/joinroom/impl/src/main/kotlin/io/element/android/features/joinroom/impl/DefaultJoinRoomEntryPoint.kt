/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.joinroom.api.JoinRoomEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的加入房间功能入口点实现类
 *
 * 实现了 JoinRoomEntryPoint 接口，负责创建 JoinRoomFlowNode 节点。
 * 该类是加入房间功能的入口点，通过 @ContributesBinding 注解绑定到 AppScope。
 */
class DefaultJoinRoomEntryPoint : JoinRoomEntryPoint {
    /**
     * 创建加入房间节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param inputs 输入参数
     * @return Node 创建的加入房间流程节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: JoinRoomEntryPoint.Inputs,
    ): Node {
        return parentNode.createNode<JoinRoomFlowNode>(
            buildContext = buildContext,
            plugins = listOf(inputs)
        )
    }
}
