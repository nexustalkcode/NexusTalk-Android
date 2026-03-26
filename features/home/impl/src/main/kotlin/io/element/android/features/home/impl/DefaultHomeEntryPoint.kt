/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.home.api.HomeEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认首页入口点实现
 *
 * 实现 HomeEntryPoint 接口，负责创建首页流程节点。
 * 使用 Hilt 依赖注入框架进行绑定。
 *
 * @see HomeEntryPoint 首页入口点接口
 */
@ContributesBinding(AppScope::class)
class DefaultHomeEntryPoint : HomeEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: HomeEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<HomeFlowNode>(buildContext, listOf(callback))
    }
}
