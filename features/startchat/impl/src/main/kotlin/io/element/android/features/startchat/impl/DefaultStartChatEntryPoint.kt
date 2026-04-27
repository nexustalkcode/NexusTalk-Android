/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.startchat.api.StartChatEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的开始聊天入口实现。
 *
 * 负责把外部导航请求转换为 [StartChatFlowNode]。
 */
class DefaultStartChatEntryPoint : StartChatEntryPoint {
    /**
     * 创建开始聊天流程节点。
     *
     * @param parentNode 父节点。
     * @param buildContext 当前节点构建上下文。
     * @param callback 流程回调。
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: StartChatEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<StartChatFlowNode>(buildContext, listOf(callback))
    }
}
