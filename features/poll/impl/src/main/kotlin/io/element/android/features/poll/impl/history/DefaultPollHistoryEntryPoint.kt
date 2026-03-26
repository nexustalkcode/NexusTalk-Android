/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.poll.api.history.PollHistoryEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认投票历史入口点实现类
 *
 * 实现了 PollHistoryEntryPoint 接口，提供投票历史功能的入口点。
 * 负责创建 PollHistoryFlowNode 节点。
 */
@ContributesBinding(AppScope::class)
class DefaultPollHistoryEntryPoint : PollHistoryEntryPoint {
    /**
     * 创建投票历史节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @return 创建的投票历史节点
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<PollHistoryFlowNode>(buildContext)
    }
}
