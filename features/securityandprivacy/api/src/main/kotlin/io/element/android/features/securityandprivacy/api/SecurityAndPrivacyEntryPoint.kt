/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 安全与隐私功能入口点接口
 *
 * 定义了安全与隐私设置的入口接口，负责创建设置节点。
 * 该接口继承自 FeatureEntryPoint，用于应用模块化架构中的功能入口。
 */
fun interface SecurityAndPrivacyEntryPoint : FeatureEntryPoint {
    /**
     * 安全与隐私设置回调接口
     *
     * 定义了在安全与隐私设置流程中需要通知调用方的回调事件。
     */
    interface Callback : Plugin {
        /**
         * 完成安全与隐私设置时的回调
         *
         * 当用户完成设置或离开设置页面时调用。
         */
        fun onDone()
    }

    /**
     * 创建一个安全与隐私设置节点
     *
     * @param parentNode 父节点，用于构建节点层级关系
     * @param buildContext 构建上下文，包含构建所需的相关信息
     * @param callback 回调接口，用于通知外部事件
     * @return Node 创建的安全与隐私设置节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node
}
