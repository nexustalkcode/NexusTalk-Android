/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 文件夹浏览功能入口点
 *
 * 定义文件夹浏览功能的接口契约，负责创建和管理文件夹浏览节点。
 * 该入口点用于在应用中嵌入文件夹浏览功能，支持浏览设备文件系统中的文件夹和文件。
 *
 * @property rootPath 浏览的根路径，指定从哪个目录开始浏览
 * @see ViewFolderFlowNode 文件夹浏览的流程节点实现
 */
interface ViewFolderEntryPoint : FeatureEntryPoint {
    /**
     * 创建文件夹浏览节点的参数
     *
     * @property rootPath 浏览的起始目录路径
     */
    data class Params(
        val rootPath: String,
    )

    /**
     * 创建文件夹浏览节点
     *
     * @param parentNode 父节点，用于构建节点层次结构
     * @param buildContext 构建上下文
     * @param params 创建参数，包含根路径信息
     * @param callback 回调接口，用于处理完成事件
     * @return 创建的节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 文件夹浏览完成回调接口
     */
    interface Callback : Plugin {
        /**
         * 浏览完成时调用
         */
        fun onDone()
    }
}
