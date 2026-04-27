/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.deeplink.api

import android.content.Intent

/**
 * 应用内部 deeplink 解析接口。
 */
fun interface DeeplinkParser {
    /**
     * 从系统 Intent 中解析 deeplink 数据。
     */
    fun getFromIntent(intent: Intent): DeeplinkData?
}
