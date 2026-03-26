/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.login

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.login.impl.error.ChangeServerErrorProvider
import io.element.android.libraries.matrix.api.auth.AuthenticationException

/**
 * 登录模式视图错误预览参数提供者
 *
 * 用于在 Compose 预览中提供不同类型的登录模式错误测试数据。
 * 组合了 ChangeServerErrorProvider 和 AuthenticationException 的错误类型。
 *
 * @see LoginModeView 登录模式视图组件
 * @see ChangeServerErrorProvider 服务器错误提供者
 */
class LoginModeViewErrorProvider : PreviewParameterProvider<Exception> {
    override val values: Sequence<Exception>
        get() = ChangeServerErrorProvider().values +
            AuthenticationException.AccountAlreadyLoggedIn("@alice:matrix.org")
}
