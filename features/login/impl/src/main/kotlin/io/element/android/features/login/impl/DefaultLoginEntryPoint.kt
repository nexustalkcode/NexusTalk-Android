/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.login.api.LoginEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 默认登录入口点实现
 *
 * 提供 LoginEntryPoint 接口的默认实现。
 * 负责创建登录流程节点并管理登录流程的生命周期。
 */
@ContributesBinding(AppScope::class)
class DefaultLoginEntryPoint : LoginEntryPoint {
    /**
     * 创建登录节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 登录参数
     * @param callback 回调接口
     * @return 创建的登录流程节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: LoginEntryPoint.Params,
        callback: LoginEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<LoginFlowNode>(
            buildContext = buildContext,
            plugins = listOf(
                LoginFlowNode.Params(
                    accountProvider = params.accountProvider,
                    loginHint = params.loginHint,
                ),
                callback,
            )
        )
    }
}
