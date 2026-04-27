/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.plugin.NodeReadyObserver
import io.element.android.features.call.api.CallType
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.call.impl.ui.IncomingCallActivity
import io.element.android.features.call.impl.ui.IncomingCallOverlayCall
import io.element.android.features.call.impl.ui.IncomingCallOverlayHost
import io.element.android.features.call.impl.ui.IncomingCallOverlayState
import io.element.android.features.call.impl.ui.incomingCallAvatarId
import io.element.android.features.call.impl.ui.incomingCallAvatarName
import io.element.android.features.call.impl.ui.incomingCallSubtitle
import io.element.android.features.call.impl.ui.incomingCallTitle
import io.element.android.features.call.impl.utils.CallState
import io.element.android.features.lockscreen.api.LockScreenEntryPoint
import io.element.android.features.lockscreen.api.LockScreenLockState
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.features.lockscreen.api.handleSecureFlag
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.designsystem.utils.snackbar.LocalSnackbarDispatcher
import io.element.android.services.analytics.compose.LocalAnalyticsService
import io.element.android.x.di.AppBindings
import io.element.android.x.intent.SafeUriHandler
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber

private val loggerTag = LoggerTag("MainActivity")
private const val startupTraceTag = "StartupTrace"
private const val incomingCallTraceTag = "IncomingCallTrace"

/**
 * ElementX 应用主 Activity。
 */
class MainActivity : NodeActivity() {
    private lateinit var mainNode: MainNode
    private lateinit var appBindings: AppBindings

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(loggerTag.value).w("onCreate, with savedInstanceState: ${savedInstanceState != null}")
        installSplashScreen()
        super.onCreate(savedInstanceState)
        appBindings = bindings()
        Timber.tag(incomingCallTraceTag).i("MainActivity.onCreate start foreground incoming call observer")
        appBindings.foregroundIncomingCallObserver().start()
        setupLockManagement(appBindings.lockScreenService(), appBindings.lockScreenEntryPoint())
        enableEdgeToEdge()
        setContent {
            SplashScreenApp(appBindings)
        }
    }

    /**
     * 应用启动入口，负责在自定义启动页和真实业务界面之间切换。
     */
    @Composable
    private fun SplashScreenApp(appBindings: AppBindings) {
        var showSplashScreen by remember { mutableStateOf(true) }

        // 这里专门记录自定义启动页何时退出，便于和后续导航日志对齐，判断空白是否发生在 splash 之后。
        LaunchedEffect(showSplashScreen) {
            Timber.tag(startupTraceTag).i("MainActivity.SplashScreenApp stage changed: showSplashScreen=$showSplashScreen")
        }

        if (showSplashScreen) {
            SplashScreenContent(
                modifier = Modifier.fillMaxSize(),
            )
            LaunchedEffect(Unit) {
                Timber.tag(startupTraceTag).i("MainActivity custom splash visible, start fixed 2000ms delay")
                kotlinx.coroutines.delay(2000)
                Timber.tag(startupTraceTag).i("MainActivity custom splash delay finished, switch to MainContent")
                showSplashScreen = false
            }
        } else {
            MainContent(appBindings)
        }
    }

    @Composable
    private fun MainContent(appBindings: AppBindings) {
        val migrationState = appBindings.migrationEntryPoint().present()
        val activeCall by appBindings.activeCallManager().activeCall.collectAsState()
        val ringingCalls by appBindings.activeCallManager().ringingCalls.collectAsState()
        val colors by remember {
            appBindings.enterpriseService().semanticColorsFlow(sessionId = null)
        }.collectAsState(SemanticColorsLightDark.default)
        val incomingCallOverlayState = remember(ringingCalls) {
            IncomingCallOverlayState(
                calls = ringingCalls.map { notificationData ->
                    IncomingCallOverlayCall(
                        id = notificationData.eventId.value,
                        title = notificationData.incomingCallTitle(),
                        subtitle = notificationData.incomingCallSubtitle(),
                        avatarData = AvatarData(
                            id = notificationData.incomingCallAvatarId(),
                            name = notificationData.incomingCallAvatarName(),
                            url = notificationData.avatarUrl,
                            size = AvatarSize.RoomDetailsHeader,
                        ),
                        avatarType = if (notificationData.isDm) AvatarType.User else AvatarType.Room(),
                        onAnswerClick = {
                            appBindings.elementCallEntryPoint().startCall(
                                CallType.RoomCall(
                                    sessionId = notificationData.sessionId,
                                    roomId = notificationData.roomId,
                                )
                            )
                        },
                        onDeclineClick = {
                            lifecycleScope.launch {
                                appBindings.activeCallManager().hangUpCall(
                                    callType = CallType.RoomCall(
                                        sessionId = notificationData.sessionId,
                                        roomId = notificationData.roomId,
                                    ),
                                    notificationData = notificationData,
                                )
                            }
                        },
                    )
                }.toImmutableList(),
            )
        }
        var launchedIncomingCallEventId by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(incomingCallOverlayState.calls) {
            Timber.tag(incomingCallTraceTag).w(
                "MainActivity overlay mapped callCount=%s items=%s",
                incomingCallOverlayState.calls.size,
                incomingCallOverlayState.calls.joinToString { call ->
                    "id=${call.id},title=${call.title},subtitle=${call.subtitle}"
                },
            )
        }

        LaunchedEffect(ringingCalls, activeCall) {
            val firstRingingCall = ringingCalls.firstOrNull()
            val isInCall = activeCall?.callState is CallState.InCall
            Timber.tag(incomingCallTraceTag).w(
                "MainActivity observed ringingCount=%s, firstEventId=%s, fallbackEventId=%s, isInCall=%s",
                ringingCalls.size,
                firstRingingCall?.eventId,
                launchedIncomingCallEventId,
                isInCall,
            )
            if (firstRingingCall == null) {
                launchedIncomingCallEventId = null
            } else if (isInCall) {
                Timber.tag(incomingCallTraceTag).i(
                    "MainActivity skipping IncomingCallActivity fallback because call is already active currentCall=%s",
                    activeCall,
                )
            } else if (launchedIncomingCallEventId == null) {
                launchedIncomingCallEventId = firstRingingCall.eventId.value
                Timber.tag(incomingCallTraceTag).w("MainActivity launching IncomingCallActivity fallback")
                startActivity(
                    Intent(this@MainActivity, IncomingCallActivity::class.java).apply {
                        putExtra(IncomingCallActivity.EXTRA_NOTIFICATION_DATA, firstRingingCall)
                    }
                )
            }
        }

        // 这里用于区分“还卡在迁移页”还是“已经进入导航宿主但页面仍然空白”。
        LaunchedEffect(migrationState.migrationAction.isSuccess()) {
            Timber.tag(startupTraceTag).i(
                "MainActivity.MainContent migration ready=%s action=%s",
                migrationState.migrationAction.isSuccess(),
                migrationState.migrationAction,
            )
        }

        ElementThemeApp(
            appPreferencesStore = appBindings.preferencesStore(),
            compoundLight = colors.light,
            compoundDark = colors.dark,
            buildMeta = appBindings.buildMeta()
        ) {
            CompositionLocalProvider(
                LocalSnackbarDispatcher provides appBindings.snackbarDispatcher(),
                LocalUriHandler provides SafeUriHandler(this),
                LocalAnalyticsService provides appBindings.analyticsService(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ElementTheme.colors.bgCanvasDefault),
                ) {
                    if (migrationState.migrationAction.isSuccess()) {
                        MainNodeHost()
                    } else {
                        appBindings.migrationEntryPoint().Render(
                            state = migrationState,
                            modifier = Modifier,
                        )
                    }
                    /**
                     * 顶部来电列表只挂在 MainActivity 上层，避免把页面过滤逻辑塞进组件内部。
                     * 这样既能满足产品只在指定 Activity 显示的要求，也方便后续继续排查展示问题。
                     */
                    IncomingCallOverlayHost(
                        state = incomingCallOverlayState,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    @Composable
    private fun MainNodeHost() {
        LaunchedEffect(Unit) {
            Timber.tag(startupTraceTag).i("MainActivity.MainNodeHost composed")
        }
        NodeHost(integrationPoint = appyxV1IntegrationPoint) {
            MainNode(
                it,
                plugins = listOf(
                    object : NodeReadyObserver<MainNode> {
                        override fun init(node: MainNode) {
                            Timber.tag(loggerTag.value).w("onMainNodeInit")
                            mainNode = node
                            mainNode.handleIntent(intent)
                        }
                    }
                ),
                context = applicationContext
            )
        }
    }

    private fun setupLockManagement(
        lockScreenService: LockScreenService,
        lockScreenEntryPoint: LockScreenEntryPoint
    ) {
        lockScreenService.handleSecureFlag(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                lockScreenService.lockState.collect { state ->
                    if (state == LockScreenLockState.Locked) {
                        startActivity(lockScreenEntryPoint.pinUnlockIntent(this@MainActivity))
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.tag(loggerTag.value).w("onNewIntent")
        if (::mainNode.isInitialized) {
            mainNode.handleIntent(intent)
        } else {
            setIntent(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(loggerTag.value).w("onPause")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(loggerTag.value).w("onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(loggerTag.value).w("onDestroy")
    }
}
