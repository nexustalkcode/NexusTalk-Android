/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.oidc

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.matrix.api.auth.OidcRedirectUrlProvider
import io.element.android.services.toolbox.api.strings.StringProvider
import io.element.android.x.R

/**
 * 默认的 OIDC 重定向 URL 提供者。
 *
 * 实现 OidcRedirectUrlProvider 接口，
 * 负责提供 OIDC 登录流程中的重定向 URL 方案。
 * 使用 StringProvider 获取本地化的 URL 方案字符串。
 *
 * 通过 @ContributesBinding 注解将其绑定到 AppScope。
 */
@ContributesBinding(AppScope::class)
class DefaultOidcRedirectUrlProvider(
    private val stringProvider: StringProvider,
) : OidcRedirectUrlProvider {
    override fun provide() = buildString {
        append(stringProvider.getString(R.string.login_redirect_scheme))
        append(":/")
    }
}
