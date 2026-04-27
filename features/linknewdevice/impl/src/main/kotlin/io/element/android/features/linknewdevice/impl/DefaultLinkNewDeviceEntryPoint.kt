/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.linknewdevice.api.LinkNewDeviceEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
/**
 * 默认的新设备关联流程入口。
 *
 * 负责把外部导航请求转换为 [LinkNewDeviceFlowNode]，并把回调插件传递给流程节点。
 */
class DefaultLinkNewDeviceEntryPoint : LinkNewDeviceEntryPoint {
    /**
     * 创建新设备关联流程节点。
     *
     * @param parentNode 父节点。
     * @param buildContext 当前节点构建上下文。
     * @param callback 流程完成后的回调。
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: LinkNewDeviceEntryPoint.Callback,
    ): Node {
        // 通过父节点创建流程节点，并注入回调插件
        return parentNode.createNode<LinkNewDeviceFlowNode>(
            buildContext = buildContext,
            plugins = listOf(
                callback,
            )
        )
    }
}
