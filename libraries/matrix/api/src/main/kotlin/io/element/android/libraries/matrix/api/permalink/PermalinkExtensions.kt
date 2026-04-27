/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.permalink

import io.element.android.libraries.core.extensions.replacePrefix

private const val MATRIX_TO_PERMALINK_BASE_URL = "https://matrix.to/#/"

/**
 * 将 Rust SDK 生成的标准 matrix.to permalink 归一化为业务侧指定的 permalink 域名。
 *
 * 这里故意只替换 permalink 的基础前缀，不触碰后面的房间 ID、用户 ID、事件 ID
 * 以及 `via` 等查询参数，避免在切换分享域名时误改 Matrix 实体本身。
 *
 * 如果输入本来就不是 matrix.to permalink，则保持原样返回，避免影响其他普通链接。
 */
fun String.normalizeMatrixPermalinkBaseUrl(targetBaseUrl: String): String {
    return replacePrefix(
        oldPrefix = MATRIX_TO_PERMALINK_BASE_URL,
        newPrefix = targetBaseUrl,
    )
}
