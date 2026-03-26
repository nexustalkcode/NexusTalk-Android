/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.ftue.api.FtueEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * FTUE 功能默认入口点实现类
 *
 * 该类是 FtueEntryPoint 接口的默认实现，负责创建首次用户体验流程的根节点。
 * 使用 @ContributesBinding 注解将其绑定到 AppScope，使得其他模块可以通过依赖注入获取 FTUE 功能的入口。
 */
@ContributesBinding(AppScope::class)
class DefaultFtueEntryPoint : FtueEntryPoint {
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<FtueFlowNode>(buildContext)
    }
}
