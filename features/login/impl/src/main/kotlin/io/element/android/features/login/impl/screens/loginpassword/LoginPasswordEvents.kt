/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.loginpassword

/**
 * 登录密码事件接口
 *
 * 定义登录密码页面的用户交互事件。
 */
sealed interface LoginPasswordEvents {
    /**
     * 设置登录名/用户名事件
     *
     * @property login 用户名或 Matrix ID
     */
    data class SetLogin(val login: String) : LoginPasswordEvents

    /**
     * 设置密码事件
     *
     * @property password 用户密码
     */
    data class SetPassword(val password: String) : LoginPasswordEvents

    /** 提交登录事件 - 用户点击登录按钮 */
    data object Submit : LoginPasswordEvents

    /** 清除错误事件 */
    data object ClearError : LoginPasswordEvents
}
