/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import io.element.android.libraries.matrix.api.auth.OidcPrompt
import org.matrix.rustcomponents.sdk.OidcPrompt as RustOidcPrompt

/**
 * 将 Android 层的 OidcPrompt 转换为 Rust SDK 层的 OidcPrompt
 *
 * OIDC Prompt 用于指定在 OIDC (OpenID Connect) 认证流程中向授权服务器请求的行为类型。
 * 这个扩展函数将 Android 定义的提示类型映射到 Rust SDK 相应的类型。
 *
 * @receiver Android 层的 OidcPrompt 对象
 * @return 转换后的 Rust SDK OidcPrompt 对象
 *
 * Prompt 类型说明：
 * - [OidcPrompt.Login] -> RustOidcPrompt.Unknown("consent"): 登录并请求用户同意
 * - [OidcPrompt.Create] -> RustOidcPrompt.Create: 创建新账户
 * - [OidcPrompt.Unknown] -> RustOidcPrompt.Unknown: 未知/自定义提示类型
 *
 * @see OidcPrompt Android 层的 OIDC 提示类型
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html">OIDC 核心规范</a>
 */
internal fun OidcPrompt.toRustPrompt(): RustOidcPrompt {
    return when (this) {
        // 登录提示，映射为需要用户同意的未知提示类型
        OidcPrompt.Login -> RustOidcPrompt.Unknown("consent")

        // 创建账户提示
        OidcPrompt.Create -> RustOidcPrompt.Create

        // 未知/自定义提示，直接传递其值
        is OidcPrompt.Unknown -> RustOidcPrompt.Unknown(value)
    }
}
