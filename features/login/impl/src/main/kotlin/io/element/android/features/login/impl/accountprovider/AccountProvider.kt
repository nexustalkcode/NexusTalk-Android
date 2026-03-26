/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.accountprovider

/**
 * 账户提供商数据类
 *
 * 表示一个 Matrix 账户提供商（homeserver）的信息。
 * 用于在登录流程中显示和选择不同的服务器选项。
 *
 * @property url 服务器的完整 URL 地址
 * @property title 显示标题，默认为 URL 去除协议前缀
 * @property subtitle 服务器的描述信息，可为 null
 * @property isPublic 是否为公共服务器（任何人都可以注册）
 * @property isMatrixOrg 是否为官方 matrix.org 服务器
 */
data class AccountProvider(
    val url: String,
    val title: String = url.removePrefix("https://").removePrefix("http://"),
    val subtitle: String? = null,
    val isPublic: Boolean = false,
    val isMatrixOrg: Boolean = false,
)
