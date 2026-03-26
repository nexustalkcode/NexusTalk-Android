/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.loginpassword

import android.os.Parcelable
import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.parcelize.Parcelize

/**
 * 登录密码状态数据类
 *
 * @property accountProvider 账户提供商
 * @property formState 登录表单状态
 * @property loginAction 登录操作的异步状态
 * @property eventSink 事件处理函数
 */
data class LoginPasswordState(
    val accountProvider: AccountProvider,
    val formState: LoginFormState,
    val loginAction: AsyncData<SessionId>,
    val eventSink: (LoginPasswordEvents) -> Unit
) {
    /** 是否允许提交 - 登录名和密码都不为空且未失败 */
    val submitEnabled: Boolean
        get() = loginAction !is AsyncData.Failure &&
            formState.login.isNotEmpty() &&
            formState.password.isNotEmpty()
}

/**
 * 登录表单状态数据类
 *
 * @property login 用户名
 * @property password 密码
 */
@Parcelize
data class LoginFormState(
    val login: String,
    val password: String,
) : Parcelable {
    companion object {
        /** 默认空表单状态 */
        val Default = LoginFormState("", "")
    }
}
