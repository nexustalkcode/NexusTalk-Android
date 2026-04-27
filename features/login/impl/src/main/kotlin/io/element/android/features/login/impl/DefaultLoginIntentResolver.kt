/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl

import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.login.api.LoginIntentResolver
import io.element.android.features.login.api.LoginParams

@ContributesBinding(AppScope::class)
/**
 * 默认的登录链接解析器。
 *
 * 用于从 Element 移动端 deeplink 中提取账号提供商和登录 hint。
 */
class DefaultLoginIntentResolver : LoginIntentResolver {
    /**
     * 解析登录 deeplink。
     *
     * @param uriString 待解析的 URI 字符串。
     * @return 成功时返回登录参数，无法识别时返回 `null`。
     */
    override fun parse(uriString: String): LoginParams? {
        val uri = uriString.toUri()
        if (uri.host != "mobile.element.io") return null
        if (uri.path.orEmpty().startsWith("/element").not()) return null
        val accountProvider = uri.getQueryParameter("account_provider") ?: return null
        val loginHint = uri.getQueryParameter("login_hint")
        return LoginParams(
            accountProvider = accountProvider,
            loginHint = loginHint,
        )
    }
}
