/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.signedout.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.signedout.api.SignedOutEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的已登出页面入口实现。
 */
class DefaultSignedOutEntryPoint : SignedOutEntryPoint {
    /**
     * 创建已登出页面节点。
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: SignedOutEntryPoint.Params,
    ): Node {
        return parentNode.createNode<SignedOutNode>(
            buildContext = buildContext,
            plugins = listOf(SignedOutNode.Inputs(params.sessionId))
        )
    }
}
