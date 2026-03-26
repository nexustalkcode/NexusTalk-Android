/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.viewfolder.api.ViewFolderEntryPoint
import io.element.android.features.viewfolder.impl.root.ViewFolderFlowNode
import io.element.android.libraries.architecture.createNode

/**
 * ViewFolderEntryPoint 的默认实现
 *
 * 提供文件夹浏览功能的默认入口点实现，负责创建 ViewFolderFlowNode 节点。
 * 该实现使用依赖注入方式提供，与应用的组件体系无缝集成。
 *
 * @see ViewFolderEntryPoint 入口点接口
 * @see ViewFolderFlowNode 流程节点实现
 */
@ContributesBinding(AppScope::class)
class DefaultViewFolderEntryPoint : ViewFolderEntryPoint {
    /**
     * 创建文件夹浏览节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 创建参数
     * @param callback 回调接口
     * @return ViewFolderFlowNode 实例
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: ViewFolderEntryPoint.Params,
        callback: ViewFolderEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<ViewFolderFlowNode>(
            buildContext = buildContext,
            plugins = listOf(
                ViewFolderFlowNode.Inputs(params.rootPath),
                callback,
            ),
        )
    }
}
