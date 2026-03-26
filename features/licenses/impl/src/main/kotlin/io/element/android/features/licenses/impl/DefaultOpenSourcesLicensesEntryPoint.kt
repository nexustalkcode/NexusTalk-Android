/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.licenses.api.OpenSourceLicensesEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 开源许可证入口点的默认实现
 *
 * 提供 OpenSourceLicensesEntryPoint 接口的默认实现，
 * 创建 DependenciesFlowNode 作为许可证查看的根节点。
 *
 * @see OpenSourceLicensesEntryPoint 开源许可证入口点接口
 * @see DependenciesFlowNode 依赖项许可流程节点
 */
@ContributesBinding(AppScope::class)
class DefaultOpenSourcesLicensesEntryPoint : OpenSourceLicensesEntryPoint {
    /**
     * 创建开源许可证节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @return DependenciesFlowNode 实例
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<DependenciesFlowNode>(buildContext)
    }
}
