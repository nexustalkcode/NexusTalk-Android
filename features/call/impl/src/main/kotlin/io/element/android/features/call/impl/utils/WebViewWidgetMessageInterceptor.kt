/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import io.element.android.features.call.impl.BuildConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber

private const val CALL_WEB_VIEW_LOGGER_TAG = "ElementCallWebView"

/**
 * WebView 小组件消息拦截器
 *
 * 处理 Element X 应用与 Element Call WebView 之间的消息通信。
 * 负责拦截 WebView 发送的消息并转发给应用，以及将应用的消息发送给 WebView。
 *
 * @param webView WebView 实例
 * @param onUrlLoaded URL 加载完成回调
 * @param onError 错误回调
 *
 * @see WidgetMessageInterceptor 消息拦截器接口
 */
class WebViewWidgetMessageInterceptor(
    private val webView: WebView,
    private val onUrlLoaded: (String) -> Unit,
    private val onError: (String?) -> Unit,
) : WidgetMessageInterceptor {
    companion object {
        /** JavaScript 监听器名称，用于在 WebView 中接收消息 */
        // We call both the WebMessageListener and the JavascriptInterface objects in JS with this
        // 'listenerName' so they can both receive the data from the WebView when
        // `${LISTENER_NAME}.postMessage(...)` is called
        const val LISTENER_NAME = "elementX"
    }

    // It's important to have extra capacity here to make sure we don't drop any messages
    override val interceptedMessages = MutableSharedFlow<String>(extraBufferCapacity = 10)

    init {
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(webView.context))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Element Call WebView page started: url=%s", url.toRedactedLogString())

                // Due to https://github.com/element-hq/element-x-android/issues/4097
                // we need to supply a logging implementation that correctly includes
                // objects in log lines.
                view.evaluateJavascript(
                    """
                        function logFn(consoleLogFn, ...args) {
                            consoleLogFn(
                                args.map(
                                    a => typeof a === "string" ? a : JSON.stringify(a)
                                ).join(' ')
                            );
                        };
                        globalThis.console.debug = logFn.bind(null, console.debug);
                        globalThis.console.log = logFn.bind(null, console.log);
                        globalThis.console.info = logFn.bind(null, console.info);
                        globalThis.console.warn = logFn.bind(null, console.warn);
                        globalThis.console.error = logFn.bind(null, console.error);
                    """.trimIndent(),
                    null
                )

                /*
                 * 注入媒体诊断脚本，用 console 日志观察 getUserMedia、MediaStreamTrack 和 video 元素状态，不改变页面业务逻辑。
                 */
                view.evaluateJavascript(mediaDiagnosticsScript, null)

                // We inject this JS code when the page starts loading to attach a message listener to the window.
                // This listener will receive both messages:
                // - EC widget API -> Element X (message.data.api == "fromWidget")
                // - Element X -> EC widget API (message.data.api == "toWidget"), we should ignore these
                view.evaluateJavascript(
                    """
                        window.addEventListener('message', function(event) {
                            let message = {data: event.data, origin: event.origin}
                            if (message.data.response && message.data.api == "toWidget"
                                || !message.data.response && message.data.api == "fromWidget") {
                                let json = JSON.stringify(event.data) 
                                ${"console.log('message sent: ' + json);".takeIf { BuildConfig.DEBUG }}
                                $LISTENER_NAME.postMessage(json);
                            } else {
                                ${"console.log('message received (ignored): ' + JSON.stringify(event.data));".takeIf { BuildConfig.DEBUG }}
                            }
                        });
                    """.trimIndent(),
                    null
                )
            }

            override fun onPageFinished(view: WebView, url: String) {
                Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Element Call WebView page finished in client: url=%s", url.toRedactedLogString())
                onUrlLoaded(url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                // No network for instance, transmit the error
                Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).e(
                    "onReceivedError error=%s description=%s requestUrl=%s isForMainFrame=%s currentUrl=%s",
                    error?.errorCode,
                    error?.description,
                    request?.url?.toString()?.toRedactedLogString(),
                    request?.isForMainFrame,
                    view?.url?.toRedactedLogString(),
                )

                // Only propagate the error if it happens while loading the current page
                if (view?.url == request?.url.toString()) {
                    onError(error?.description.toString())
                }

                super.onReceivedError(view, request, error)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).e(
                    "onReceivedHttpError status=%s reason=%s requestUrl=%s isForMainFrame=%s currentUrl=%s",
                    errorResponse?.statusCode,
                    errorResponse?.reasonPhrase,
                    request?.url?.toString()?.toRedactedLogString(),
                    request?.isForMainFrame,
                    view?.url?.toRedactedLogString(),
                )

                // Only propagate the error if it happens while loading the current page
                if (view?.url == request?.url.toString()) {
                    onError(errorResponse?.statusCode.toString())
                }

                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).e(
                    "onReceivedSslError primaryError=%s url=%s currentUrl=%s",
                    error?.primaryError,
                    error?.url?.toRedactedLogString(),
                    view?.url?.toRedactedLogString(),
                )

                // Only propagate the error if it happens while loading the current page
                if (view?.url == error?.url.toString()) {
                    onError(error?.toString())
                }

                super.onReceivedSslError(view, handler, error)
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun shouldInterceptRequest(view: WebView?, url: String): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(url.toUri())
            }
        }

        // Create a WebMessageListener, which will receive messages from the WebView and reply to them
        val webMessageListener = WebViewCompat.WebMessageListener { _, message, _, _, _ ->
            onMessageReceived(message.data)
        }

        // Use WebMessageListener if supported, otherwise use JavascriptInterface
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            WebViewCompat.addWebMessageListener(
                webView,
                LISTENER_NAME,
                setOf("*"),
                webMessageListener
            )
        } else {
            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun postMessage(json: String?) {
                    onMessageReceived(json)
                }
            }, LISTENER_NAME)
        }
    }

    override fun sendMessage(message: String) {
        Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Sending widget message to Element Call WebView: length=%s", message.length)
        webView.evaluateJavascript("postMessage($message, '*')", null)
    }

    private fun onMessageReceived(json: String?) {
        // Here is where we would handle the messages from the WebView, passing them to the Rust SDK
        if (json == null) {
            Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).w("Received null widget message from Element Call WebView")
            return
        }
        Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Received widget message from Element Call WebView: length=%s", json.length)
        interceptedMessages.tryEmit(json)
    }
}

private val mediaDiagnosticsScript = """
        (function() {
          if (globalThis.__elementXMediaDiagnosticsInstalled) {
            console.debug('[ElementXMedia] diagnostics already installed');
            return;
          }
          globalThis.__elementXMediaDiagnosticsInstalled = true;

          function safeJson(value) {
            try {
              return JSON.stringify(value);
            } catch (error) {
              return String(value);
            }
          }

          function log(message, data) {
            console.debug('[ElementXMedia] ' + message + (data === undefined ? '' : ' ' + safeJson(data)));
          }

          function trackStream(label, stream) {
            if (!stream || typeof stream.getTracks !== 'function') {
              log(label + ' returned without a MediaStream', { hasStream: !!stream });
              return;
            }
            log(label + ' stream', {
              id: stream.id,
              active: stream.active,
              tracks: stream.getTracks().map(function(track) {
                return {
                  id: track.id,
                  kind: track.kind,
                  label: track.label,
                  enabled: track.enabled,
                  muted: track.muted,
                  readyState: track.readyState
                };
              })
            });
            stream.getTracks().forEach(function(track) {
              ['ended', 'mute', 'unmute'].forEach(function(eventName) {
                track.addEventListener(eventName, function() {
                  log(label + ' track ' + eventName, {
                    id: track.id,
                    kind: track.kind,
                    enabled: track.enabled,
                    muted: track.muted,
                    readyState: track.readyState
                  });
                });
              });
            });
          }

          if (navigator.mediaDevices) {
            var originalGetUserMedia = navigator.mediaDevices.getUserMedia && navigator.mediaDevices.getUserMedia.bind(navigator.mediaDevices);
            if (originalGetUserMedia) {
              navigator.mediaDevices.getUserMedia = function(constraints) {
                log('getUserMedia called', constraints);
                return originalGetUserMedia(constraints).then(function(stream) {
                  trackStream('getUserMedia resolved', stream);
                  return stream;
                }).catch(function(error) {
                  log('getUserMedia rejected', { name: error && error.name, message: error && error.message });
                  throw error;
                });
              };
            }

            var originalEnumerateDevices = navigator.mediaDevices.enumerateDevices && navigator.mediaDevices.enumerateDevices.bind(navigator.mediaDevices);
            if (originalEnumerateDevices) {
              navigator.mediaDevices.enumerateDevices = function() {
                return originalEnumerateDevices().then(function(devices) {
                  log('enumerateDevices resolved', devices.map(function(device) {
                    return {
                      kind: device.kind,
                      label: device.label,
                      deviceIdPresent: !!device.deviceId,
                      groupIdPresent: !!device.groupId
                    };
                  }));
                  return devices;
                });
              };
            }
          } else {
            log('navigator.mediaDevices is unavailable');
          }

          function describeVideo(video) {
            var tracks = [];
            if (video.srcObject && typeof video.srcObject.getTracks === 'function') {
              tracks = video.srcObject.getTracks().map(function(track) {
                return {
                  id: track.id,
                  kind: track.kind,
                  enabled: track.enabled,
                  muted: track.muted,
                  readyState: track.readyState
                };
              });
            }
            return {
              id: video.id,
              className: video.className,
              readyState: video.readyState,
              videoWidth: video.videoWidth,
              videoHeight: video.videoHeight,
              paused: video.paused,
              muted: video.muted,
              hasSrcObject: !!video.srcObject,
              tracks: tracks
            };
          }

          var observedVideos = new WeakSet();
          function observeVideo(video) {
            if (observedVideos.has(video)) return;
            observedVideos.add(video);
            log('video element observed', describeVideo(video));
            [
              'loadedmetadata',
              'loadeddata',
              'canplay',
              'playing',
              'pause',
              'waiting',
              'stalled',
              'suspend',
              'emptied',
              'error',
              'resize'
            ].forEach(function(eventName) {
              video.addEventListener(eventName, function() {
                log('video ' + eventName, describeVideo(video));
              });
            });
          }

          function scanVideos() {
            document.querySelectorAll('video').forEach(observeVideo);
          }

          if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', scanVideos);
          } else {
            scanVideos();
          }

          if (document.documentElement) {
            new MutationObserver(scanVideos).observe(document.documentElement, { childList: true, subtree: true });
          }

          log('media diagnostics installed');
        })();
""".trimIndent()

private fun String.toRedactedLogString(): String = toUri().let { uri ->
    buildString {
        uri.scheme?.let { append(it).append("://") }
        uri.host?.let { append(it) }
        if (uri.path.isNullOrBlank()) {
            append("/")
        } else {
            append(uri.path)
        }
        if (!uri.query.isNullOrBlank()) {
            append("?<redacted>")
        }
        if (!uri.fragment.isNullOrBlank()) {
            append("#<redacted>")
        }
    }
}
