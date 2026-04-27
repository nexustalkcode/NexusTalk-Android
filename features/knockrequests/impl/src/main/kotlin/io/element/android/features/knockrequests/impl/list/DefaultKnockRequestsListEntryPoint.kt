/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.list

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.knockrequests.api.list.KnockRequestsListEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的敲门请求列表入口实现。
 *
 * 负责把导航入口解析为 [KnockRequestsListNode]。
 */
class DefaultKnockRequestsListEntryPoint : KnockRequestsListEntryPoint {
    /**
     * 创建敲门请求列表节点。
     *
     * @param parentNode 父节点。
     * @param buildContext 当前节点的构建上下文。
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<KnockRequestsListNode>(buildContext)
    }
}
