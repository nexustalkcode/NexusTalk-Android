/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 身份验证配置 (Authentication Configuration)
 *
 * 此对象包含用户登录和身份验证相关的配置项。
 * 包括默认服务器地址、Sliding Sync代理设置等。
 */
object AuthenticationConfig {
    /** Matrix.org官方服务器的URL地址。这是Element的默认Home Server，用于新用户注册和登录 */
    const val MATRIX_ORG_URL = "https://nexustalk.space"
//    const val MATRIX_ORG_URL = "https://matrix.org"

    /**
     * Sliding Sync的详细文档URL。Sliding Sync是Matrix的新一代同步API，
     * 该链接指向官方文档，帮助用户了解什么是Sliding Sync以及如何在家园服务器上启用它。
     */
    const val SLIDING_SYNC_READ_MORE_URL = "https://github.com/matrix-org/sliding-sync/blob/main/docs/Landing.md"

    /**
     * 强制使用的Sliding Sync代理URL。如果设置为此值不为null，将忽略服务器 .well-known 文件中配置的代理URL。
     * 这允许开发者强制使用特定的代理服务器进行开发和测试。
     * 设置为null时，将使用服务器配置的默认代理。
     */
    val SLIDING_SYNC_PROXY_URL: String? = null
}
