/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * Matrix 配置 (Matrix Configuration)
 *
 * 此对象包含与Matrix协议相关的配置项。
 * Matrix是一个去中心化的即时通讯协议，Element就是基于此协议构建的。
 */
object MatrixConfiguration {
    /** Matrix.to永久链接的基础URL。用于生成指向Matrix房间、用户或事件的永久链接 */
    const val MATRIX_TO_PERMALINK_BASE_URL: String = "https://matrix.to/#/"

    /**
     * 客户端永久链接基础URL。如果不为null，将使用此URL而不是matrix.to来生成永久链接。
     * 这允许自定义Matrix服务器提供自己的永久链接服务。
     */
    val clientPermalinkBaseUrl: String? = null
}
