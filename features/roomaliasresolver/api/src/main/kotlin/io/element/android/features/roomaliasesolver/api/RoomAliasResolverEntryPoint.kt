/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasesolver.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

/**
 * 房间别名解析器功能入口点接口
 *
 * 该接口定义了房间别名解析功能的外部访问入口，提供创建解析节点的方法。
 * 用于从外部模块启动并集成房间别名解析功能。
 *
 * @see DefaultRoomAliasResolverEntryPoint 默认实现
 */
interface RoomAliasResolverEntryPoint : FeatureEntryPoint {
    /**
     * 创建房间别名解析节点
     *
     * @param parentNode 父节点，用于构建节点树
     * @param buildContext 构建上下文，包含节点构建所需的信息
     * @param params 入口点参数，包含要解析的房间别名
     * @param callback 回调接口，用于通知解析结果
     * @return 创建的解析节点
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 解析结果回调接口
     *
     * 当房间别名解析成功或失败时，通过此接口通知调用者结果。
     */
    interface Callback : Plugin {
        /**
         * 别名解析成功回调
         *
         * @param data 解析后的房间别名信息，包含房间ID等
         */
        fun onAliasResolved(data: ResolvedRoomAlias)
    }

    /**
     * 入口点参数数据类
     *
     * 包含创建节点时所需的输入参数。
     *
     * @property roomAlias 要解析的房间别名
     */
    data class Params(
        /** 要解析的房间别名 */
        val roomAlias: RoomAlias
    ) : NodeInputs
}
