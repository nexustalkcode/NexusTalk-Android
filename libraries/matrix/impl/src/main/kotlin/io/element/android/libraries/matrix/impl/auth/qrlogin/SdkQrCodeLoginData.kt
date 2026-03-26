/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth.qrlogin

import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData
import org.matrix.rustcomponents.sdk.QrCodeData as RustQrCodeData

/**
 * Rust SDK 实现的二维码登录数据
 *
 * 该类是 [MatrixQrCodeLoginData] 接口的 Rust 实现，封装了从二维码扫描获取的
 * 登录数据。二维码登录是 Matrix 的一种无密码认证方式，用户可以通过扫描
 * 桌面端或其他设备的二维码来快速登录。
 *
 * 主要功能：
 * - 存储 Rust SDK 的二维码数据
 * - 提供访问 Homeserver 名称的方法
 *
 * @property rustQrCodeData 底层的 Rust SDK 二维码数据对象
 *
 * @see MatrixQrCodeLoginData 二维码登录数据接口
 * @see <a href="https://matrix.org/docs/guides/login-with-qr-code">Matrix 二维码登录指南</a>
 */
class SdkQrCodeLoginData(
    internal val rustQrCodeData: RustQrCodeData,
) : MatrixQrCodeLoginData {

    /**
     * 获取二维码对应的 Homeserver 名称
     *
     * @return Homeserver 的名称（如 "matrix.org"），如果无法解析则返回 null
     */
    override fun serverName(): String? {
        return rustQrCodeData.serverName()
    }
}
