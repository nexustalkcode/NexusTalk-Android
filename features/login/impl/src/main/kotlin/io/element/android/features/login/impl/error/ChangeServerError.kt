/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.error

import io.element.android.features.login.impl.changeserver.AccountProviderAccessException
import io.element.android.libraries.matrix.api.auth.AuthenticationException

/**
 * 更改服务器错误密封类
 *
 * 定义更改服务器时可能发生的各种错误类型。
 * 用于在 UI 层统一处理和显示错误信息。
 *
 * @see ChangeServerErrorProvider 错误信息提供者
 * @see ChangeServerView 显示错误的视图组件
 */
sealed class ChangeServerError : Exception() {
    /**
     * 通用错误
     *
     * 用于包装其他未明确处理的错误。
     *
     * @property messageStr 错误消息字符串，可为 null
     */
    data class Error(
        val messageStr: String? = null,
    ) : ChangeServerError()

    /**
     * 需要 Element Pro 错误
     *
     * 当服务器要求使用 Element Pro 版本时触发。
     *
     * @property unauthorisedAccountProviderTitle 未被授权的服务器标题
     * @property applicationId Element Pro 应用程序包名
     */
    data class NeedElementPro(
        val unauthorisedAccountProviderTitle: String,
        val applicationId: String,
    ) : ChangeServerError()

    /**
     * 未授权账户提供商错误
     *
     * 当服务器不在允许列表中时触发。
     *
     * @property unauthorisedAccountProviderTitle 未被授权的服务器标题
     * @property authorisedAccountProviderTitles 允许的服务器标题列表
     */
    data class UnauthorizedAccountProvider(
        val unauthorisedAccountProviderTitle: String,
        val authorisedAccountProviderTitles: List<String>,
    ) : ChangeServerError()

    /** Sliding Sync 警告 - 服务器不支持 Sliding Sync 协议 */
    data object SlidingSyncAlert : ChangeServerError()

    /** 无效服务器错误 - 服务器地址无效或无法访问 */
    data object InvalidServer : ChangeServerError()

    /** 不支持的服务器错误 - 服务器不支持所需的认证方式 */
    data object UnsupportedServer : ChangeServerError()

    companion object {
        /**
         * 从 Throwable 转换 ChangeServerError
         *
         * 将各种异常类型转换为统一的 ChangeServerError 类型。
         *
         * @param error 原始异常
         * @return 对应的 ChangeServerError
         */
        fun from(error: Throwable): ChangeServerError = when (error) {
            is ChangeServerError -> error
            is AuthenticationException -> {
                when (error) {
                    is AuthenticationException.SlidingSyncVersion -> SlidingSyncAlert
                    is AuthenticationException.InvalidServerName,
                    is AuthenticationException.ServerUnreachable -> InvalidServer
                    // AccountAlreadyLoggedIn error should not happen at this point
                    is AuthenticationException.AccountAlreadyLoggedIn -> Error(messageStr = error.message)
                    is AuthenticationException.Generic -> Error(messageStr = error.message)
                    is AuthenticationException.Oidc -> Error(messageStr = error.message)
                }
            }
            is AccountProviderAccessException.NeedElementProException -> NeedElementPro(
                unauthorisedAccountProviderTitle = error.unauthorisedAccountProviderTitle,
                applicationId = error.applicationId,
            )
            is AccountProviderAccessException.UnauthorizedAccountProviderException -> UnauthorizedAccountProvider(
                unauthorisedAccountProviderTitle = error.unauthorisedAccountProviderTitle,
                authorisedAccountProviderTitles = error.authorisedAccountProviderTitles,
            )
            else -> Error(messageStr = error.message)
        }
    }
}
