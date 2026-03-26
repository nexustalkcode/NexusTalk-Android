/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.resolver

/**
 * Homeserver 数据类
 *
 * 存储已解析和验证的 homeserver 信息。
 * URL 可能来自 wellknown 文件检索，也可能是直接验证的有效 URL。
 *
 * @property homeserverUrl homeserver 的完整 URL 地址
 * @see HomeserverResolver 用于解析和验证 homeserver
 */
data class HomeserverData(
    // The computed homeserver url, for which a wellknown file has been retrieved, or just a valid Url
    val homeserverUrl: String,
)
