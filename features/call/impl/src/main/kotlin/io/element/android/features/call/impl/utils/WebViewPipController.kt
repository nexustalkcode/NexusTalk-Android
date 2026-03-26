/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import android.webkit.WebView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * WebView 画中画控制器
 *
 * 通过 JavaScript 与 Element Call WebView 通信，控制画中画模式。
 * 调用 WebView 中的 controls 对象方法来管理画中画状态。
 *
 * @param webView WebView 实例，用于执行 JavaScript
 *
 * @see PipController 画中画控制器接口
 * @see <a href="https://github.com/element-hq/element-call/blob/livekit/docs/controls.md#picture-in-picture">Element Call Controls 文档</a>
 */
class WebViewPipController(
    private val webView: WebView,
) : PipController {
    /**
     * 检查是否可以进入画中画模式
     *
     * 调用 WebView 中的 controls.canEnterPip() 方法。
     *
     * @return Boolean 如果可以进入画中画则返回 true
     */
    override suspend fun canEnterPip(): Boolean {
        return suspendCoroutine { continuation ->
            webView.evaluateJavascript("controls.canEnterPip()") { result ->
                // Note if the method is not available, it will return "null"
                continuation.resume(result == "true" || result == "null")
            }
        }
    }

    /**
     * 进入画中画模式
     *
     * 调用 WebView 中的 controls.enablePip() 方法。
     */
    override fun enterPip() {
        webView.evaluateJavascript("controls.enablePip()", null)
    }

    /**
     * 退出画中画模式
     *
     * 调用 WebView 中的 controls.disablePip() 方法。
     */
    override fun exitPip() {
        webView.evaluateJavascript("controls.disablePip()", null)
    }
}
