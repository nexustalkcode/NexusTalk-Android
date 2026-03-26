/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.timeline.TimelineProvider

/**
 * 转发功能入口点接口
 *
 * 定义了消息转发功能的入口接口，负责创建和管理转发消息流程的节点。
 */
interface ForwardEntryPoint : FeatureEntryPoint {
    /**
     * 转发流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 转发完成回调
         *
         * @param roomIds 目标房间 ID 列表
         */
        fun onDone(roomIds: List<RoomId>)
    }

    /**
     * 输入参数数据类
     *
     * @property eventId 要转发的事件 ID
     * @property timelineProvider 时间线提供者
     */
    data class Params(
        val eventId: EventId,
        val timelineProvider: TimelineProvider,
    ) : NodeInputs

    /**
     * 创建一个转发消息节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @param callback 回调接口
     * @return Node 转发消息节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
