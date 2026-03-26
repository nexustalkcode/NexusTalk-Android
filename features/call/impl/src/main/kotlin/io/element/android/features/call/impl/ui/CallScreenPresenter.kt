/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.call.api.CallType
import io.element.android.features.call.impl.data.WidgetMessage
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.call.impl.utils.CallWidgetProvider
import io.element.android.features.call.impl.utils.WidgetMessageInterceptor
import io.element.android.features.call.impl.utils.WidgetMessageSerializer
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.sync.SyncState
import io.element.android.libraries.matrix.api.widget.MatrixWidgetDriver
import io.element.android.libraries.network.useragent.UserAgentProvider
import io.element.android.services.analytics.api.ScreenTracker
import io.element.android.services.appnavstate.api.AppForegroundStateService
import io.element.android.services.toolbox.api.systemclock.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * 通话界面 Presenter
 *
 * 负责通话界面的业务逻辑和状态管理。处理通话的启动、消息传递、WebView 交互等。
 *
 * @property callType 通话类型
 * @property navigator 导航器，用于关闭界面
 * @property callWidgetProvider 通话小组件提供者
 * @property userAgentProvider 用户代理提供者
 * @property clock 系统时钟
 * @property dispatchers 协程调度器
 * @property matrixClientsProvider Matrix 客户端提供者
 * @property screenTracker 屏幕追踪器
 * @property activeCallManager 活动通话管理器
 * @property languageTagProvider 语言标签提供者
 * @property appForegroundStateService 应用前台状态服务
 * @property appCoroutineScope 应用协程作用域
 * @property widgetMessageSerializer 小组件消息序列化器
 *
 * @see Presenter 基础 Presenter 类
 * @see CallScreenState 通话界面状态
 * @see CallScreenEvents 通话界面事件
 */
@AssistedInject
class CallScreenPresenter(
    @Assisted private val callType: CallType,
    @Assisted private val navigator: CallScreenNavigator,
    private val callWidgetProvider: CallWidgetProvider,
    userAgentProvider: UserAgentProvider,
    private val clock: SystemClock,
    private val dispatchers: CoroutineDispatchers,
    private val matrixClientsProvider: MatrixClientProvider,
    private val screenTracker: ScreenTracker,
    private val activeCallManager: ActiveCallManager,
    private val languageTagProvider: LanguageTagProvider,
    private val appForegroundStateService: AppForegroundStateService,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope,
    private val widgetMessageSerializer: WidgetMessageSerializer,
) : Presenter<CallScreenState> {
    /**
     * CallScreenPresenter 工厂接口
     *
     * 用于依赖注入创建 CallScreenPresenter 实例。
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建通话界面 Presenter
         *
         * @param callType 通话类型
         * @param navigator 导航器
         * @return CallScreenPresenter 实例
         */
        fun create(callType: CallType, navigator: CallScreenNavigator): CallScreenPresenter
    }

    private val isInWidgetMode = callType is CallType.RoomCall
    private val userAgent = userAgentProvider.provide()

    @Composable
    override fun present(): CallScreenState {
        val coroutineScope = rememberCoroutineScope()
        val urlState = remember { mutableStateOf<AsyncData<String>>(AsyncData.Uninitialized) }
        val callWidgetDriver = remember { mutableStateOf<MatrixWidgetDriver?>(null) }
        val messageInterceptor = remember { mutableStateOf<WidgetMessageInterceptor?>(null) }
        var isWidgetLoaded by rememberSaveable { mutableStateOf(false) }
        var ignoreWebViewError by rememberSaveable { mutableStateOf(false) }
        var webViewError by remember { mutableStateOf<String?>(null) }
        val languageTag = languageTagProvider.provideLanguageTag()
        val theme = if (ElementTheme.isLightTheme) "light" else "dark"

        DisposableEffect(Unit) {
            coroutineScope.launch {
                // Sets the call as joined
                activeCallManager.joinedCall(callType)
                fetchRoomCallUrl(
                    inputs = callType,
                    urlState = urlState,
                    callWidgetDriver = callWidgetDriver,
                    languageTag = languageTag,
                    theme = theme,
                )
            }
            onDispose {
                appCoroutineScope.launch { activeCallManager.hangUpCall(callType) }
            }
        }

        when (callType) {
            is CallType.ExternalUrl -> {
                // No analytics yet for external calls
            }
            is CallType.RoomCall -> {
                screenTracker.TrackScreen(screen = MobileScreen.ScreenName.RoomCall)
            }
        }

        HandleMatrixClientSyncState()

        callWidgetDriver.value?.let { driver ->
            LaunchedEffect(Unit) {
                driver.incomingMessages
                    .onEach {
                        // Relay message to the WebView
                        messageInterceptor.value?.sendMessage(it)
                    }
                    .launchIn(this)

                driver.run()
            }
        }

        messageInterceptor.value?.let { interceptor ->
            LaunchedEffect(Unit) {
                interceptor.interceptedMessages
                    .onEach {
                        // We are receiving messages from the WebView, consider that the application is loaded
                        ignoreWebViewError = true
                        // Relay message to Widget Driver
                        callWidgetDriver.value?.send(it)

                        val parsedMessage = parseMessage(it)
                        if (parsedMessage?.direction == WidgetMessage.Direction.FromWidget) {
                            if (parsedMessage.action == WidgetMessage.Action.Close) {
                                close(callWidgetDriver.value, navigator)
                            } else if (parsedMessage.action == WidgetMessage.Action.ContentLoaded) {
                                isWidgetLoaded = true
                            }
                        }
                    }
                    .launchIn(this)
            }

            if (callType is CallType.RoomCall) {
                // Note: For external calls isWidgetLoaded will always be false
                LaunchedEffect(Unit) {
                    // Wait for the call to be joined, if it takes too long, we display an error
                    delay(10.seconds)

                    if (!isWidgetLoaded) {
                        Timber.w("The call took too long to load. Displaying an error before exiting.")

                        // This will display a simple 'Sorry, an error occurred' dialog and force the user to exit the call
                        webViewError = ""
                    }
                }
            }
        }

        fun handleEvent(event: CallScreenEvents) {
            when (event) {
                is CallScreenEvents.Hangup -> {
                    val widgetId = callWidgetDriver.value?.id
                    val interceptor = messageInterceptor.value
                    if (widgetId != null && interceptor != null && isWidgetLoaded) {
                        // If the call was joined, we need to hang up first. Then the UI will be dismissed automatically.
                        sendHangupMessage(widgetId, interceptor)
                        isWidgetLoaded = false

                        coroutineScope.launch {
                            // Wait for a couple of seconds to receive the hangup message
                            // If we don't get it in time, we close the screen anyway
                            delay(2.seconds)
                            close(callWidgetDriver.value, navigator)
                        }
                    } else {
                        coroutineScope.launch {
                            close(callWidgetDriver.value, navigator)
                        }
                    }
                }
                is CallScreenEvents.SetupMessageChannels -> {
                    messageInterceptor.value = event.widgetMessageInterceptor
                }
                is CallScreenEvents.OnWebViewError -> {
                    if (!ignoreWebViewError) {
                        webViewError = event.description.orEmpty()
                    }
                    // Else ignore the error, give a chance the Element Call to recover by itself.
                }
            }
        }

        return CallScreenState(
            urlState = urlState.value,
            webViewError = webViewError,
            userAgent = userAgent,
            isCallActive = isWidgetLoaded,
            isInWidgetMode = isInWidgetMode,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun fetchRoomCallUrl(
        inputs: CallType,
        urlState: MutableState<AsyncData<String>>,
        callWidgetDriver: MutableState<MatrixWidgetDriver?>,
        languageTag: String?,
        theme: String?,
    ) {
        urlState.runCatchingUpdatingState {
            when (inputs) {
                is CallType.ExternalUrl -> {
                    inputs.url
                }
                is CallType.RoomCall -> {
                    val result = callWidgetProvider.getWidget(
                        sessionId = inputs.sessionId,
                        roomId = inputs.roomId,
                        clientId = UUID.randomUUID().toString(),
                        languageTag = languageTag,
                        theme = theme,
                    ).getOrThrow()
                    callWidgetDriver.value = result.driver
                    Timber.d("Call widget driver initialized for sessionId: ${inputs.sessionId}, roomId: ${inputs.roomId}")
                    result.url
                }
            }
        }
    }

    @Composable
    private fun HandleMatrixClientSyncState() {
        val coroutineScope = rememberCoroutineScope()
        DisposableEffect(Unit) {
            val roomCallType = callType as? CallType.RoomCall ?: return@DisposableEffect onDispose {}
            val client = matrixClientsProvider.getOrNull(roomCallType.sessionId) ?: return@DisposableEffect onDispose {
                Timber.w("No MatrixClient found for sessionId, can't send call notification: ${roomCallType.sessionId}")
            }
            coroutineScope.launch {
                Timber.d("Observing sync state in-call for sessionId: ${roomCallType.sessionId}")
                client.syncService.syncState
                    .collect { state ->
                        if (state != SyncState.Running) {
                            appForegroundStateService.updateIsInCallState(true)
                        }
                    }
            }
            onDispose {
                Timber.d("Stopped observing sync state in-call for sessionId: ${roomCallType.sessionId}")
                // Make sure we mark the call as ended in the app state
                appForegroundStateService.updateIsInCallState(false)
            }
        }
    }

    private fun parseMessage(message: String): WidgetMessage? {
        return widgetMessageSerializer.deserialize(message).getOrNull()
    }

    private fun sendHangupMessage(widgetId: String, messageInterceptor: WidgetMessageInterceptor) {
        val message = WidgetMessage(
            direction = WidgetMessage.Direction.ToWidget,
            widgetId = widgetId,
            requestId = "widgetapi-${clock.epochMillis()}",
            action = WidgetMessage.Action.HangUp,
            data = null,
        )
        messageInterceptor.sendMessage(widgetMessageSerializer.serialize(message))
    }

    private fun CoroutineScope.close(widgetDriver: MatrixWidgetDriver?, navigator: CallScreenNavigator) = launch(dispatchers.io) {
        navigator.close()
        widgetDriver?.close()
    }
}
