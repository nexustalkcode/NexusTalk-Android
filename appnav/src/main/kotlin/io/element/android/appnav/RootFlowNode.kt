/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav

import android.content.Intent
import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.JoinedRoom
import io.element.android.annotations.ContributesNode
import io.element.android.appnav.di.MatrixSessionCache
import io.element.android.appnav.intent.IntentResolver
import io.element.android.appnav.intent.ResolvedIntent
import io.element.android.appnav.room.RoomFlowNode
import io.element.android.appnav.root.RootNavStateFlowFactory
import io.element.android.appnav.root.RootPresenter
import io.element.android.appnav.root.RootView
import io.element.android.features.announcement.api.AnnouncementService
import io.element.android.features.login.api.LoginParams
import io.element.android.features.login.api.accesscontrol.AccountProviderAccessControl
import io.element.android.features.rageshake.api.bugreport.BugReportEntryPoint
import io.element.android.features.signedout.api.SignedOutEntryPoint
import io.element.android.libraries.accountselect.api.AccountSelectEntryPoint
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.appyx.rememberDelegateTransitionHandler
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.waitForChildAttached
import io.element.android.libraries.core.uri.ensureProtocol
import io.element.android.libraries.deeplink.api.DeeplinkData
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.asEventId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.oidc.api.OidcAction
import io.element.android.libraries.oidc.api.OidcActionFlow
import io.element.android.libraries.sessionstorage.api.LoggedInState
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.libraries.ui.common.nodes.emptyNode
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import io.element.android.services.appnavstate.api.ROOM_OPENED_FROM_NOTIFICATION
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

private const val startupTraceTag = "StartupTrace"
private const val permalinkDebugTag = "PermalinkDebug"

/**
 * RootFlowNode 是应用导航体系中的根节点，负责管理整个应用的生命周期状态和全局导航流程。
 *
 * 作为应用架构的顶层节点，RootFlowNode 承担以下核心职责：
 *
 * 1. **状态管理**：通过 [RootNavStateFlowFactory] 观察应用的登录状态变化，
 *    包括已登录、已注销、未登录三种状态，并根据状态自动切换相应的导航流程。
 *
 * 2. **会话恢复**：在应用启动时尝试恢复之前的会话信息，
 *    确保用户无需重复登录即可继续使用应用。
 *
 * 3. **导航路由**：作为导航中枢，根据不同的导航目标（NavTarget）
 *    创建和管理相应的子节点，包括登录流程、已登录流程、已注销流程等。
 *
 * 4. **深度链接处理**：解析并处理来自外部的 Intent，
 *    包括深度链接、登录链接、分享内容等，实现从外部无缝跳转到应用内特定页面。
 *
 * 5. **分析集成**：集成分析服务以追踪应用启动和使用情况，
 *    包括冷启动追踪和通知打开追踪等。
 *
 * @property buildContext 构建上下文，包含保存状态等构建信息
 * @property plugins 插件列表，用于依赖注入和功能扩展
 */
@ContributesNode(AppScope::class)
@AssistedInject
class RootFlowNode(
    @Assisted val buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val sessionStore: SessionStore,
    private val accountProviderAccessControl: AccountProviderAccessControl,
    private val navStateFlowFactory: RootNavStateFlowFactory,
    private val matrixSessionCache: MatrixSessionCache,
    private val presenter: RootPresenter,
    private val bugReportEntryPoint: BugReportEntryPoint,
    private val signedOutEntryPoint: SignedOutEntryPoint,
    private val accountSelectEntryPoint: AccountSelectEntryPoint,
    private val intentResolver: IntentResolver,
    private val oidcActionFlow: OidcActionFlow,
    private val featureFlagService: FeatureFlagService,
    private val announcementService: AnnouncementService,
    private val analyticsService: AnalyticsService,
    private val analyticsColdStartWatcher: AnalyticsColdStartWatcher,
) : BaseFlowNode<RootFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.SplashScreen,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 节点构建完成后的初始化操作。
     *
     * 在此阶段执行以下初始化任务：
     * 1. 启动分析冷启动观察器，开始追踪应用启动指标
     * 2. 从保存状态中恢复矩阵会话缓存，确保会话信息在应用重建时得以保留
     * 3. 调用父类初始化逻辑
     * 4. 开始观察导航状态变化，以便根据登录状态自动切换流程
     */
    override fun onBuilt() {
        // 这是应用级导航开始接管启动流程的时间锚点，可用于判断空白是否发生在登录态判定之前。
        Timber.tag(startupTraceTag).i("RootFlowNode.onBuilt")
        analyticsColdStartWatcher.start()
        matrixSessionCache.restoreWithSavedState(buildContext.savedStateMap)
        super.onBuilt()
        observeNavState()
    }

    /**
     * 保存实例状态。
     *
     * 在应用可能被系统销毁前（如配置更改或内存压力），将关键状态持久化：
     * 1. 调用父类保存逻辑，保存基础状态
     * 2. 将矩阵会话缓存保存到状态映射中，确保会话信息不会丢失
     * 3. 将导航状态流工厂的当前状态保存起来，以便重建时恢复正确的导航位置
     *
     * @param state 可变保存状态映射，用于存储需要持久化的状态数据
     */
    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        matrixSessionCache.saveIntoSavedState(state)
        navStateFlowFactory.saveIntoSavedState(state)
    }

    /**
     * 观察导航状态变化并自动切换到相应的流程。
     *
     * 该方法创建一个导航状态流，持续监听应用登录状态的变化。
     * 根据不同的登录状态，触发相应的导航切换：
     *
     * - [LoggedInState.LoggedIn] 且令牌有效：尝试恢复最新会话并切换到已登录流程
     * - [LoggedInState.LoggedIn] 但令牌无效：切换到已注销流程，要求用户重新认证
     * - [LoggedInState.NotLoggedIn]：切换到未登录流程，显示登录界面
     *
     * 使用 [distinctUntilChanged] 确保仅在状态实际变化时才处理，
     * 并通过 [launchIn] 将收集操作绑定到生命周期作用域。
     */
    private fun observeNavState() {
        navStateFlowFactory.create(buildContext.savedStateMap).distinctUntilChanged().onEach { navState ->
            Timber.v("navState=$navState")
            Timber.tag(startupTraceTag).i("RootFlowNode.observeNavState loggedInState=%s cacheIndex=%s", navState.loggedInState, navState.cacheIndex)
            when (navState.loggedInState) {
                is LoggedInState.LoggedIn -> {
                    if (navState.loggedInState.isTokenValid) {
                        tryToRestoreLatestSession(
                            onSuccess = { sessionId -> switchToLoggedInFlow(sessionId, navState.cacheIndex) },
                            onFailure = { switchToNotLoggedInFlow(null) }
                        )
                    } else {
                        switchToSignedOutFlow(SessionId(navState.loggedInState.sessionId))
                    }
                }
                LoggedInState.NotLoggedIn -> {
                    switchToNotLoggedInFlow(null)
                }
            }
        }.launchIn(lifecycleScope)
    }

    /**
     * 切换到已登录流程。
     *
     * 当用户成功登录或会话恢复成功后调用此方法，
     * 将导航栈根节点更新为已登录流程节点。
     *
     * @param sessionId 已恢复的会话ID
     * @param navId 导航状态索引，用于恢复之前的导航位置
     */
    private fun switchToLoggedInFlow(sessionId: SessionId, navId: Int) {
        Timber.tag(startupTraceTag).i("RootFlowNode.switchToLoggedInFlow sessionId=%s navId=%s", sessionId, navId)
        backstack.safeRoot(NavTarget.LoggedInFlow(sessionId, navId))
    }

    /**
     * 切换到未登录流程。
     *
     * 当用户注销或需要重新登录时调用此方法。
     * 会清除所有已缓存的会话信息，并将导航栈根节点更新为未登录流程节点。
     *
     * @param params 可选的登录参数，用于预填充登录界面（如处理登录链接时）
     */
    private fun switchToNotLoggedInFlow(params: LoginParams?) {
        Timber.tag(startupTraceTag).i("RootFlowNode.switchToNotLoggedInFlow hasParams=%s", params != null)
        matrixSessionCache.removeAll()
        backstack.safeRoot(NavTarget.NotLoggedInFlow(params))
    }

    /**
     * 切换到已注销流程。
     *
     * 当用户会话的令牌失效（如令牌过期或被撤销）时调用此方法，
     * 将导航栈根节点更新为已注销流程节点，要求用户重新认证。
     *
     * @param sessionId 已失效的会话ID，用于显示适当的注销信息
     */
    private fun switchToSignedOutFlow(sessionId: SessionId) {
        Timber.tag(startupTraceTag).i("RootFlowNode.switchToSignedOutFlow sessionId=%s", sessionId)
        backstack.safeRoot(NavTarget.SignedOutFlow(sessionId))
    }

    /**
     * 尝试恢复指定会话。
     *
     * 从会话缓存中恢复给定会话ID对应的矩阵客户端。
     * 恢复成功时调用 [onSuccess] 回调，失败时调用 [onFailure] 回调。
     *
     * @param sessionId 需要恢复的会话ID
     * @param onFailure 恢复失败时的回调
     * @param onSuccess 恢复成功时的回调，参数为已恢复的会话ID
     */
    private suspend fun restoreSessionIfNeeded(
        sessionId: SessionId,
        onFailure: () -> Unit,
        onSuccess: (SessionId) -> Unit,
    ) {
        // 如果 splash 结束后仍然空白，这里就是首个需要确认是否卡住的异步会话恢复关口。
        Timber.tag(startupTraceTag).i("RootFlowNode.restoreSessionIfNeeded start sessionId=%s", sessionId)
        matrixSessionCache.getOrRestore(sessionId).onSuccess {
            Timber.v("Succeed to restore session $sessionId")
            Timber.tag(startupTraceTag).i("RootFlowNode.restoreSessionIfNeeded success sessionId=%s", sessionId)
            onSuccess(sessionId)
        }.onFailure {
            Timber.e(it, "Failed to restore session $sessionId")
            Timber.tag(startupTraceTag).e(it, "RootFlowNode.restoreSessionIfNeeded failure sessionId=%s", sessionId)
            onFailure()
        }
    }

    /**
     * 尝试恢复最新会话。
     *
     * 从会话存储中获取最近使用的会话ID，
     * 如果存在会话则尝试恢复，否则触发失败回调。
     * 这是应用启动时恢复用户会话的主要入口点。
     *
     * @param onSuccess 恢复成功时的回调
     * @param onFailure 无会话或恢复失败时的回调
     */
    private suspend fun tryToRestoreLatestSession(
        onSuccess: (SessionId) -> Unit, onFailure: () -> Unit
    ) {
        val latestSessionId = sessionStore.getLatestSessionId()
        if (latestSessionId == null) {
            Timber.tag(startupTraceTag).w("RootFlowNode.tryToRestoreLatestSession no latest session")
            onFailure()
            return
        }
        Timber.tag(startupTraceTag).i("RootFlowNode.tryToRestoreLatestSession latestSessionId=%s", latestSessionId)
        restoreSessionIfNeeded(latestSessionId, onFailure, onSuccess)
    }

    /**
     * 打开错误报告界面。
     *
     * 将 BugReport 导航目标压入导航栈，显示错误报告界面。
     */
    private fun onOpenBugReport() {
        backstack.push(NavTarget.BugReport)
    }

    /**
     * 渲染根节点视图。
     *
     * 此方法是应用的主要UI入口点，负责：
     * 1. 从 [RootPresenter] 获取根节点状态
     * 2. 配置导航过渡动画处理器，根据导航目标类型选择滑入滑出或淡入淡出效果
     * 3. 渲染 [BackstackView] 管理子节点的内容显示
     * 4. 渲染全局公告服务内容
     *
     * @param modifier 应用于根视图的修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RootView(
            state = state,
            modifier = modifier,
            onOpenBugReport = this::onOpenBugReport,
        ) {
            val backstackSlider = rememberBackstackSlider<NavTarget>(
                transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
            )
            val backstackFader = rememberBackstackFader<NavTarget>(
                transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
            )
            val transitionHandler = rememberDelegateTransitionHandler<NavTarget, BackStack.State> { navTarget ->
                when (navTarget) {
                    is NavTarget.SplashScreen,
                    is NavTarget.LoggedInFlow -> backstackFader
                    else -> backstackSlider
                }
            }
            BackstackView(transitionHandler = transitionHandler)
            announcementService.Render(Modifier)
        }
    }

    /**
     * 导航目标密封接口。
     *
     * 定义了根节点可能导航到的所有目标状态，每个目标对应应用中的一个主要流程或界面。
     * 这些目标会被持久化保存，以便在应用重建时恢复导航状态。
     */
    sealed interface NavTarget : Parcelable {
        /** 启动屏幕，应用启动时显示的初始界面 */
        @Parcelize data object SplashScreen : NavTarget

        /**
         * 账户选择界面。
         *
         * 当应用存在多个账户时，允许用户选择要使用的账户。
         * @param currentSessionId 当前选中的会话ID
         * @param intent 可选的分享意图，账户选择后需要处理
         * @param permalinkData 可选的永久链接数据，账户选择后需要导航
         */
        @Parcelize data class AccountSelect(
            val currentSessionId: SessionId,
            val intent: Intent?,
            val permalinkData: PermalinkData?,
        ) : NavTarget

        /**
         * 未登录流程。
         *
         * 用户未登录时显示的流程，包含登录、注册等功能。
         * @param params 可选的登录参数，用于预填充登录界面
         */
        @Parcelize data class NotLoggedInFlow(
            val params: LoginParams?
        ) : NavTarget

        /**
         * 已登录流程。
         *
         * 用户成功登录后显示的主流程，包含房间列表、设置等功能。
         * @param sessionId 当前登录用户的会话ID
         * @param navId 导航状态索引，用于恢复之前的导航位置
         */
        @Parcelize data class LoggedInFlow(
            val sessionId: SessionId, val navId: Int
        ) : NavTarget

        /**
         * 已注销流程。
         *
         * 用户会话失效后显示的流程，要求用户重新认证。
         * @param sessionId 已失效的会话ID
         */
        @Parcelize data class SignedOutFlow(
            val sessionId: SessionId
        ) : NavTarget

        /** 错误报告界面，用于收集和提交应用问题反馈 */
        @Parcelize data object BugReport : NavTarget
    }

    /**
     * 解析导航目标并创建相应的节点。
     *
     * 根据传入的导航目标类型，创建并返回对应的子节点。
     * 这是导航系统的核心路由逻辑，负责将抽象的导航目标转换为具体的UI节点。
     *
     * @param navTarget 需要解析的导航目标
     * @param buildContext 构建上下文，用于创建节点
     * @return 创建完成的节点实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        Timber.tag(startupTraceTag).i("RootFlowNode.resolve navTarget=%s", navTarget)
        return when (navTarget) {
            // 已登录流程：创建登录用户的主流程节点
            is NavTarget.LoggedInFlow -> {
                val matrixClient = matrixSessionCache.getOrNull(navTarget.sessionId)
                    ?: return emptyNode(buildContext).also {
                        Timber.w("Couldn't find any session, go through SplashScreen")
                        Timber.tag(startupTraceTag).w("RootFlowNode.resolve LoggedInFlow missing sessionId=%s, return empty node", navTarget.sessionId)
                    }
                val inputs = LoggedInAppScopeFlowNode.Inputs(matrixClient)
                val callback = object : LoggedInAppScopeFlowNode.Callback {
                    override fun navigateToBugReport() {
                        backstack.push(NavTarget.BugReport)
                    }

                    override fun navigateToAddAccount() {
                        backstack.push(NavTarget.NotLoggedInFlow(null))
                    }
                }
                createNode<LoggedInAppScopeFlowNode>(buildContext, plugins = listOf(inputs, callback))
            }
            // 未登录流程：创建登录/注册界面节点
            is NavTarget.NotLoggedInFlow -> {
                val callback = object : NotLoggedInFlowNode.Callback {
                    override fun navigateToBugReport() {
                        backstack.push(NavTarget.BugReport)
                    }

                    override fun onDone() {
                        backstack.pop()
                    }
                }
                val params = NotLoggedInFlowNode.Params(
                    loginParams = navTarget.params,
                )
                createNode<NotLoggedInFlowNode>(buildContext, plugins = listOf(params, callback))
            }
            // 已注销流程：创建会话失效提示界面节点
            is NavTarget.SignedOutFlow -> {
                signedOutEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = SignedOutEntryPoint.Params(
                        sessionId = navTarget.sessionId,
                    ),
                )
            }
            // 启动屏幕：创建空节点
            NavTarget.SplashScreen -> emptyNode(buildContext).also {
                Timber.tag(startupTraceTag).i("RootFlowNode.resolve SplashScreen")
            }
            // 错误报告界面：创建错误报告节点
            NavTarget.BugReport -> {
                val callback = object : BugReportEntryPoint.Callback {
                    override fun onDone() {
                        backstack.pop()
                    }
                }
                bugReportEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }
            // 账户选择界面：创建账户选择节点
            is NavTarget.AccountSelect -> {
                val callback: AccountSelectEntryPoint.Callback = object : AccountSelectEntryPoint.Callback {
                    override fun onAccountSelected(sessionId: SessionId) {
                        lifecycleScope.launch {
                            if (sessionId == navTarget.currentSessionId) {
                                // Ensure that the account selection Node is removed from the backstack
                                // Do not pop when the account is changed to avoid a UI flicker.
                                backstack.pop()
                            }
                            attachSession(sessionId).apply {
                                if (navTarget.intent != null) {
                                    attachIncomingShare(navTarget.intent)
                                } else if (navTarget.permalinkData != null) {
                                    attachPermalinkData(navTarget.permalinkData)
                                }
                            }
                        }
                    }

                    override fun onCancel() {
                        backstack.pop()
                    }
                }
                accountSelectEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }
        }
    }

    /**
     * 处理外部传入的 Intent。
     *
     * 这是应用处理外部启动和深度链接的主要入口点。
     * 解析 Intent 并根据解析结果类型执行相应的导航操作：
     *
     * - [ResolvedIntent.Navigation]: 导航到深度链接指定的位置
     * - [ResolvedIntent.Login]: 处理登录链接
     * - [ResolvedIntent.Oidc]: 处理 OpenID Connect 认证流程
     * - [ResolvedIntent.Permalink]: 导航到永久链接指定的位置
     * - [ResolvedIntent.IncomingShare]: 处理从其他应用分享的内容
     *
     * 如果无法解析 Intent，则直接返回不做处理。
     *
     * @param intent 需要处理的外部 Intent
     */
    suspend fun handleIntent(intent: Intent) {
        // 这里记录 RootFlowNode 收到的原始 Intent 与解析结果，方便确认问题是卡在解析前还是导航前。
        Timber.tag(permalinkDebugTag).i(
            "RootFlowNode.handleIntent action=%s data=%s extras=%s",
            intent.action,
            intent.dataString,
            intent.extras,
        )
        val resolvedIntent = intentResolver.resolve(intent) ?: return
        Timber.tag(permalinkDebugTag).i("RootFlowNode.handleIntent resolvedIntent=%s", resolvedIntent)
        when (resolvedIntent) {
            is ResolvedIntent.Navigation -> {
                val openingRoomFromNotification = intent.getBooleanExtra(ROOM_OPENED_FROM_NOTIFICATION, false)
                if (openingRoomFromNotification && resolvedIntent.deeplinkData is DeeplinkData.Room) {
                    analyticsService.startLongRunningTransaction(AnalyticsLongRunningTransaction.NotificationToMessage)
                }
                navigateTo(resolvedIntent.deeplinkData)
            }
            is ResolvedIntent.Login -> onLoginLink(resolvedIntent.params)
            is ResolvedIntent.Oidc -> onOidcAction(resolvedIntent.oidcAction)
            is ResolvedIntent.Permalink -> navigateTo(resolvedIntent.permalinkData)
            is ResolvedIntent.IncomingShare -> onIncomingShare(resolvedIntent.intent)
        }
    }

    /**
     * 处理登录链接。
     *
     * 当用户点击登录链接时调用此方法。
     * 处理流程：
     * 1. 验证账户提供商是否被允许连接
     * 2. 检查是否存在已登录的会话
     * 3. 根据多账户开关和登录提示执行相应操作：
     *    - 如果有匹配的现有账户，切换到该账户
     *    - 如果多账户功能启用且无匹配账户，切换到最新使用的账户并打开登录界面
     *    - 如果多账户功能禁用，记录警告并忽略
     * 4. 如果没有现有会话，直接打开登录界面
     *
     * @param params 登录链接解析后的参数
     */
    private suspend fun onLoginLink(params: LoginParams) {
        if (accountProviderAccessControl.isAllowedToConnectToAccountProvider(params.accountProvider.ensureProtocol())) {
            // Is there a session already?
            val sessions = sessionStore.getAllSessions()
            if (sessions.isNotEmpty()) {
                if (featureFlagService.isFeatureEnabled(FeatureFlags.MultiAccount)) {
                    val loginHintMatrixId = params.loginHint?.removePrefix("mxid:")
                    val existingAccount = sessions.find { it.userId == loginHintMatrixId }
                    if (existingAccount != null) {
                        // We have an existing account matching the login hint, ensure this is the current session
                        sessionStore.setLatestSession(existingAccount.userId)
                    } else {
                        val latestSessionId = sessions.maxBy { it.lastUsageIndex }.userId
                        attachSession(SessionId(latestSessionId))
                        backstack.push(NavTarget.NotLoggedInFlow(params))
                    }
                } else {
                    Timber.w("Login link ignored, multi account is disabled")
                }
            } else {
                switchToNotLoggedInFlow(params)
            }
        } else {
            Timber.w("Login link ignored, we are not allowed to connect to the homeserver")
        }
    }

    /**
     * 处理从其他应用分享的内容。
     *
     * 当用户从其他应用分享内容到本应用时调用此方法。
     * 处理流程：
     * 1. 检查是否存在已登录的会话
     * 2. 如果没有会话，打开登录界面
     * 3. 如果存在会话，恢复该会话
     * 4. 根据会话数量决定后续操作：
     *    - 多个会话：打开账户选择界面让用户选择
     *    - 单个会话：直接附加分享内容
     *
     * @param intent 包含分享内容的 Intent
     */
    private suspend fun onIncomingShare(intent: Intent) {
        // Is there a session already?
        val latestSessionId = sessionStore.getLatestSessionId()
        if (latestSessionId == null) {
            // No session, open login
            switchToNotLoggedInFlow(null)
        } else {
            // wait for the current session to be restored
            val loggedInFlowNode = attachSession(latestSessionId)
            if (sessionStore.numberOfSessions() > 1) {
                // Several accounts, let the user choose which one to use
                backstack.push(
                    NavTarget.AccountSelect(
                        currentSessionId = latestSessionId,
                        intent = intent,
                        permalinkData = null,
                    )
                )
            } else {
                // Only one account, directly attach the incoming share node.
                loggedInFlowNode.attachIncomingShare(intent)
            }
        }
    }

    /**
     * 导航到永久链接指定的位置。
     *
     * 处理用户点击的 Element 永久链接（如 room/#/... 或 user/... 格式）。
     * 根据链接类型和会话情况执行相应的导航操作：
     * 1. 检查是否存在已登录的会话
     * 2. 如果没有会话，打开登录界面
     * 3. 如果存在会话，恢复该会话
     * 4. 根据链接类型导航到对应页面（房间、用户等）
     * 5. 多账户情况下可能需要先选择账户
     *
     * @param permalinkData 需要导航到的永久链接数据
     */
    private suspend fun navigateTo(permalinkData: PermalinkData) {
        Timber.d("Navigating to $permalinkData")
        // Is there a session already?
        val latestSessionId = sessionStore.getLatestSessionId()
        Timber.tag(permalinkDebugTag).i(
            "RootFlowNode.navigateToPermalink permalinkData=%s latestSessionId=%s sessionCount=%s",
            permalinkData,
            latestSessionId,
            sessionStore.numberOfSessions(),
        )
        if (latestSessionId == null) {
            // No session, open login
            switchToNotLoggedInFlow(null)
        } else {
            // wait for the current session to be restored
            val loggedInFlowNode = attachSession(latestSessionId)
            when (permalinkData) {
                is PermalinkData.FallbackLink -> Unit
                is PermalinkData.RoomEmailInviteLink -> Unit
                else -> {
                    if (sessionStore.numberOfSessions() > 1) {
                        // Several accounts, let the user choose which one to use
                        backstack.push(
                            NavTarget.AccountSelect(
                                currentSessionId = latestSessionId,
                                intent = null,
                                permalinkData = permalinkData,
                            )
                        )
                    } else {
                        // Only one account, directly attach the room or the user node.
                        loggedInFlowNode.attachPermalinkData(permalinkData)
                    }
                }
            }
        }
    }

    /**
     * 将永久链接数据附加到登录流程节点。
     *
     * 根据永久链接类型执行相应的导航操作：
     * - [PermalinkData.RoomLink]: 导航到指定房间，如果链接包含线程ID则聚焦到该线程
     * - [PermalinkData.UserLink]: 导航到指定用户的个人资料页面
     * - [PermalinkData.FallbackLink] 和 [PermalinkData.RoomEmailInviteLink]: 不做处理
     *
     * @receiver LoggedInFlowNode 登录流程节点
     * @param permalinkData 需要附加的永久链接数据
     */
    private suspend fun LoggedInFlowNode.attachPermalinkData(permalinkData: PermalinkData) {
        when (permalinkData) {
            is PermalinkData.FallbackLink -> Unit
            is PermalinkData.RoomEmailInviteLink -> Unit
            is PermalinkData.RoomLink -> {
                Timber.tag(permalinkDebugTag).i(
                    "RootFlowNode.attachPermalinkData roomIdOrAlias=%s eventId=%s threadId=%s via=%s",
                    permalinkData.roomIdOrAlias,
                    permalinkData.eventId,
                    permalinkData.threadId,
                    permalinkData.viaParameters,
                )
                // If there is a thread id, focus on it in the main timeline
                val focusedEventId = if (permalinkData.threadId != null) {
                    permalinkData.threadId?.asEventId()
                } else {
                    permalinkData.eventId
                }
                attachRoom(
                    roomIdOrAlias = permalinkData.roomIdOrAlias,
                    trigger = JoinedRoom.Trigger.MobilePermalink,
                    serverNames = permalinkData.viaParameters,
                    eventId = focusedEventId,
                    clearBackstack = true
                ).maybeAttachThread(permalinkData.threadId, permalinkData.eventId)
            }
            is PermalinkData.UserLink -> {
                Timber.tag(permalinkDebugTag).i("RootFlowNode.attachPermalinkData userId=%s", permalinkData.userId)
                attachUser(permalinkData.userId)
            }
        }
    }

    /**
     * 可选地附加线程上下文。
     *
     * 如果提供了线程ID，则将房间节点附加到指定的线程。
     * 这是房间链接处理的辅助方法，用于处理包含线程信息的链接。
     *
     * @receiver RoomFlowNode 房间流程节点
     * @param threadId 需要附加的线程ID，如果为null则不执行任何操作
     * @param focusedEventId 聚焦的事件ID，用于定位到线程中的特定消息
     */
    private suspend fun RoomFlowNode.maybeAttachThread(threadId: ThreadId?, focusedEventId: EventId?) {
        if (threadId != null) {
            attachThread(threadId, focusedEventId)
        }
    }

    /**
     * 导航到深度链接指定的位置。
     *
     * 处理来自外部的深度链接 Intent，根据深度链接类型导航到相应页面。
     * 深度链接通常是用户在浏览器或其他应用中点击 Element 链接后触发。
     *
     * 支持的深度链接类型：
     * - [DeeplinkData.Root]: 根链接，导航到应用主页面（房间列表）
     * - [DeeplinkData.Room]: 房间链接，导航到指定房间
     *
     * @param deeplinkData 深度链接解析后的数据
     */
    private suspend fun navigateTo(deeplinkData: DeeplinkData) {
        Timber.d("Navigating to $deeplinkData")
        attachSession(deeplinkData.sessionId).let { loggedInFlowNode ->
            when (deeplinkData) {
                is DeeplinkData.Root -> Unit // The room list will always be shown, observing FtueState
                is DeeplinkData.Room -> {
                    loggedInFlowNode.attachRoom(
                        roomIdOrAlias = deeplinkData.roomId.toRoomIdOrAlias(),
                        eventId = if (deeplinkData.threadId != null) deeplinkData.threadId?.asEventId() else deeplinkData.eventId,
                        clearBackstack = true,
                    ).maybeAttachThread(deeplinkData.threadId, deeplinkData.eventId)
                }
            }
        }
    }

    /**
     * 处理 OpenID Connect 认证操作。
     *
     * 将 OIDC 操作事件发送到 [OidcActionFlow]，
     * 由相应的观察者处理认证流程（如打开认证页面、完成认证等）。
     *
     * @param oidcAction 需要执行的 OIDC 操作
     */
    private fun onOidcAction(oidcAction: OidcAction) {
        oidcActionFlow.post(oidcAction)
    }

    /**
     * 附加会话到导航栈。
     *
     * 将指定会话设置为最新会话，然后等待对应的登录流程节点附加到导航栈后返回。
     * 这是会话激活和导航到登录用户主界面的核心方法。
     *
     * 处理流程：
     * 1. 将指定会话ID设置为当前最新会话
     * 2. 等待导航栈中出现匹配的 LoggedInAppScopeFlowNode
     * 3. 在该节点上调用 attachSession 完成附加
     *
     * @param sessionId 需要附加的会话ID
     * @return 附加完成的登录流程节点
     */
    private suspend fun attachSession(sessionId: SessionId): LoggedInFlowNode {
        // Ensure that the session is the latest one
        sessionStore.setLatestSession(sessionId.value)
        return waitForChildAttached<LoggedInAppScopeFlowNode, NavTarget> { navTarget ->
            navTarget is NavTarget.LoggedInFlow && navTarget.sessionId == sessionId
        }.attachSession()
    }
}

/**
 * 获取会话存储中的最新会话ID。
 *
 * 扩展 [SessionStore] 方法，从最新会话中提取用户ID并转换为 [SessionId]。
 * 如果没有会话或获取失败，返回 null。
 *
 * @receiver SessionStore 会话存储实例
 * @return 最新会话的 SessionId，如果不存在则返回 null
 */
private suspend fun SessionStore.getLatestSessionId() = getLatestSession()?.userId?.let(::SessionId)
