/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.web

import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.appconfig.AuthenticationConfig
import io.element.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.element.android.libraries.wellknown.api.WellknownRetriever
import timber.log.Timber

/**
 * Web 客户端认证 URL 检索器接口
 *
 * 定义获取 Web 端认证页面 URL 的功能接口。
 * 用于在 WebView 中打开账户创建或登录页面。
 */
interface WebClientUrlForAuthenticationRetriever {
    /**
     * 检索认证页面 URL
     *
     * @param homeServerUrl homeserver URL
     * @return 认证页面的完整 URL
     * @throws AccountCreationNotSupported 如果不支持账户创建
     */
    suspend fun retrieve(homeServerUrl: String): String
}

/**
 * Web 客户端认证 URL 检索器默认实现
 *
 * 获取用于在 WebView 中进行账户创建的 URL。
 * 目前仅支持 matrix.org 的临时账户创建流程。
 *
 * @param wellknownRetriever Wellknown 信息检索器
 * @throws AccountCreationNotSupported 如果服务器不支持账户创建
 */
@ContributesBinding(AppScope::class)
class DefaultWebClientUrlForAuthenticationRetriever(
    private val wellknownRetriever: WellknownRetriever,
) : WebClientUrlForAuthenticationRetriever {
    override suspend fun retrieve(homeServerUrl: String): String {
        if (homeServerUrl != AuthenticationConfig.MATRIX_ORG_URL) {
            Timber.w("Temporary account creation flow is only supported on matrix.org")
            throw AccountCreationNotSupported()
        }
        val wellknown = wellknownRetriever.getElementWellKnown(homeServerUrl).dataOrNull()
            ?: throw AccountCreationNotSupported()
        val registrationHelperUrl = wellknown.registrationHelperUrl
        return if (registrationHelperUrl != null) {
            registrationHelperUrl.toUri()
                .buildUpon()
                .appendQueryParameter("hs_url", homeServerUrl)
                .build()
                .toString()
        } else {
            throw AccountCreationNotSupported()
        }
    }
}
