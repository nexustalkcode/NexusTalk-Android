/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth.qrlogin

import io.element.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import org.matrix.rustcomponents.sdk.QrLoginProgress

/**
 * 将 Rust SDK 的二维码登录进度转换为 Android 层的步骤枚举
 *
 * 这个扩展函数将 Rust SDK 的二维码登录进度状态映射为 Android 定义的
 * 统一步骤枚举，用于在 UI 层展示登录进度。
 *
 * 登录流程步骤：
 * 1. [QrLoginProgress.Starting] -> [QrCodeLoginStep.Starting]: 初始化
 * 2. [QrLoginProgress.EstablishingSecureChannel] -> [QrCodeLoginStep.EstablishingSecureChannel]: 建立安全通道
 * 3. [QrLoginProgress.WaitingForToken] -> [QrCodeLoginStep.WaitingForToken]: 等待令牌
 * 4. [QrLoginProgress.SyncingSecrets] -> [QrCodeLoginStep.SyncingSecrets]: 同步密钥
 * 5. [QrLoginProgress.Done] -> [QrCodeLoginStep.Finished]: 完成
 *
 * @receiver Rust SDK 的二维码登录进度对象
 * @return 转换后的 Android 层二维码登录步骤枚举
 *
 * @see QrLoginProgress Rust SDK 的登录进度状态
 * @see QrCodeLoginStep Android 层的登录步骤枚举
 */
fun QrLoginProgress.toStep(): QrCodeLoginStep {
    return when (this) {
        // 正在建立安全通道，可能需要输入验证码
        is QrLoginProgress.EstablishingSecureChannel -> QrCodeLoginStep.EstablishingSecureChannel(checkCodeString)

        // 正在启动二维码登录流程
        is QrLoginProgress.Starting -> QrCodeLoginStep.Starting

        // 正在等待认证令牌
        is QrLoginProgress.WaitingForToken -> QrCodeLoginStep.WaitingForToken(userCode)

        // 正在同步密钥和其他数据
        is QrLoginProgress.SyncingSecrets -> QrCodeLoginStep.SyncingSecrets

        // 登录完成
        is QrLoginProgress.Done -> QrCodeLoginStep.Finished
    }
}
