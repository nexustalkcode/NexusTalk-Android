/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.login

import io.element.android.libraries.matrix.api.auth.OidcDetails

/**
 * 登录模式密封接口
 *
 * 定义用户登录的不同方式。
 * 根据服务器支持的认证方式，登录流程会有所不同。
 *
 * @see LoginWithClassicPresenter 使用此接口确定登录方式
 * @see LoginModeView 显示登录模式的视图
 */
sealed interface LoginMode {
    /** 密码登录模式 - 使用用户名和密码进行认证 */
    data object PasswordLogin : LoginMode

    /**
     * OIDC 登录模式
     *
     * 使用 OpenID Connect 协议进行身份验证。
     *
     * @property oidcDetails OIDC 认证详情
     */
    data class Oidc(val oidcDetails: OidcDetails) : LoginMode

    /**
     * 账户创建模式
     *
     * 在 WebView 中打开账户创建页面。
     *
     * @property url 账户创建页面的 URL
     */
    data class AccountCreation(val url: String) : LoginMode
}
