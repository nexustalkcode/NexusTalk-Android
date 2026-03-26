/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 登录参数数据类
 *
 * 表示从 mobile.element.io 链接启动登录流程时的参数。
 * 用于在应用启动时自动引导用户登录到特定的账户提供商。
 *
 * @property accountProvider 账户提供商 URL
 * @property loginHint 登录提示文本，可为 null
 */
@Parcelize
data class LoginParams(
    val accountProvider: String,
    val loginHint: String?
) : Parcelable
