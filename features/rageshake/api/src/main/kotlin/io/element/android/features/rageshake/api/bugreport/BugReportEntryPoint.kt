/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.bugreport

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 问题报告功能入口点接口
 *
 * 定义了问题报告流程的入口，负责创建和管理问题报告的节点。
 */
interface BugReportEntryPoint : FeatureEntryPoint {
    /**
     * 创建问题报告节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 问题报告节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    /**
     * 问题报告回调接口
     */
    interface Callback : Plugin {
        /** 报告完成 */
        fun onDone()
    }
}
