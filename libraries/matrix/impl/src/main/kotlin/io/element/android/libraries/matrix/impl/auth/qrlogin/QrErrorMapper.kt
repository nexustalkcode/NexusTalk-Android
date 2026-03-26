/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth.qrlogin

import io.element.android.libraries.matrix.api.auth.qrlogin.QrCodeDecodeException
import io.element.android.libraries.matrix.api.auth.qrlogin.QrLoginException
import org.matrix.rustcomponents.sdk.HumanQrLoginException as RustHumanQrLoginException
import org.matrix.rustcomponents.sdk.QrCodeDecodeException as RustQrCodeDecodeException

/**
 * 二维码登录错误映射器
 *
 * 该对象负责将 Rust SDK 的二维码登录相关异常转换为 Android 层的统一异常类型。
 * 这样可以将 Rust 层的实现细节封装起来，上层只需处理 Android 定义的异常类型。
 *
 * 主要功能：
 * - 将二维码解码异常 (QrCodeDecodeException) 从 Rust 层映射到 Android 层
 * - 将二维码登录异常 (HumanQrLoginException) 从 Rust 层映射到 Android 层
 *
 * @see QrCodeDecodeException Android 层的二维码解码异常
 * @see QrLoginException Android 层的二维码登录异常
 */
object QrErrorMapper {

    /**
     * 将 Rust SDK 的二维码解码异常转换为 Android 层的异常
     *
     * 二维码解码异常通常在解析二维码数据时发生，例如：
     * - 数据不足
     * - 编码格式错误
     * - 版本不兼容等
     *
     * @param qrCodeDecodeException Rust SDK 的二维码解码异常
     * @return 转换后的 Android 层 QrCodeDecodeException 对象
     */
    fun map(qrCodeDecodeException: RustQrCodeDecodeException): QrCodeDecodeException = when (qrCodeDecodeException) {
        // 加密相关错误 - 未来当 UniFFi 支持时会恢复更详细的错误信息
        is RustQrCodeDecodeException.Crypto -> {
            // We plan to restore it in the future when UniFFi can process them
//            val reason = when (qrCodeDecodeException.error) {
//                LoginQrCodeDecodeError.NOT_ENOUGH_DATA -> QrCodeDecodeException.Crypto.Reason.NOT_ENOUGH_DATA
//                LoginQrCodeDecodeError.NOT_UTF8 -> QrCodeDecodeException.Crypto.Reason.NOT_UTF8
//                LoginQrCodeDecodeError.URL_PARSE -> QrCodeDecodeException.Crypto.Reason.URL_PARSE
//                LoginQrCodeDecodeError.INVALID_MODE -> QrCodeDecodeException.Crypto.Reason.INVALID_MODE
//                LoginQrCodeDecodeError.INVALID_VERSION -> QrCodeDecodeException.Crypto.Reason.INVALID_VERSION
//                LoginQrCodeDecodeError.BASE64 -> QrCodeDecodeException.Crypto.Reason.BASE64
//                LoginQrCodeDecodeError.INVALID_PREFIX -> QrCodeDecodeException.Crypto.Reason.INVALID_PREFIX
//            }
            QrCodeDecodeException.Crypto(
                qrCodeDecodeException.message.orEmpty(),
//                reason
            )
        }
    }

    /**
     * 将 Rust SDK 的人类可读二维码登录异常转换为 Android 层的异常
     *
     * 这些异常在二维码登录过程中发生，表示用户可见的错误状态：
     * - Cancelled: 用户取消了登录
     * - ConnectionInsecure: 连接不安全
     * - Declined: 对方设备拒绝了登录请求
     * - Expired: 二维码已过期
     * - OtherDeviceNotSignedIn: 对方设备未登录
     * - LinkingNotSupported: 不支持二维码登录
     * - Unknown: 未知错误
     * - OidcMetadataInvalid: OIDC 元数据无效
     * - SlidingSyncNotAvailable: Sliding Sync 不可用
     * - CheckCodeAlreadySent: 验证码已发送
     * - CheckCodeCannotBeSent: 无法发送验证码
     * - NotFound: 未找到
     *
     * @param humanQrLoginError Rust SDK 的二维码登录异常
     * @return 转换后的 Android 层 QrLoginException 对象
     */
    fun map(humanQrLoginError: RustHumanQrLoginException): QrLoginException = when (humanQrLoginError) {
        is RustHumanQrLoginException.Cancelled -> QrLoginException.Cancelled
        is RustHumanQrLoginException.ConnectionInsecure -> QrLoginException.ConnectionInsecure
        is RustHumanQrLoginException.Declined -> QrLoginException.Declined
        is RustHumanQrLoginException.Expired -> QrLoginException.Expired
        is RustHumanQrLoginException.OtherDeviceNotSignedIn -> QrLoginException.OtherDeviceNotSignedIn
        is RustHumanQrLoginException.LinkingNotSupported -> QrLoginException.LinkingNotSupported
        is RustHumanQrLoginException.Unknown -> QrLoginException.Unknown
        is RustHumanQrLoginException.OidcMetadataInvalid -> QrLoginException.OidcMetadataInvalid
        is RustHumanQrLoginException.SlidingSyncNotAvailable -> QrLoginException.SlidingSyncNotAvailable
        is RustHumanQrLoginException.CheckCodeAlreadySent -> QrLoginException.CheckCodeAlreadySent
        is RustHumanQrLoginException.CheckCodeCannotBeSent -> QrLoginException.CheckCodeCannotBeSent
        is RustHumanQrLoginException.NotFound -> QrLoginException.NotFound
    }
}
