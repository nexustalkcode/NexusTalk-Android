/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.create

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.poll.api.create.CreatePollEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认创建投票入口点实现类
 *
 * 实现了 CreatePollEntryPoint 接口，提供创建投票功能的入口点。
 * 负责创建 CreatePollNode 节点。
 */
@ContributesBinding(AppScope::class)
class DefaultCreatePollEntryPoint : CreatePollEntryPoint {
    /**
     * 创建投票节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @return 创建的投票节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: CreatePollEntryPoint.Params,
    ): Node {
        return parentNode.createNode<CreatePollNode>(
            buildContext = buildContext,
            plugins = listOf(CreatePollNode.Inputs(timelineMode = params.timelineMode, mode = params.mode))
        )
    }
}
