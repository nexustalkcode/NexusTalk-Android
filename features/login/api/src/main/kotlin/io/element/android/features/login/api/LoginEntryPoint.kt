/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 登录功能入口点接口
 *
 * 定义登录功能的接口契约，负责创建和管理登录流程节点。
 * 该入口点用于在应用中嵌入用户登录功能，支持多种登录方式。
 *
 * @property Params 创建节点的参数，包含账户提供商和登录提示
 * @property Callback 回调接口，处理登录完成和导航事件
 * @see LoginEntryPoint.Params 登录参数
 * @see LoginEntryPoint.Callback 回调接口
 */
interface LoginEntryPoint : FeatureEntryPoint {
    /**
     * 创建登录节点的参数
     *
     * @property accountProvider 账户提供商 URL，null 表示显示提供商列表
     * @property loginHint 登录提示文本
     */
    data class Params(
        val accountProvider: String?,
        val loginHint: String?,
    )

    /**
     * 登录流程回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到问题报告页面
         */
        fun navigateToBugReport()
        /**
         * 登录完成时调用
         */
        fun onDone()
    }

    /**
     * 创建登录节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 创建参数
     * @param callback 回调接口
     * @return 创建的节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
