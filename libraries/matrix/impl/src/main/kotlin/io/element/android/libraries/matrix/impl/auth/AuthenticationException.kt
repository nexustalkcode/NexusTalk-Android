/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import io.element.android.libraries.matrix.api.auth.AuthenticationException
import org.matrix.rustcomponents.sdk.ClientBuildException
import org.matrix.rustcomponents.sdk.OidcException

/**
 * 将 Throwable 异常映射为 Android 层的 AuthenticationException
 *
 * 此扩展函数负责将 Rust SDK 抛出的各种异常转换为 Android 应用层定义的
 * 统一认证异常类型。这样可以将 Rust 层的实现细节封装起来，上层只需处理
 * Android 定义的异常类型。
 *
 * @receiver 任何可能的异常对象
 * @return 转换后的 Android AuthenticationException 对象
 *
 * 映射规则：
 * - 已是 AuthenticationException 类型：直接返回
 * - ClientBuildException：构建 Matrix 客户端时产生的错误
 *   - Generic: 通用错误
 *   - InvalidServerName: 服务器名称无效
 *   - SlidingSyncVersion: Sliding Sync 版本不支持
 *   - Sdk: SDK 相关错误
 *   - ServerUnreachable: 服务器无法访问
 *   - SlidingSync: Sliding Sync 相关错误
 *   - WellKnownDeserializationException: well-known 配置解析失败
 *   - WellKnownLookupFailed: well-known 配置查询失败
 *   - EventCache: 事件缓存相关错误
 * - OidcException: OIDC 认证相关错误
 *   - Generic: OIDC 通用错误
 *   - CallbackUrlInvalid: 回调 URL 无效
 *   - Cancelled: 用户取消认证
 *   - MetadataInvalid: OIDC 元数据无效
 *   - NotSupported: OIDC 不支持
 * - 其他异常：转换为通用认证错误
 *
 * @see AuthenticationException Android 层的认证异常类型
 */
fun Throwable.mapAuthenticationException(): AuthenticationException {
    return when (this) {
        // 已经是 Android 层的认证异常，直接返回
        is AuthenticationException -> this

        // 客户端构建异常
        is ClientBuildException -> when (this) {
            is ClientBuildException.Generic -> AuthenticationException.Generic(message)
            is ClientBuildException.InvalidServerName -> AuthenticationException.InvalidServerName(message)
            is ClientBuildException.SlidingSyncVersion -> AuthenticationException.SlidingSyncVersion(message)
            is ClientBuildException.Sdk -> AuthenticationException.Generic(message)
            is ClientBuildException.ServerUnreachable -> AuthenticationException.ServerUnreachable(message)
            is ClientBuildException.SlidingSync -> AuthenticationException.Generic(message)
            is ClientBuildException.WellKnownDeserializationException -> AuthenticationException.Generic(message)
            is ClientBuildException.WellKnownLookupFailed -> AuthenticationException.Generic(message)
            is ClientBuildException.EventCache -> AuthenticationException.Generic(message)
        }

        // OIDC 认证异常
        is OidcException -> when (this) {
            is OidcException.Generic -> AuthenticationException.Oidc(message)
            is OidcException.CallbackUrlInvalid -> AuthenticationException.Oidc(message)
            is OidcException.Cancelled -> AuthenticationException.Oidc(message)
            is OidcException.MetadataInvalid -> AuthenticationException.Oidc(message)
            is OidcException.NotSupported -> AuthenticationException.Oidc(message)
        }

        // 其他未知异常，转换为通用认证异常
        else -> AuthenticationException.Generic(message)
    }
}
