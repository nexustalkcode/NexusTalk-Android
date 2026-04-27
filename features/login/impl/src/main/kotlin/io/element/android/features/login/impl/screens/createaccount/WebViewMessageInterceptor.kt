/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.createaccount

import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

/**
 * 拦截注册 WebView 与原生层之间的消息通信。
 *
 * 负责把注册页通过 `postMessage` 发送的结果传回原生层，
 * 并把页面内需要跳到外部浏览器的链接交给回调处理。
 */
class WebViewMessageInterceptor(
    webView: WebView,
    private val debugLog: Boolean,
    private val onOpenExternalUrl: (String) -> Unit,
    private val onMessage: (String) -> Unit,
) {
    companion object {
        /**
         * JS 侧调用 `postMessage` 时使用的统一监听器名称。
         */
        const val LISTENER_NAME = "elementX"
    }

    /**
     * 在初始化时安装 WebViewClient 和消息桥接能力。
     */
    init {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                // We inject this JS code when the page starts loading to attach a message listener to the window.
                view?.evaluateJavascript(
                    """
                        window.addEventListener(
                          "mobileregistrationresponse",
                          (event) => {
                            let json = JSON.stringify(event.detail)
                            ${"console.log('message sent: ' + json);".takeIf { debugLog }}
                            $LISTENER_NAME.postMessage(json);
                          },
                          false,
                        );
                    """.trimIndent(),
                    null
                )
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request ?: return false
                // Load the URL in a Chrome Custom Tab, and return true to cancel the load
                onOpenExternalUrl(request.url.toString())
                return true
            }
        }

        // Use WebMessageListener if supported, otherwise use JavascriptInterface
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            // Create a WebMessageListener, which will receive messages from the WebView and reply to them
            val webMessageListener = WebViewCompat.WebMessageListener { _, message, _, _, _ ->
                onMessageReceived(message.data)
            }
            WebViewCompat.addWebMessageListener(
                webView,
                LISTENER_NAME,
                setOf("*"),
                webMessageListener
            )
        } else {
            webView.addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun postMessage(json: String?) {
                        onMessageReceived(json)
                    }
                },
                LISTENER_NAME,
            )
        }
    }

    /**
     * 把 WebView 回传的消息转交给调用方。
     */
    private fun onMessageReceived(json: String?) {
        json?.let { onMessage(it) }
    }
}
