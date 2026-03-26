/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 举报房间功能的入口点接口
 *
 * 定义了创建举报房间页面的节点的方法，用于在应用中展示举报房间的界面
 */
fun interface ReportRoomEntryPoint : FeatureEntryPoint {
    /**
     * 创建举报房间的节点
     *
     * @param parentNode 父节点，用于将新节点添加到此节点下
     * @param buildContext 构建上下文，包含节点构建所需的信息
     * @param roomId 要举报的房间ID
     * @return 返回创建的节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        roomId: RoomId,
    ): Node
}
