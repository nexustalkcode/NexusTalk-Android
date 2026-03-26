/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.matrix.api.auth.OidcConfig
import io.element.android.libraries.matrix.api.auth.OidcRedirectUrlProvider
import org.matrix.rustcomponents.sdk.OidcConfiguration

/**
 * OIDC (OpenID Connect) 配置提供者
 *
 * 该类负责组装并提供 OIDC 认证所需的配置信息。
 * 在 Element (Matrix 客户端) 的 OAuth2/OIDC 登录流程中，
 * 这些配置会被传递给 Rust SDK，用于与身份提供商 (IdP) 进行认证交互。
 *
 * 使用 @Inject 注解，表明由依赖注入框架管理。
 *
 * @property buildMeta 应用元信息，用于获取应用名称等
 * @property oidcRedirectUrlProvider OIDC 回调 URL 提供者
 *
 * @see OidcConfig OIDC 配置常量
 * @see OidcRedirectUrlProvider 回调 URL 提供者接口
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html">OIDC 核心规范</a>
 */
@Inject
class OidcConfigurationProvider(
    private val buildMeta: BuildMeta,
    private val oidcRedirectUrlProvider: OidcRedirectUrlProvider,
) {

    /**
     * 获取 OIDC 配置
     *
     * @return 包含完整 OIDC 配置信息的 OidcConfiguration 对象
     *
     * 配置项说明：
     * - clientName: 应用名称
     * - redirectUri: OIDC 认证完成后的回调 URI
     * - clientUri: 客户端官网 URI
     * - logoUri: 应用 Logo URI
     * - tosUri: 服务条款链接
     * - policyUri: 隐私政策链接
     * - staticRegistrations: 静态注册信息
     */
    fun get(): OidcConfiguration = OidcConfiguration(
        clientName = buildMeta.applicationName,
        redirectUri = oidcRedirectUrlProvider.provide(),
        clientUri = OidcConfig.CLIENT_URI,
        logoUri = OidcConfig.LOGO_URI,
        tosUri = OidcConfig.TOS_URI,
        policyUri = OidcConfig.POLICY_URI,
        staticRegistrations = OidcConfig.STATIC_REGISTRATIONS,
    )
}
