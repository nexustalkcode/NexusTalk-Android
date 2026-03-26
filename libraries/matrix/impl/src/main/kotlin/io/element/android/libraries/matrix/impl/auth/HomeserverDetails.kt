/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import io.element.android.libraries.matrix.api.auth.MatrixHomeServerDetails
import org.matrix.rustcomponents.sdk.HomeserverLoginDetails

/**
 * 将 Rust SDK 的 HomeserverLoginDetails 转换为 Android 使用的 MatrixHomeServerDetails
 *
 * 这是一个扩展函数，用于在 Rust SDK 和 Android 层之间转换 homeserver 详情数据。
 * 它提取 Rust 层的 homeserver 信息并映射到 Android 定义的数据类中。
 *
 * @receiver Rust SDK 的 HomeserverLoginDetails 对象
 * @return 转换后的 MatrixHomeServerDetails 对象，包含：
 *         - url: Homeserver 的 URL 地址
 *         - supportsPasswordLogin: 是否支持密码登录
 *         - supportsOidcLogin: 是否支持 OIDC (OpenID Connect) 登录
 *
 * @see MatrixHomeServerDetails Android 层的 homeserver 详情数据类
 * @see HomeserverLoginDetails Rust SDK 的 homeserver 登录详情
 */
fun HomeserverLoginDetails.map(): MatrixHomeServerDetails = use {
    MatrixHomeServerDetails(
        url = url(),
        supportsPasswordLogin = supportsPasswordLogin(),
        supportsOidcLogin = supportsOidcLogin(),
    )
}
