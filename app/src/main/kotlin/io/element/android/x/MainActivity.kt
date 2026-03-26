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
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.call.impl.ui.IncomingCallActivity
import io.element.android.features.lockscreen.api.LockScreenEntryPoint
import io.element.android.features.lockscreen.api.LockScreenLockState
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.features.lockscreen.api.handleSecureFlag
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.designsystem.utils.snackbar.LocalSnackbarDispatcher
import io.element.android.services.analytics.compose.LocalAnalyticsService
import io.element.android.x.di.AppBindings
import io.element.android.x.intent.SafeUriHandler
import kotlinx.coroutines.launch
import timber.log.Timber

private val loggerTag = LoggerTag("MainActivity")

/**
 * ElementX 应用的主 Activity 类。
 *
 * 继承自 NodeActivity，作为应用导航结构的宿主 Activity。
 * 负责以下核心功能：
 * - 使用 Jetpack Compose 设置应用主界面内容，应用 Element 主题
 * - 管理锁屏状态，处理应用锁定/解锁流程
 * - 处理来自外部的 Intent（深链接、通知点击等）
 * - 托管 MainNode 作为应用导航树的根节点
 *
 * 与 AppBindings 协作获取各种应用服务，
 * 包括偏好设置存储、分析服务、企业服务等。
 */
class MainActivity : NodeActivity() {
    private lateinit var mainNode: MainNode
    private lateinit var appBindings: AppBindings

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(loggerTag.value).w("onCreate, with savedInstanceState: ${savedInstanceState != null}")
        installSplashScreen()
        super.onCreate(savedInstanceState)
        appBindings = bindings()
        appBindings.foregroundIncomingCallObserver().start()
        setupLockManagement(appBindings.lockScreenService(), appBindings.lockScreenEntryPoint())
        enableEdgeToEdge()
        setContent {
            SplashScreenApp(appBindings)
        }
    }

    /**
     * 应用启动入口，包含启动页和主内容的切换逻辑
     */
    @Composable
    private fun SplashScreenApp(appBindings: AppBindings) {
        var showSplashScreen by remember { mutableStateOf(true) }

        if (showSplashScreen) {
            SplashScreenContent(
                modifier = Modifier.fillMaxSize(),
            )
            // 延迟 2 秒后隐藏启动页，进入主应用
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSplashScreen = false
            }
        } else {
            MainContent(appBindings)
        }
    }

    @Composable
    private fun MainContent(appBindings: AppBindings) {
        val migrationState = appBindings.migrationEntryPoint().present()
        val hasRingingCall by appBindings.appForegroundStateService().hasRingingCall.collectAsState()
        val colors by remember {
            appBindings.enterpriseService().semanticColorsFlow(sessionId = null)
        }.collectAsState(SemanticColorsLightDark.default)
        var hasLaunchedIncomingCallFallback by remember { mutableStateOf(false) }

        LaunchedEffect(hasRingingCall) {
            if (!hasRingingCall) {
                hasLaunchedIncomingCallFallback = false
            } else if (!hasLaunchedIncomingCallFallback) {
                hasLaunchedIncomingCallFallback = true
                startActivity(Intent(this@MainActivity, IncomingCallActivity::class.java))
            }
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
                }
            }
        }
    }

    @Composable
    private fun MainNodeHost() {
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

    /**
     * 在以下情况会被调用：
     * - 点击应用图标时（如果应用已在运行）；
     * - 点击通知时；
     * - 点击深链接时；
     * - 应用进入后台时（<- 这有点奇怪）
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.tag(loggerTag.value).w("onNewIntent")
        // 如果 mainNode 尚未初始化，保留 intent 以便稍后处理。
        // 这种情况可能发生在 Activity 被系统杀死时。方法调用顺序如下：
        // onCreate(savedInstanceState=true) -> onNewIntent -> onResume -> onMainNodeInit
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
