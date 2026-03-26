/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.reportroom.api.ReportRoomEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * [ReportRoomEntryPoint] 接口的默认实现类
 *
 * 负责创建举报房间页面的节点实例，使用 Appyx 框架的依赖注入机制
 * 绑定到 AppScope 作用域，使其可以在应用范围内使用
 */
@ContributesBinding(AppScope::class)
class DefaultReportRoomEntryPoint : ReportRoomEntryPoint {
    /**
     * 创建举报房间的节点实例
     *
     * @param parentNode 父节点，用于将新节点添加到此节点下
     * @param buildContext 构建上下文，包含节点构建所需的信息
     * @param roomId 要举报的房间ID
     * @return 返回创建的 [ReportRoomNode] 节点实例
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        roomId: RoomId,
    ): Node {
        return parentNode.createNode<ReportRoomNode>(buildContext, plugins = listOf(ReportRoomNode.Inputs(roomId)))
    }
}
