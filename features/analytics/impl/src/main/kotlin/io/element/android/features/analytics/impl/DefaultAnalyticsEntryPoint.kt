/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.analytics.api.AnalyticsEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * AnalyticsEntryPoint 的默认实现
 *
 * 提供分析功能的默认入口点实现，创建 AnalyticsOptInNode 节点。
 * 该实现用于在应用级提供分析功能入口。
 *
 * @see AnalyticsEntryPoint 分析功能入口点接口
 * @see AnalyticsOptInNode 分析功能节点
 */
@ContributesBinding(AppScope::class)
class DefaultAnalyticsEntryPoint : AnalyticsEntryPoint {
    /**
     * 创建分析功能节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @return AnalyticsOptInNode 实例
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<AnalyticsOptInNode>(buildContext)
    }
}
