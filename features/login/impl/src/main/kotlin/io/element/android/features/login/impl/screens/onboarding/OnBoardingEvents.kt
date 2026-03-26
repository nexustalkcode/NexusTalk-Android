/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

/**
 * 初始页面事件密封接口
 *
 * 定义初始页面可能发生的用户交互事件。
 */
sealed interface OnBoardingEvents {
    /**
     * 登录事件
     *
     * @property defaultAccountProvider 默认账户提供商
     */
    data class OnSignIn(
        val defaultAccountProvider: String
    ) : OnBoardingEvents

    /**
     * 创建账户事件
     *
     */
    data object OnCreateAccount : OnBoardingEvents

    /** 版本点击事件 */
    data object OnVersionClick : OnBoardingEvents
    /** 清除错误事件 */
    data object ClearError : OnBoardingEvents
}
