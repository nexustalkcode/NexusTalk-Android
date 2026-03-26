/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.qrcode

import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData
import io.element.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.StateFlow

/**
 * 二维码登录管理器接口
 *
 * 处理二维码登录流程的辅助接口。
 * 在获取二维码数据后，负责管理与服务器的身份验证过程。
 *
 * @see DefaultQrCodeLoginManager 默认实现
 * @see QrCodeLoginFlowNode 二维码登录流程节点
 */
interface QrCodeLoginManager {
    /**
     * 当前二维码登录步骤
     *
     * 使用 StateFlow 响应式地提供登录流程的当前状态。
     *
     * @return 当前登录步骤的状态流
     */
    val currentLoginStep: StateFlow<QrCodeLoginStep>

    /**
     * 使用二维码数据认证
     *
     * 使用扫描的二维码数据与服务器进行身份验证。
     *
     * @param qrCodeLoginData 扫描的二维码包含的登录数据
     * @return 登录成功返回 SessionId，失败返回错误结果
     */
    suspend fun authenticate(qrCodeLoginData: MatrixQrCodeLoginData): Result<SessionId>

    /**
     * 重置登录状态
     *
     * 清除当前的登录状态，允许用户重新开始登录流程。
     */
    fun reset()
}
