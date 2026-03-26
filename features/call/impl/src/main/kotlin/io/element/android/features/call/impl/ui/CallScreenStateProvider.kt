/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncData

/**
 * 通话界面状态预览参数提供者
 *
 * 用于在 Jetpack Compose 预览中提供多种通话界面状态。
 * 继承 PreviewParameterProvider 以支持 @PreviewParameter 注解。
 *
 * @see CallScreenState 通话界面状态
 * @see aCallScreenState 创建测试状态的工具函数
 */
open class CallScreenStateProvider : PreviewParameterProvider<CallScreenState> {
    override val values: Sequence<CallScreenState>
        get() = sequenceOf(
            aCallScreenState(),
            aCallScreenState(urlState = AsyncData.Loading()),
            aCallScreenState(urlState = AsyncData.Failure(Exception("An error occurred"))),
            aCallScreenState(webViewError = "Error details from WebView"),
        )
}

/**
 * 创建通话界面测试状态
 *
 * 用于测试和预览通话界面状态的工具函数。
 *
 * @param urlState URL 加载状态（默认成功状态）
 * @param webViewError WebView 错误信息（默认 null）
 * @param userAgent 用户代理字符串（默认空字符串）
 * @param isCallActive 通话是否处于活动状态（默认 true）
 * @param isInWidgetMode 是否处于小组件模式（默认 false）
 * @param eventSink 事件处理函数（默认空函数）
 * @return CallScreenState 通话界面状态对象
 *
 * @see CallScreenState 通话界面状态数据类
 */
internal fun aCallScreenState(
    urlState: AsyncData<String> = AsyncData.Success("https://call.element.io/some-actual-call?with=parameters"),
    webViewError: String? = null,
    userAgent: String = "",
    isCallActive: Boolean = true,
    isInWidgetMode: Boolean = false,
    eventSink: (CallScreenEvents) -> Unit = {},
): CallScreenState {
    return CallScreenState(
        urlState = urlState,
        webViewError = webViewError,
        userAgent = userAgent,
        isCallActive = isCallActive,
        isInWidgetMode = isInWidgetMode,
        eventSink = eventSink,
    )
}
