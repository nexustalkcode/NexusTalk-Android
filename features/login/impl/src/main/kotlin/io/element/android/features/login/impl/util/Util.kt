/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.element.android.appconfig.AuthenticationConfig
import io.element.android.libraries.core.data.tryOrNull

/**
 * 打开了解更多页面
 *
 * 在浏览器中打开 Sliding Sync 的详细了解页面。
 *
 * @param context Android 上下文
 */
fun openLearnMorePage(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, AuthenticationConfig.SLIDING_SYNC_READ_MORE_URL.toUri())
    tryOrNull { context.startActivity(intent) }
}
