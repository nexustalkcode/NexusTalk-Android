/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 退出登录功能入口点接口
 *
 * 定义了退出登录功能的入口接口，负责创建和管理退出登录流程的节点。
 */
interface LogoutEntryPoint : FeatureEntryPoint {
    /**
     * 创建一个退出登录节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 退出登录节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    /**
     * 退出登录流程回调接口
     */
    interface Callback : Plugin {
        /** 导航到安全备份设置 */
        fun navigateToSecureBackup()
    }
}
