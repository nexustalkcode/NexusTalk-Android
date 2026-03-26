/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.forward.api.ForwardEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope

/**
 * 默认转发功能入口点实现
 *
 * 实现了 [ForwardEntryPoint] 接口，提供消息转发功能的入口点。
 * 使用 [ContributesBinding] 注解将其绑定到 SessionScope 作用域，
 * 以便在整个会话周期内共享使用。
 *
 * 负责创建转发消息节点 [ForwardMessagesNode]，并将必要的输入参数和回调传递给该节点。
 *
 * @see ForwardEntryPoint
 * @see ForwardMessagesNode
 */
@ContributesBinding(SessionScope::class)
class DefaultForwardEntryPoint : ForwardEntryPoint {
    /**
     * 创建转发消息节点
     *
     * @param parentNode 父节点，用于将新创建的节点添加到其子节点中
     * @param buildContext 构建上下文，包含节点构建所需的信息
     * @param params 转发功能的输入参数，包含事件 ID 和时间线提供者
     * @param callback 转发完成后的回调接口
     * @return Node 创建的转发消息节点实例
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: ForwardEntryPoint.Params,
        callback: ForwardEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<ForwardMessagesNode>(
            buildContext = buildContext,
            plugins = listOf(
                ForwardMessagesNode.Inputs(
                    eventId = params.eventId,
                    timelineProvider = params.timelineProvider,
                ),
                callback,
            )
        )
    }
}
