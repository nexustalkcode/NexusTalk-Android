/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.deactivation.api.AccountDeactivationEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 账户停用功能默认入口点实现
 *
 * 实现 AccountDeactivationEntryPoint 接口，
 * 负责创建账户停用的 Node 节点。
 */
@ContributesBinding(AppScope::class)
class DefaultAccountDeactivationEntryPoint : AccountDeactivationEntryPoint {
    /**
     * 创建账户停用界面节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @return 账户停用节点
     */
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<AccountDeactivationNode>(buildContext)
    }
}
