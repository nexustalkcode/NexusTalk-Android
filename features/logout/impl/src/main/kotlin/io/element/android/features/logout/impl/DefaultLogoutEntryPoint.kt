/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.logout.api.LogoutEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认退出登录入口点实现
 *
 * 实现了 LogoutEntryPoint 接口，
 * 负责创建退出登录功能的相关节点。
 */
@ContributesBinding(AppScope::class)
class DefaultLogoutEntryPoint : LogoutEntryPoint {
    /**
     * 创建退出登录节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 退出登录流程回调接口
     * @return Node 退出登录节点实例
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: LogoutEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<LogoutNode>(buildContext, listOf(callback))
    }
}
