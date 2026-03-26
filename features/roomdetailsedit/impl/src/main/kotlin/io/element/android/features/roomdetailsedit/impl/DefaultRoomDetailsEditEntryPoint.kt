/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.roomdetailsedit.api.RoomDetailsEditEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 房间详情编辑功能的默认入口点实现
 *
 * 实现 [RoomDetailsEditEntryPoint] 接口，提供创建编辑页面节点的功能
 */
@ContributesBinding(AppScope::class)
class DefaultRoomDetailsEditEntryPoint : RoomDetailsEditEntryPoint {
    /**
     * 创建房间详情编辑节点
     *
     * @param parentNode 父节点，用于构建节点层级
     * @param buildContext 构建上下文，包含必要的配置信息
     * @return 创建的 [RoomDetailsEditNode] 实例
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<RoomDetailsEditNode>(buildContext)
    }
}
