/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import android.webkit.PermissionRequest
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.content.IntentCompat
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.Inject
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.CallType.ExternalUrl
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.call.impl.DefaultElementCallEntryPoint
import io.element.android.features.call.impl.di.CallBindings
import io.element.android.features.call.impl.pip.PictureInPictureEvents
import io.element.android.features.call.impl.pip.PictureInPicturePresenter
import io.element.android.features.call.impl.pip.PictureInPictureState
import io.element.android.features.call.impl.pip.PipView
import io.element.android.features.call.impl.services.CallForegroundService
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.call.impl.utils.CallIntentDataParser
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.androidutils.browser.ConsoleMessageLogger
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.audio.api.AudioFocus
import io.element.android.libraries.audio.api.AudioFocusRequester
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import kotlinx.coroutines.launch
import timber.log.Timber

/** 通话活动日志标签 */
private val loggerTag = LoggerTag("ElementCallActivity")
private val cameraLoggerTag = LoggerTag("ElementCallCamera")

/**
 * Element Call 主界面 Activity
 *
 * 通话功能的主要界面，负责加载和显示 Element Call WebView。
 * 实现了 CallScreenNavigator 接口用于导航控制，以及 PipView 接口用于画中画支持。
 *
 * 此 Activity 处理以下功能：
 * - 加载 Element Call WebView
 * - 管理通话权限
 * - 处理画中画模式
 * - 管理音频焦点
 * - 显示通话前台服务通知
 *
 * @see CallScreenNavigator 通话界面导航接口
 * @see PipView 画中画视图接口
 * @see CallScreenPresenter 通话界面 Presenter
 * @see CallScreenView 通话界面视图
 */
class ElementCallActivity :
    AppCompatActivity(),
    CallScreenNavigator,
    PipView {
    @Inject lateinit var callIntentDataParser: CallIntentDataParser
    @Inject lateinit var presenterFactory: CallScreenPresenter.Factory
    @Inject lateinit var appPreferencesStore: AppPreferencesStore
    @Inject lateinit var enterpriseService: EnterpriseService
    @Inject lateinit var pictureInPicturePresenter: PictureInPicturePresenter
    @Inject lateinit var activeCallManager: ActiveCallManager
    @Inject lateinit var elementCallEntryPoint: ElementCallEntryPoint
    @Inject lateinit var buildMeta: BuildMeta
    @Inject lateinit var audioFocus: AudioFocus
    @Inject lateinit var consoleMessageLogger: ConsoleMessageLogger

    private lateinit var presenter: Presenter<CallScreenState>

    private var requestPermissionCallback: RequestPermissionCallback? = null

    private val requestPermissionsLauncher = registerPermissionResultLauncher()

    private val webViewTarget = mutableStateOf<CallType?>(null)

    private var eventSink: ((CallScreenEvents) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindings<CallBindings>().inject(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }

        setCallType(intent)
        // If presenter is not created at this point, it means we have no call to display, the Activity is finishing, so return early
        if (!::presenter.isInitialized) {
            return
        }

        pictureInPicturePresenter.setPipView(this)

        Timber.tag(loggerTag.value).d(
            "Created ElementCallActivity with callType=%s context=%s",
            webViewTarget.value,
            pipDebugContext(),
        )

        setContent {
            val pipState = pictureInPicturePresenter.present()
            ListenToAndroidEvents(pipState)
            val colors by remember(webViewTarget.value?.getSessionId()) {
                enterpriseService.semanticColorsFlow(sessionId = webViewTarget.value?.getSessionId())
            }.collectAsState(SemanticColorsLightDark.default)
            ElementThemeApp(
                appPreferencesStore = appPreferencesStore,
                compoundLight = colors.light,
                compoundDark = colors.dark,
                buildMeta = buildMeta,
            ) {
                val state = presenter.present()
                val ringingCalls by activeCallManager.ringingCalls.collectAsState()
                val incomingCallOverlayState = remember(ringingCalls) {
                    ringingCalls.toIncomingCallOverlayState(
                        onAnswerClick = { notificationData ->
                            elementCallEntryPoint.startCall(
                                CallType.RoomCall(
                                    sessionId = notificationData.sessionId,
                                    roomId = notificationData.roomId,
                                )
                            )
                        },
                        onDeclineClick = { notificationData ->
                            lifecycleScope.launch {
                                activeCallManager.hangUpCall(
                                    callType = CallType.RoomCall(
                                        sessionId = notificationData.sessionId,
                                        roomId = notificationData.roomId,
                                    ),
                                    notificationData = notificationData,
                                )
                            }
                        },
                    )
                }
                eventSink = state.eventSink
                LaunchedEffect(incomingCallOverlayState.calls) {
                    Timber.tag("IncomingCallTrace").i(
                        "ElementCallActivity overlay mapped callCount=%s items=%s",
                        incomingCallOverlayState.calls.size,
                        incomingCallOverlayState.calls.joinToString { call ->
                            "id=${call.id},title=${call.title},subtitle=${call.subtitle}"
                        },
                    )
                }
                LaunchedEffect(state.isCallActive, state.isInWidgetMode) {
                    // Note when not in WidgetMode, isCallActive will never be true, so consider the call is active
                    if (state.isCallActive || !state.isInWidgetMode) {
                        setCallIsActive()
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    CallScreenView(
                    state = state,
                    pipState = pipState,
                    onConsoleMessage = {
                        consoleMessageLogger.log("ElementCall", it)
                    },
                    requestPermissions = { permissions, callback ->
                        /*
                         * 记录 Android 权限请求入口，方便和 WebKit 权限请求、系统授权结果按时间线对齐。
                         */
                        Timber.tag(cameraLoggerTag.value).d(
                            "Requesting Android permissions for WebView media: permissions=%s",
                            permissions.toDebugString(),
                        )
                        requestPermissionCallback = callback
                        requestPermissionsLauncher.launch(permissions)
                    }
                    )
                    IncomingCallOverlayHost(
                        state = incomingCallOverlayState,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    private fun setCallIsActive() {
        audioFocus.requestAudioFocus(
            requester = AudioFocusRequester.ElementCall,
            onFocusLost = {
                // If the audio focus is lost, we do not stop the call.
                Timber.tag(loggerTag.value).w("Audio focus lost")
            }
        )
        CallForegroundService.start(this)
    }

    @Composable
    private fun ListenToAndroidEvents(pipState: PictureInPictureState) {
        val pipEventSink by rememberUpdatedState(pipState.eventSink)
        DisposableEffect(Unit) {
            val onPictureInPictureModeChangedListener = Consumer { _: PictureInPictureModeChangedInfo ->
                Timber.tag(loggerTag.value).d(
                    "onPictureInPictureModeChanged listener fired: isInPictureInPictureMode=%s context=%s",
                    isInPictureInPictureMode,
                    pipDebugContext(),
                )
                pipEventSink(PictureInPictureEvents.OnPictureInPictureModeChanged(isInPictureInPictureMode))
                if (!isInPictureInPictureMode && !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    Timber.tag(loggerTag.value).d("Exiting PiP mode: Hangup the call")
                    eventSink?.invoke(CallScreenEvents.Hangup)
                }
            }
            addOnPictureInPictureModeChangedListener(onPictureInPictureModeChangedListener)
            onDispose {
                removeOnPictureInPictureModeChangedListener(onPictureInPictureModeChangedListener)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.tag(loggerTag.value).d("onNewIntent: action=%s data=%s context=%s", intent.action, intent.dataString, pipDebugContext())
        lifecycleScope.launch {
            activeCallManager.clearIncomingCallNotification()
        }
        setCallType(intent)
    }

    override fun onStart() {
        super.onStart()
        Timber.tag(loggerTag.value).d("onStart: context=%s", pipDebugContext())
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(true)
        }
        CallForegroundService.stop(this)
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(loggerTag.value).d("onResume: context=%s", pipDebugContext())
    }

    override fun onPause() {
        Timber.tag(loggerTag.value).d("onPause: context=%s", pipDebugContext())
        super.onPause()
    }

    override fun onStop() {
        Timber.tag(loggerTag.value).d("onStop: context=%s", pipDebugContext())
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(false)
        }
        if (!isFinishing && webViewTarget.value != null) {
            CallForegroundService.start(this)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Timber.tag(loggerTag.value).d("onDestroy: context=%s", pipDebugContext())
        super.onDestroy()
        audioFocus.releaseAudioFocus()
        CallForegroundService.stop(this)
        pictureInPicturePresenter.setPipView(null)
    }

    override fun onUserLeaveHint() {
        /*
         * 这里保留日志，但不再把 onUserLeaveHint 当成进入画中画的信号。
         * 实际排查发现系统会在并非用户点击 Home/返回的场景下回调它，
         * 如果继续在这里直接触发 PiP，会导致通话页误进入画中画。
         */
        Timber.tag(loggerTag.value).d("onUserLeaveHint callback ignored for PiP entry: context=%s", pipDebugContext())
        super.onUserLeaveHint()
    }

    override fun finish() {
        Timber.tag(loggerTag.value).d("finish requested: context=%s", pipDebugContext())
        // Also remove the task from recents
        finishAndRemoveTask()
    }

    override fun close() {
        finish()
    }

    private fun setCallType(intent: Intent?) {
        val extraCallType = intent?.let {
            IntentCompat.getParcelableExtra(intent, DefaultElementCallEntryPoint.EXTRA_CALL_TYPE, CallType::class.java)
        }
        val parsedExternalUrl = intent?.dataString?.let(::parseUrl)
        val callType = extraCallType ?: parsedExternalUrl?.let(::ExternalUrl)
        val currentCallType = webViewTarget.value
        /*
         * 这条日志用于区分两类入口：
         * 1. App 内部房间通话，通常携带 RoomCall extra；
         * 2. 外部 Element Call deeplink，通常会被解析成 ExternalUrl。
         * 出现游客页时先看这里，就能知道问题是不是入口分支走偏了。
         */
        Timber.tag(loggerTag.value).i(
            "Resolved incoming call intent: hasExtra=%s extraType=%s dataPresent=%s parsedExternalUrl=%s currentCallType=%s",
            extraCallType != null,
            extraCallType,
            intent?.dataString != null,
            parsedExternalUrl != null,
            currentCallType,
        )
        if (currentCallType == null) {
            if (callType == null) {
                Timber.tag(loggerTag.value).d("Re-opened the activity but we have no url to load or a cached one, finish the activity")
                finish()
            } else {
                Timber.tag(loggerTag.value).d("Set the call type and create the presenter")
                webViewTarget.value = callType
                presenter = presenterFactory.create(callType, this)
            }
        } else {
            if (callType == null) {
                Timber.tag(loggerTag.value).d("Coming back from notification, do nothing")
            } else if (callType != currentCallType) {
                Timber.tag(loggerTag.value).d("User starts another call, restart the Activity")
                setIntent(intent)
                recreate()
            } else {
                // Starting the same call again, should not happen, the UI is preventing this. But maybe when using external links.
                Timber.tag(loggerTag.value).d("Starting the same call again, do nothing")
            }
        }
    }

    private fun parseUrl(url: String?): String? = callIntentDataParser.parse(url)

    private fun registerPermissionResultLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            Timber.tag(cameraLoggerTag.value).d("Android permission result for WebView media: result=%s", permissions.toDebugString())
            val callback = requestPermissionCallback
            if (callback == null) {
                Timber.tag(cameraLoggerTag.value).w("Ignoring Android permission result because no WebView permission callback is pending")
                return@registerForActivityResult
            }
            val permissionsToGrant = mutableListOf<String>()
            permissions.forEach { (permission, granted) ->
                if (granted) {
                    val webKitPermission = when (permission) {
                        Manifest.permission.CAMERA -> PermissionRequest.RESOURCE_VIDEO_CAPTURE
                        Manifest.permission.RECORD_AUDIO -> PermissionRequest.RESOURCE_AUDIO_CAPTURE
                        else -> return@forEach
                    }
                    permissionsToGrant.add(webKitPermission)
                }
            }
            Timber.tag(cameraLoggerTag.value).d("Forwarding WebKit media grants to WebView: grants=%s", permissionsToGrant.toTypedArray().toDebugString())
            callback(permissionsToGrant.toTypedArray())
            requestPermissionCallback = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setPipParams() {
        val params = getPictureInPictureParams()
        Timber.tag(loggerTag.value).d(
            "Applying PiP params: autoEnterEnabled=%s aspectRatio=%s context=%s",
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            "3:5",
            pipDebugContext(),
        )
        setPictureInPictureParams(params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun enterPipMode(): Boolean {
        val canEnterFromLifecycle = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        Timber.tag(loggerTag.value).d(
            "enterPipMode requested: canEnterFromLifecycle=%s context=%s",
            canEnterFromLifecycle,
            pipDebugContext(),
        )
        return if (canEnterFromLifecycle) {
            val result = enterPictureInPictureMode(getPictureInPictureParams())
            Timber.tag(loggerTag.value).d("enterPictureInPictureMode result=%s context=%s", result, pipDebugContext())
            result
        } else {
            Timber.tag(loggerTag.value).w("Ignoring enterPipMode because lifecycle is not RESUMED: context=%s", pipDebugContext())
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPictureInPictureParams(): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            // Portrait for calls seems more appropriate
            .setAspectRatio(Rational(3, 5))
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setAutoEnterEnabled(true)
                }
            }
            .build()
    }

    override fun hangUp() {
        eventSink?.invoke(CallScreenEvents.Hangup)
    }

    /**
     * 统一整理 PiP 排查上下文，便于把生命周期、窗口焦点和系统 PiP 状态串成一条时间线。
     */
    private fun pipDebugContext(): String {
        return buildString {
            append("lifecycle=").append(lifecycle.currentState)
            append(", hasWindowFocus=").append(hasWindowFocus())
            append(", isInPip=").append(isInPictureInPictureMode)
            append(", isFinishing=").append(isFinishing)
            append(", isDestroyed=").append(isDestroyed)
            append(", isChangingConfigurations=").append(isChangingConfigurations)
            append(", requestPermissionPending=").append(requestPermissionCallback != null)
            append(", webViewTarget=").append(webViewTarget.value)
        }
    }
}

internal fun mapWebkitPermissions(permissions: Array<String>): List<String> {
    return permissions.mapNotNull { permission ->
        when (permission) {
            PermissionRequest.RESOURCE_AUDIO_CAPTURE -> Manifest.permission.RECORD_AUDIO
            PermissionRequest.RESOURCE_VIDEO_CAPTURE -> Manifest.permission.CAMERA
            else -> null
        }
    }
}

private fun Array<String>.toDebugString(): String = joinToString(prefix = "[", postfix = "]")

private fun Map<String, Boolean>.toDebugString(): String {
    return entries.joinToString(prefix = "[", postfix = "]") { (permission, granted) ->
        "$permission=$granted"
    }
}
