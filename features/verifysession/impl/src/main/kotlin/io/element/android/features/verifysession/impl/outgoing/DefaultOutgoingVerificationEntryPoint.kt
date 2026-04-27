/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.outgoing

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.verifysession.api.OutgoingVerificationEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的发起验证入口实现。
 *
 * 负责把外部导航请求包装为 [OutgoingVerificationNode]。
 */
class DefaultOutgoingVerificationEntryPoint : OutgoingVerificationEntryPoint {
    /**
     * 创建发起验证节点。
     *
     * @param parentNode 父节点。
     * @param buildContext 当前节点构建上下文。
     * @param params 发起验证流程参数。
     * @param callback 流程回调。
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: OutgoingVerificationEntryPoint.Params,
        callback: OutgoingVerificationEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<OutgoingVerificationNode>(buildContext, listOf(params, callback))
    }
}
