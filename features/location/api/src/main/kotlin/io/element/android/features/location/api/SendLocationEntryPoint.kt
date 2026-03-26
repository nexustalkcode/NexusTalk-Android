/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 发送位置功能入口点接口
 *
 * 定义了在房间内发送位置消息的功能入口，负责创建和管理发送位置的节点。
 */
interface SendLocationEntryPoint : FeatureEntryPoint {
    /**
     * 创建发送位置节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param timelineMode 时间线模式
     * @return Node 发送位置节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        timelineMode: Timeline.Mode,
    ): Node
}
