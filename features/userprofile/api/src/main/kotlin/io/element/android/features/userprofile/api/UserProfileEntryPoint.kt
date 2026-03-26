/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 用户资料功能入口点接口
 *
 * 定义了用户资料功能的入口接口，负责创建和管理用户资料节点。
 */
interface UserProfileEntryPoint : FeatureEntryPoint {
    /**
     * 输入参数数据类
     *
     * @property userId 用户 ID
     */
    data class Params(val userId: UserId) : NodeInputs

    /**
     * 用户资料流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到指定房间
         *
         * @param roomId 房间 ID
         */
        fun navigateToRoom(roomId: RoomId)
    }

    /**
     * 创建一个用户资料节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @param callback 回调接口
     * @return Node 用户资料节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
