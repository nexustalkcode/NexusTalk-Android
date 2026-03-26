/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.bugreport

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.rageshake.api.bugreport.BugReportEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认问题报告入口点
 *
 * BugReportEntryPoint 接口的默认实现，负责创建问题报告流程节点。
 */
@ContributesBinding(AppScope::class)
class DefaultBugReportEntryPoint : BugReportEntryPoint {
    /**
     * 创建问题报告节点
     *
     * 创建一个 BugReportFlowNode 来管理问题报告的完整流程。
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 问题报告流程节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: BugReportEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<BugReportFlowNode>(buildContext, listOf(callback))
    }
}
