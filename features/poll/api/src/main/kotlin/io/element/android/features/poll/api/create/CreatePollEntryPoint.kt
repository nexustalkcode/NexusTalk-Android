/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.create

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 创建投票功能入口点接口
 *
 * 定义了创建投票流程的入口，负责创建和管理创建投票的节点。
 * 继承自 FeatureEntryPoint，是投票模块的核心入口点。
 */
interface CreatePollEntryPoint : FeatureEntryPoint {
    /**
     * 输入参数数据类
     *
     * 包含创建投票节点所需的参数。
     *
     * @property timelineMode 时间线模式（实时或线程模式）
     * @property mode 创建投票模式（新建或编辑）
     */
    data class Params(
        val timelineMode: Timeline.Mode,
        val mode: CreatePollMode,
    )

    /**
     * 创建投票节点
     *
     * 根据提供的参数创建投票创建界面的节点。
     *
     * @param parentNode 父节点，新节点将添加到此节点
     * @param buildContext 构建上下文，包含节点构建所需的信息
     * @param params 输入参数，包含时间线模式和创建模式
     * @return Node 投票创建节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
    ): Node
}
