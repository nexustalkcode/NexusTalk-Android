/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias

/**
 * 开始聊天功能入口点接口
 *
 * 定义了开始新聊天功能的入口接口，负责创建和管理聊天创建流程的节点。
 */
interface StartChatEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个开始聊天节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 开始聊天节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    /**
     * 开始聊天流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 房间创建完成回调
         *
         * @param roomIdOrAlias 创建的房间 ID 或别名
         * @param serverNames 服务器名称列表
         */
        fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>)
        /** 导航到房间目录 */
        fun navigateToRoomDirectory()
    }
}
