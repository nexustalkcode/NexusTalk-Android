/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.messages.api.MessagesEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope

/**
 * 默认消息入口点实现类
 *
 * 实现 MessagesEntryPoint 接口，负责创建消息功能模块的节点。
 * 使用 @ContributesBinding 注解绑定到 SessionScope。
 *
 * @see MessagesEntryPoint 消息入口点接口
 * @see MessagesFlowNode 消息流程节点
 * @see BuildContext 构建上下文
 * @see Node Appyx核心节点
 */
@ContributesBinding(SessionScope::class)
class DefaultMessagesEntryPoint : MessagesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: MessagesEntryPoint.Params,
        callback: MessagesEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<MessagesFlowNode>(buildContext, listOf(params, callback))
    }
}

/**
 * 将初始目标转换为导航目标的扩展函数
 *
 * @return 对应的 MessagesFlowNode.NavTarget
 */
internal fun MessagesEntryPoint.InitialTarget.toNavTarget() = when (this) {
    is MessagesEntryPoint.InitialTarget.Messages -> MessagesFlowNode.NavTarget.Messages(focusedEventId)
    MessagesEntryPoint.InitialTarget.PinnedMessages -> MessagesFlowNode.NavTarget.PinnedMessagesList
}
