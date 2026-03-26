/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.api

import android.content.Intent
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 分享功能入口点接口
 *
 * 定义内容分享功能的入口接口，支持将内容分享到多个房间。
 * 该入口点用于在应用中嵌入分享功能，用户可以选择房间分享内容。
 *
 * @property Params 创建节点的参数，包含分享意图
 * @property Callback 回调接口，处理分享完成事件
 * @see ShareNode 分享节点
 */
/**
 * Entry point interface for the share feature.
 *
 * Defines the entry point for content sharing functionality, supporting sharing content to multiple rooms.
 * This entry point is used to embed the share feature in the app, allowing users to select rooms to share content with.
 */
interface ShareEntryPoint : FeatureEntryPoint {
    /**
     * 创建分享节点的参数
     *
     * @property intent 要分享的 Intent 内容
     */
    /**
     * Parameters for creating a share node.
     *
     * @property intent The intent to be shared
     */
    data class Params(val intent: Intent)

    /**
     * 创建分享节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 创建参数
     * @param callback 回调接口
     * @return 创建的节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 分享完成回调接口
     */
    /**
     * Callback interface for share completion.
     */
    interface Callback : Plugin {
        /**
         * 分享完成时调用
         *
         * @param roomIds 分享到的房间 ID 列表
         */
        /**
         * Called when the share operation is complete.
         *
         * @param roomIds The list of room IDs that were shared to
         */
        fun onDone(roomIds: List<RoomId>)
    }
}
