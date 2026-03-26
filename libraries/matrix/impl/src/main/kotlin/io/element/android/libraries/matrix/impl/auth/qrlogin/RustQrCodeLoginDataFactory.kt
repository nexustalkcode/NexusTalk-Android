/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth.qrlogin

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData
import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginDataFactory
import org.matrix.rustcomponents.sdk.QrCodeData

/**
 * Rust SDK 实现的二维码登录数据工厂
 *
 * 该类是 [MatrixQrCodeLoginDataFactory] 接口的 Rust 实现，负责解析二维码数据。
 * 它将原始的字节数组转换为结构化的二维码登录数据对象。
 *
 * 工作流程：
 * 1. 接收扫描获取的原始字节数据
 * 2. 使用 Rust SDK 解析字节数据
 * 3. 封装为 Android 层的 [MatrixQrCodeLoginData] 对象
 *
 * 使用 @ContributesBinding 注解将此类绑定到 AppScope，使得整个应用可以通过
 * [MatrixQrCodeLoginDataFactory] 接口使用此功能。
 *
 * @see MatrixQrCodeLoginDataFactory 二维码登录数据工厂接口
 * @see SdkQrCodeLoginData Rust SDK 实现的二维码登录数据
 */
@ContributesBinding(AppScope::class)
class RustQrCodeLoginDataFactory : MatrixQrCodeLoginDataFactory {

    /**
     * 解析二维码数据
     *
     * 将扫描获取的原始字节数组解析为二维码登录数据对象。
     * 如果解析失败，返回包含错误信息的 Result。
     *
     * @param data 从二维码扫描获取的原始字节数据
     * @return Result<MatrixQrCodeLoginData> 成功时返回解析后的二维码登录数据，
     *         失败时返回包含异常信息的 Result
     *
     * @throws 如果字节数据不是有效的二维码格式，可能会抛出异常
     */
    override fun parseQrCodeData(data: ByteArray): Result<MatrixQrCodeLoginData> {
        return runCatchingExceptions { SdkQrCodeLoginData(QrCodeData.fromBytes(data)) }
    }
}
