/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.createaccount

/**
 * 创建账号页面可能触发的事件。
 */
sealed interface CreateAccountEvents {
    /** 更新当前 WebView 的页面加载进度。 */
    data class SetPageProgress(val progress: Int) : CreateAccountEvents

    /** 收到注册页通过消息通道发送的结果消息。 */
    data class OnMessageReceived(val message: String) : CreateAccountEvents
}
