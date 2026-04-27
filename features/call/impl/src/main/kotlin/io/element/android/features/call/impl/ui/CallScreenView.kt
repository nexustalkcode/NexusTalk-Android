/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.viewinterop.AndroidView
import io.element.android.features.call.impl.R
import io.element.android.features.call.impl.pip.PictureInPictureEvents
import io.element.android.features.call.impl.pip.PictureInPictureState
import io.element.android.features.call.impl.pip.aPictureInPictureState
import io.element.android.features.call.impl.utils.InvalidAudioDeviceReason
import io.element.android.features.call.impl.utils.WebViewAudioManager
import io.element.android.features.call.impl.utils.WebViewPipController
import io.element.android.features.call.impl.utils.WebViewWidgetMessageInterceptor
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import timber.log.Timber

private const val CALL_WEB_VIEW_LOGGER_TAG = "ElementCallWebView"

/** 权限请求回调类型别名 */
typealias RequestPermissionCallback = (Array<String>) -> Unit

/**
 * 通话界面导航器接口
 *
 * 定义了通话界面的导航操作。
 */
interface CallScreenNavigator {
    /**
     * 关闭通话界面
     */
    fun close()
}

@Composable
internal fun CallScreenView(
    state: CallScreenState,
    pipState: PictureInPictureState,
    onConsoleMessage: (ConsoleMessage) -> Unit,
    requestPermissions: (Array<String>, RequestPermissionCallback) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun handleBack() {
        Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
            "Back pressed from call screen, supportPip=%s isInPictureInPicture=%s",
            pipState.supportPip,
            pipState.isInPictureInPicture,
        )
        if (pipState.supportPip) {
            pipState.eventSink.invoke(PictureInPictureEvents.EnterPictureInPicture)
        } else {
            state.eventSink(CallScreenEvents.Hangup)
        }
    }

    Scaffold(
        modifier = modifier,
    ) { padding ->
        BackHandler {
            handleBack()
        }
        if (state.webViewError != null) {
            ErrorDialog(
                content = buildString {
                    append(stringResource(CommonStrings.error_unknown))
                    state.webViewError.takeIf { it.isNotEmpty() }?.let { append("\n\n").append(it) }
                },
                onSubmit = { state.eventSink(CallScreenEvents.Hangup) },
            )
        } else {
            var webViewAudioManager by remember { mutableStateOf<WebViewAudioManager?>(null) }
            val coroutineScope = rememberCoroutineScope()

            var invalidAudioDeviceReason by remember { mutableStateOf<InvalidAudioDeviceReason?>(null) }
            invalidAudioDeviceReason?.let {
                InvalidAudioDeviceDialog(invalidAudioDeviceReason = it) {
                    invalidAudioDeviceReason = null
                }
            }

            CallWebView(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .fillMaxSize(),
                url = state.urlState,
                userAgent = state.userAgent,
                onPermissionsRequest = { request ->
                    val androidPermissions = mapWebkitPermissions(request.resources)
                    Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
                        "WebView permission request received: webkitResources=%s mappedAndroidPermissions=%s origin=%s",
                        request.resources.toDebugString(),
                        androidPermissions,
                        request.origin?.toRedactedLogString(),
                    )
                    val callback: RequestPermissionCallback = { grantedWebkitPermissions ->
                        Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
                            "Granting WebView permission request: requestedWebkitResources=%s grantedWebkitResources=%s origin=%s",
                            request.resources.toDebugString(),
                            grantedWebkitPermissions.toDebugString(),
                            request.origin?.toRedactedLogString(),
                        )
                        request.grant(grantedWebkitPermissions)
                    }
                    requestPermissions(androidPermissions.toTypedArray(), callback)
                },
                onConsoleMessage = onConsoleMessage,
                onCreateWebView = { webView ->
                    Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Created Element Call WebView instance=%s", webView.hashCode())
                    webView.addBackHandler(onBackPressed = ::handleBack)
                    val interceptor = WebViewWidgetMessageInterceptor(
                        webView = webView,
                        onUrlLoaded = { url ->
                            Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Element Call WebView page finished: url=%s", url.toRedactedLogString())
                            webView.evaluateJavascript("controls.onBackButtonPressed = () => { backHandler.onBackPressed() }", null)
                            if (webViewAudioManager?.isInCallMode?.get() == false) {
                                Timber.d("URL ${url.toRedactedLogString()} is loaded, starting in-call audio mode")
                                webViewAudioManager?.onCallStarted()
                            } else {
                                Timber.d("Can't start in-call audio mode since the app is already in it.")
                            }
                        },
                        onError = { state.eventSink(CallScreenEvents.OnWebViewError(it)) },
                    )
                    webViewAudioManager = WebViewAudioManager(
                        webView = webView,
                        coroutineScope = coroutineScope,
                        onInvalidAudioDeviceAdded = { invalidAudioDeviceReason = it },
                    )
                    state.eventSink(CallScreenEvents.SetupMessageChannels(interceptor))
                    val pipController = WebViewPipController(webView)
                    pipState.eventSink(PictureInPictureEvents.SetPipController(pipController))
                },
                onDestroyWebView = {
                    Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d("Destroying Element Call WebView instance=%s url=%s", it.hashCode(), it.url?.toRedactedLogString())
                    // Reset audio mode
                    webViewAudioManager?.onCallStopped()
                }
            )
            when (state.urlState) {
                AsyncData.Uninitialized,
                is AsyncData.Loading ->
                    ProgressDialog(text = stringResource(id = CommonStrings.common_please_wait))
                is AsyncData.Failure -> {
                    Timber.e(state.urlState.error, "WebView failed to load URL: ${state.urlState.error.message}")
                    ErrorDialog(
                        content = state.urlState.error.message.orEmpty(),
                        onSubmit = { state.eventSink(CallScreenEvents.Hangup) },
                    )
                }
                is AsyncData.Success -> Unit
            }
        }
    }
}

@Composable
private fun InvalidAudioDeviceDialog(
    invalidAudioDeviceReason: InvalidAudioDeviceReason,
    onDismiss: () -> Unit,
) {
    ErrorDialog(
        content = when (invalidAudioDeviceReason) {
            InvalidAudioDeviceReason.BT_AUDIO_DEVICE_DISABLED -> {
                stringResource(R.string.call_invalid_audio_device_bluetooth_devices_disabled)
            }
        },
        onSubmit = onDismiss,
    )
}

@Composable
private fun CallWebView(
    url: AsyncData<String>,
    userAgent: String,
    onPermissionsRequest: (PermissionRequest) -> Unit,
    onConsoleMessage: (ConsoleMessage) -> Unit,
    onCreateWebView: (WebView) -> Unit,
    onDestroyWebView: (WebView) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("WebView - can't be previewed")
        }
    } else {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    onCreateWebView(this)
                    setup(
                        userAgent = userAgent,
                        onPermissionsRequested = onPermissionsRequest,
                        onConsoleMessage = onConsoleMessage,
                    )
                }
            },
            update = { webView ->
                if (url is AsyncData.Success && webView.url != url.data) {
                    Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
                        "Loading Element Call URL in WebView: currentUrl=%s targetUrl=%s",
                        webView.url?.toRedactedLogString(),
                        url.data.toRedactedLogString(),
                    )
                    webView.loadUrl(url.data)
                }
            },
            onRelease = { webView ->
                onDestroyWebView(webView)
                webView.destroy()
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.setup(
    userAgent: String,
    onPermissionsRequested: (PermissionRequest) -> Unit,
    onConsoleMessage: (ConsoleMessage) -> Unit,
) {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    with(settings) {
        javaScriptEnabled = true
        allowContentAccess = true
        allowFileAccess = true
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        @Suppress("DEPRECATION")
        databaseEnabled = true
        loadsImagesAutomatically = true
        userAgentString = userAgent
    }
    Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
        "Configured Element Call WebView settings: mediaPlaybackRequiresUserGesture=%s domStorageEnabled=%s allowFileAccess=%s userAgentLength=%s",
        settings.mediaPlaybackRequiresUserGesture,
        settings.domStorageEnabled,
        settings.allowFileAccess,
        settings.userAgentString.length,
    )

    webChromeClient = object : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).d(
                "WebChromeClient.onPermissionRequest: resources=%s origin=%s",
                request.resources.toDebugString(),
                request.origin?.toRedactedLogString(),
            )
            onPermissionsRequested(request)
        }

        override fun onPermissionRequestCanceled(request: PermissionRequest) {
            Timber.tag(CALL_WEB_VIEW_LOGGER_TAG).w(
                "WebChromeClient.onPermissionRequestCanceled: resources=%s origin=%s",
                request.resources.toDebugString(),
                request.origin?.toRedactedLogString(),
            )
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            onConsoleMessage(consoleMessage)
            return true
        }
    }
}

private fun WebView.addBackHandler(onBackPressed: () -> Unit) {
    addJavascriptInterface(
        object {
            @Suppress("unused")
            @JavascriptInterface
            fun onBackPressed() = onBackPressed()
        },
        "backHandler"
    )
}

private fun Array<String>.toDebugString(): String = joinToString(prefix = "[", postfix = "]")

private fun Uri.toRedactedLogString(): String {
    return buildString {
        scheme?.let { append(it).append("://") }
        host?.let { append(it) }
        if (path.isNullOrBlank()) {
            append("/")
        } else {
            append(path)
        }
        if (!query.isNullOrBlank()) {
            append("?<redacted>")
        }
        if (!fragment.isNullOrBlank()) {
            append("#<redacted>")
        }
    }
}

private fun String.toRedactedLogString(): String = Uri.parse(this).toRedactedLogString()

@PreviewsDayNight
@Composable
internal fun CallScreenViewPreview(
    @PreviewParameter(CallScreenStateProvider::class) state: CallScreenState,
) = ElementPreview {
    CallScreenView(
        state = state,
        pipState = aPictureInPictureState(),
        requestPermissions = { _, _ -> },
        onConsoleMessage = {},
    )
}

@PreviewsDayNight
@Composable
internal fun InvalidAudioDeviceDialogPreview() = ElementPreview {
    InvalidAudioDeviceDialog(invalidAudioDeviceReason = InvalidAudioDeviceReason.BT_AUDIO_DEVICE_DISABLED) {}
}
