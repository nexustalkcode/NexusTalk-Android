/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 引导流程配置 (OnBoarding Configuration)
 *
 * 此对象包含用户首次使用应用时的引导流程相关配置项。
 * 控制新用户是否可以注册新账户等引导功能。
 */
object OnBoardingConfig {
    /** 是否允许用户通过应用创建新账户。设置为true时，用户可以在应用内注册新账号；设置为false时，用户只能通过现有账号登录 */
    const val CAN_CREATE_ACCOUNT = true
}
