/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.features.logout.api.LogoutEntryPoint
import io.element.android.tests.testutils.lambda.lambdaError

/**
 * 虚假的退出登录入口点（用于测试）
 *
 * 实现了 LogoutEntryPoint 接口，
 * 用于测试环境中，当需要模拟退出登录入口点时使用。
 * 所有方法调用都会触发 lambdaError，以提示该方法未正确设置。
 */
class FakeLogoutEntryPoint : LogoutEntryPoint {
    /**
     * 创建退出登录节点（测试用）
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 退出登录流程回调接口
     * @return Node 节点实例（实际测试中会抛出错误）
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: LogoutEntryPoint.Callback,
    ): Node = lambdaError()
}
