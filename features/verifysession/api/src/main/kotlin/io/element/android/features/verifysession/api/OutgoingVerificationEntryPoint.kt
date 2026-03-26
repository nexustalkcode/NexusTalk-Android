/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.verification.VerificationRequest

/**
 * 传出会话验证功能入口点接口
 *
 * 定义了传出会话验证流程的入口，负责创建和管理验证流程的节点。
 */
interface OutgoingVerificationEntryPoint : FeatureEntryPoint {
    /**
     * 输入参数数据类
     *
     * @property showDeviceVerifiedScreen 是否显示设备已验证屏幕
     * @property verificationRequest 传出验证请求
     */
    data class Params(
        val showDeviceVerifiedScreen: Boolean,
        val verificationRequest: VerificationRequest.Outgoing,
    ) : NodeInputs

    /**
     * 创建传出会话验证节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 输入参数
     * @param callback 回调接口
     * @return Node 传出会话验证节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 传出会话验证回调接口
     */
    interface Callback : Plugin {
        /** 导航到了解更多加密信息页面 */
        fun navigateToLearnMoreAboutEncryption()
        /** 返回上一页面 */
        fun onBack()
        /** 验证完成 */
        fun onDone()
    }
}
