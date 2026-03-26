/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.features.call.impl.utils.WidgetMessageInterceptor

/**
 * 通话界面事件密封接口
 *
 * 定义了通话界面中可能发生的各种用户交互事件。
 *
 * @see Hangup 挂断通话事件
 * @see SetupMessageChannels 设置消息通道事件
 * @see OnWebViewError WebView 错误事件
 */
sealed interface CallScreenEvents {
    /**
     * 挂断通话事件
     *
     * 用户点击挂断按钮或通过其他方式结束通话时触发。
     */
    data object Hangup : CallScreenEvents

    /**
     * 设置消息通道事件
     *
     * 在 WebView 准备好后，设置与 Widget 通信的消息拦截器。
     *
     * @property widgetMessageInterceptor 消息拦截器，用于处理 WebView 和应用之间的消息
     */
    data class SetupMessageChannels(val widgetMessageInterceptor: WidgetMessageInterceptor) : CallScreenEvents

    /**
     * WebView 错误事件
     *
     * 当 WebView 加载出错时触发。
     *
     * @property description 错误描述信息（可选）
     */
    data class OnWebViewError(val description: String?) : CallScreenEvents
}
