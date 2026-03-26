/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import kotlinx.coroutines.flow.Flow

/**
 * 小组件消息拦截器接口
 *
 * 定义了用于在应用和 Widget（WebView）之间传递消息的接口。
 *
 * @see WebViewWidgetMessageInterceptor WebView 实现
 */
interface WidgetMessageInterceptor {
    /**
     * 拦截到的消息流
     *
     * 从 WebView 拦截到的消息会以 Flow 的形式发出。
     */
    val interceptedMessages: Flow<String>

    /**
     * 发送消息到 WebView
     *
     * @param message 要发送的消息（JSON 字符串）
     */
    fun sendMessage(message: String)
}
