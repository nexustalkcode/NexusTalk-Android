/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 房间目录功能入口点接口
 *
 * 定义了房间目录功能的入口接口，负责创建和管理房间目录浏览节点。
 */
interface RoomDirectoryEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个房间目录节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 房间目录节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    /**
     * 房间目录流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到指定房间
         *
         * @param roomDescription 房间描述
         */
        fun navigateToRoom(roomDescription: RoomDescription)
    }
}
