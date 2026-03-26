/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.intent

import android.app.Activity
import androidx.compose.ui.platform.UriHandler
import io.element.android.libraries.androidutils.system.openUrlInExternalApp

/**
 * 安全 URI 处理器。
 *
 * 实现 Compose 的 UriHandler 接口，
 * 用于在 Compose UI 中安全地打开外部链接。
 * 封装了 Activity 的 openUrlInExternalApp 方法，
 * 确保 URL 打开操作在正确的上下文中执行。
 *
 * 主要用于 Compose 中处理链接点击事件。
 */
class SafeUriHandler(private val activity: Activity) : UriHandler {
    override fun openUri(uri: String) {
        activity.openUrlInExternalApp(uri)
    }
}
