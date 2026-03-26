/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * LoggedInFlowNode - 登录后流程主导航节点
 *
 * 这是 Element X Android 应用的核心导航组件，负责管理用户登录后的主流程。
 * 使用 Appyx 导航框架的 BackStack 来管理页面导航，使用 PermanentNavModel 来保持永久显示的组件。
 *
 * 主要职责：
 * 1. 管理登录后各个功能模块的导航（首页、房间、设置、用户资料等）
 * 2. 处理来自外部的分享意图和验证请求
 * 3. 协调 FTUE（首次使用体验）流程
 * 4. 维护会话级别的状态和服务（同步、发送队列、企业服务等）
 *
 * 架构说明：
 * - 继承自 BaseFlowNode，使用 BackStack 管理可堆叠的页面
 * - 使用 PermanentNavModel 保持 LoggedInNode 永久显示（底部导航栏等）
 * - 通过 resolve 方法根据 NavTarget 创建对应的子节点
 */

package io.element.android.appnav

// ==================== Android 平台相关导入 ====================
import android.content.Intent
import android.os.Parcelable

// ==================== Compose UI 框架导入 ====================
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

// ==================== 生命周期管理导入 ====================
import androidx.lifecycle.lifecycleScope

// ==================== Appyx 导航框架导入 ====================
// 核心导航组件
import com.bumble.appyx.core.composable.PermanentChild
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin

// BackStack 导航模型和状态
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.State.STASHED
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.operation.BackStackOperation

// BackStack 操作方法
import com.bumble.appyx.navmodel.backstack.operation.Push
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.operation.singleTop

// ==================== 依赖注入框架导入 ====================
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject

// ==================== 应用功能模块导入 ====================
// 架构注解和组件
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.waitForChildAttached
import io.element.android.libraries.architecture.waitForNavTargetAttached
import io.element.android.libraries.ui.common.nodes.emptyNode

// AppNav 模块组件
import io.element.android.appnav.loggedin.LoggedInNode
import io.element.android.appnav.loggedin.MediaPreviewConfigMigration
import io.element.android.appnav.loggedin.SendQueues
import io.element.android.appnav.room.RoomFlowNode
import io.element.android.appnav.room.RoomNavigationTarget
import io.element.android.appnav.room.joined.JoinedRoomLoadedFlowNode

// 设计和主题组件
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher

// 功能模块入口点
import io.element.android.features.createroom.api.CreateRoomEntryPoint
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.enterprise.api.SessionEnterpriseService
import io.element.android.features.ftue.api.FtueEntryPoint
import io.element.android.features.ftue.api.state.FtueService
import io.element.android.features.ftue.api.state.FtueState
import io.element.android.features.ftue.impl.sessionverification.FtueSessionVerificationFlowNode
import io.element.android.features.home.api.HomeEntryPoint
import io.element.android.features.linknewdevice.api.LinkNewDeviceEntryPoint
import io.element.android.features.preferences.api.PreferencesEntryPoint
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.features.roomdirectory.api.RoomDirectoryEntryPoint
import io.element.android.features.securebackup.api.SecureBackupEntryPoint
import io.element.android.features.share.api.ShareEntryPoint
import io.element.android.features.startchat.api.StartChatEntryPoint
import io.element.android.features.userprofile.api.UserProfileEntryPoint
import io.element.android.features.verifysession.api.IncomingVerificationEntryPoint

// ==================== 库和工具类导入 ====================
// 构建元数据
import io.element.android.libraries.core.meta.BuildMeta

// 依赖注入作用域
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope

// Matrix 客户端库 - 核心类型
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.MAIN_SPACE
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.user.MatrixUser

// Matrix 客户端库 - 高级功能
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.verification.SessionVerificationServiceListener
import io.element.android.libraries.matrix.api.verification.VerificationRequest

// 首选项存储
import io.element.android.libraries.preferences.api.store.AppPreferencesStore

// 推送通知
import io.element.android.libraries.push.api.notifications.conversations.NotificationConversationService

// 网络监控
import io.element.android.features.networkmonitor.api.NetworkMonitor
import io.element.android.features.networkmonitor.api.NetworkStatus
import io.element.android.features.networkmonitor.api.ui.ConnectivityIndicatorContainer
import io.element.android.libraries.architecture.OverlayView
import io.element.android.libraries.architecture.overlay.operation.hide
import io.element.android.libraries.architecture.overlay.operation.show

// 分析服务
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.watchers.AnalyticsRoomListStateWatcher

// 应用导航状态
import io.element.android.services.appnavstate.api.AppNavigationStateService

// Kotlin 协程
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

// Parcelable 序列化
import kotlinx.parcelize.Parcelize

// 日志
import timber.log.Timber

// Java 时间工具
import java.time.Duration
import java.time.Instant
import java.util.Optional
import java.util.UUID

// Kotlin 时间扩展
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

// 分析事件别名
import im.vector.app.features.analytics.plan.JoinedRoom as JoinedRoomAnalyticsEvent

/**
 * LoggedInFlowNode - 登录后流程主导航节点
 *
 * 核心职责：
 * 1. 管理用户登录后的完整应用流程导航
 * 2. 协调各个功能模块的页面切换（房间、设置、用户资料等）
 * 3. 处理外部Intent（分享链接、验证请求等）
 * 4. 维护会话级别的服务状态
 *
 * 导航架构：
 * - BackStack：管理可堆叠的页面（房间详情、设置、创建房间等）
 * - PermanentNavModel：保持永久显示的组件（LoggedInNode包含底部导航栏）
 * - 通过NavTarget密封类定义所有可能的导航目标
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class LoggedInFlowNode(
    // ==================== 导航框架必需参数 ====================
    @Assisted buildContext: BuildContext,    // Appyx导航构建上下文，包含状态保存信息
    @Assisted plugins: List<Plugin>,         // 插件列表，用于扩展功能

    // ==================== 功能模块入口点 ====================
    private val homeEntryPoint: HomeEntryPoint,                  // 首页/房间列表入口
    private val preferencesEntryPoint: PreferencesEntryPoint,    // 设置页面入口
    private val startChatEntryPoint: StartChatEntryPoint,        // 开始聊天/创建房间入口
    private val roomDirectoryEntryPoint: RoomDirectoryEntryPoint,// 房间目录入口
    private val shareEntryPoint: ShareEntryPoint,                // 分享功能入口
    private val secureBackupEntryPoint: SecureBackupEntryPoint,  // 安全备份入口
    private val userProfileEntryPoint: UserProfileEntryPoint,    // 用户资料入口
    private val ftueEntryPoint: FtueEntryPoint,                  // 首次引导入口
    private val linkNewDeviceEntryPoint: LinkNewDeviceEntryPoint,// 链接新设备入口
    private val incomingVerificationEntryPoint: IncomingVerificationEntryPoint,// 验证请求入口
    private val createRoomEntryPoint: CreateRoomEntryPoint,      // 创建房间入口

    // ==================== 会话级别的服务和作用域 ====================
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,           // 会话级别的协程作用域
    private val matrixClient: MatrixClient,                      // Matrix客户端实例
    private val sendingQueue: SendQueues,                        // 消息发送队列
    private val ftueService: FtueService,                        // FTUE状态服务
    private val sessionEnterpriseService: SessionEnterpriseService,// 会话企业服务

    // ==================== 系统服务和监控 ====================
    private val appNavigationStateService: AppNavigationStateService,// 应用导航状态服务
    private val networkMonitor: NetworkMonitor,                   // 网络状态监控
    private val syncService: SyncService,                        // 同步服务
    private val notificationConversationService: NotificationConversationService,// 通知对话服务
    private val enterpriseService: EnterpriseService,            // 企业服务
    private val mediaPreviewConfigMigration: MediaPreviewConfigMigration,// 媒体预览配置迁移

    // ==================== 用户界面和偏好设置 ====================
    private val appPreferencesStore: AppPreferencesStore,        // 应用偏好设置存储
    private val buildMeta: BuildMeta,                            // 构建元数据
    snackbarDispatcher: SnackbarDispatcher,                      // Snackbar调度器（非private）

    // ==================== 分析服务 ====================
    private val analyticsService: AnalyticsService,              // 分析服务
    private val analyticsRoomListStateWatcher: AnalyticsRoomListStateWatcher,// 房间列表状态观察者
) : BaseFlowNode<LoggedInFlowNode.NavTarget>(
    // 初始化BackStack，初始元素为占位符
    backstack = BackStack(
        initialElement = NavTarget.Placeholder,
        savedStateMap = buildContext.savedStateMap,
    ),
    // 配置永久导航模型，保持LoggedInNode始终显示
    permanentNavModel = PermanentNavModel(
        navTargets = setOf(NavTarget.LoggedInPermanent),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    // ==================== 回调接口定义 ====================
    /**
     * LoggedInFlowNode 回调接口
     *
     * 用于将导航事件向上传递给父节点（通常是RootFlowNode）处理。
     * 实现了Plugin接口，可以被注入到子节点中。
     */
    interface Callback : Plugin {
        /**
         * 导航到Bug报告页面
         * 由外部（RootFlowNode）处理，因为需要访问更高级别的导航能力
         */
        fun navigateToBugReport()

        /**
         * 导航到添加账户页面
         * 由外部（RootFlowNode）处理，支持多账户管理
         */
        fun navigateToAddAccount()
    }

    // ==================== 内部属性 ====================
    private val callback: Callback = callback()  // 从插件中获取的回调实例
    private val loggedInFlowProcessor = LoggedInEventProcessor(  // 处理登录流程中的事件（如房间成员变化）
        snackbarDispatcher = snackbarDispatcher,
        roomMembershipObserver = matrixClient.roomMembershipObserver,
    )

    // ==================== 会话验证监听器 ====================
    /**
     * 会话验证请求监听器
     *
     * 职责：
     * 1. 接收来自其他设备的会话验证请求
     * 2. 确保在应用前台时显示验证界面
     * 3. 超时处理（2分钟超时）
     * 4. 等待首页UI就绪后再显示验证界面（避免UI层级问题）
     *
     * 注意：此监听器必须在后台线程中启动协程，因为Rust回调可能在后台线程执行
     */
    private val verificationListener = object : SessionVerificationServiceListener {
        override fun onIncomingSessionRequest(verificationRequest: VerificationRequest.Incoming) {
            // 在协程中处理，避免Rust后台线程导致Appyx节点状态与渲染不同步的问题
            lifecycleScope.launch {
                val receivedAt = Instant.now()

                // 等待应用进入前台后再显示验证请求
                appNavigationStateService.appNavigationState.first { it.isInForeground }

                // 计算接收验证请求后经过的时间
                val now = Instant.now()
                val elapsedTimeSinceReceived = Duration.between(receivedAt, now).toKotlinDuration()

                // 如果验证请求已超时（超过2分钟），则丢弃
                if (elapsedTimeSinceReceived > 2.minutes) {
                    Timber.w("Incoming verification request ${verificationRequest.details.flowId} discarded due to timeout.")
                    return@launch
                }

                // 等待首页UI就绪，确保验证界面能正确显示在首页上方
                // 使用5秒超时，避免无限等待
                withTimeout(5.seconds) {
                    backstack.elements.first { elements ->
                        elements.any { it.key.navTarget == NavTarget.Home }
                    }
                }

                // 使用singleTop操作，确保只有一个验证请求实例
                backstack.singleTop(NavTarget.IncomingVerificationRequest(verificationRequest))
            }
        }
    }

    /**
     * onBuilt - 节点构建完成时的初始化
     *
     * 这是Node生命周期的关键方法，用于：
     * 1. 初始化会话级别的服务
     * 2. 设置各种监听器和观察者
     * 3. 根据FTUE状态导航到首页或引导页
     * 4. 启动消息发送队列
     *
     * 初始化顺序：
     * - 首先调用super.onBuilt()确保父类初始化完成
     * - 然后并行执行多个初始化任务
     */
    override fun onBuilt() {
        super.onBuilt()

        // 异步初始化企业服务（不影响其他初始化流程）
        lifecycleScope.launch {
            sessionEnterpriseService.init()
        }

        // 订阅生命周期事件，初始化各种服务和监听器
        lifecycle.subscribe(
            /**
             * onCreate - 节点创建时初始化
             *
             * 执行以下初始化任务：
             * 1. 启动分析服务的房间列表状态观察者
             * 2. 向导航状态服务注册会话和空间
             * 3. 启动登录流程事件处理器
             * 4. 设置会话验证监听器
             * 5. 执行媒体预览配置迁移
             * 6. 预获取最大文件上传大小
             * 7. 启动分析事务
             * 8. 观察FTUE状态并导航到相应页面
             */
            onCreate = {
                // 启动分析服务的房间列表状态观察
                analyticsRoomListStateWatcher.start()

                // 向导航状态服务注册当前会话
                appNavigationStateService.onNavigateToSession(id, matrixClient.sessionId)

                // TODO: 当前不支持Space功能，直接导航到主空间
                appNavigationStateService.onNavigateToSpace(id, MAIN_SPACE)

                // 启动登录流程事件处理器（监听房间成员变化等事件）
                loggedInFlowProcessor.observeEvents(sessionCoroutineScope)

                // 设置会话验证请求监听器
                matrixClient.sessionVerificationService.setListener(verificationListener)

                // 执行媒体预览配置的迁移任务
                mediaPreviewConfigMigration()

                // 在会话协程作用域中预获取最大文件上传大小
                // 等待网络连接后再执行
                sessionCoroutineScope.launch {
                    networkMonitor.connectivity.first { networkStatus -> networkStatus == NetworkStatus.Connected }
                    matrixClient.getMaxFileUploadSize()
                }

                // 启动分析事务，记录"首次房间显示"事件
                analyticsService.startLongRunningTransaction(AnalyticsLongRunningTransaction.FirstRoomsDisplayed)

                // 观察FTUE状态，根据状态导航到引导页或首页
                ftueService.state
                    .onEach { ftueState ->
                        when (ftueState) {
                            is FtueState.Unknown -> Unit // 未知状态，不做处理
                            is FtueState.Incomplete -> backstack.safeRoot(NavTarget.Ftue)  // FTUE未完成，导航到引导页
                            is FtueState.Complete -> backstack.safeRoot(NavTarget.Home)    // FTUE完成，导航到首页
                        }
                    }
                    .launchIn(lifecycleScope)
            },

            /**
             * onResume - 节点恢复时更新可用房间列表
             *
             * 当应用恢复到前台时，更新通知服务中可用的房间列表
             */
            onResume = {
                lifecycleScope.launch {
                    val availableRoomIds = matrixClient.getJoinedRoomIds().getOrNull() ?: return@launch
                    notificationConversationService.onAvailableRoomsChanged(
                        sessionId = matrixClient.sessionId,
                        roomIds = availableRoomIds
                    )
                }
            },

            /**
             * onDestroy - 节点销毁时清理资源
             *
             * 执行清理任务：
             * 1. 通知导航状态服务离开空间和会话
             * 2. 停止事件处理器
             * 3. 移除验证监听器
             * 4. 停止分析观察者
             */
            onDestroy = {
                appNavigationStateService.onLeavingSpace(id)
                appNavigationStateService.onLeavingSession(id)
                loggedInFlowProcessor.stopObserving()
                matrixClient.sessionVerificationService.setListener(null)
                analyticsRoomListStateWatcher.stop()
            }
        )

        // 启动消息发送队列
        setupSendingQueue()
    }

    /**
     * 设置消息发送队列
     *
     * 将发送队列绑定到节点的 lifecycleScope，
     * 使队列的生命周期与节点生命周期一致
     */
    private fun setupSendingQueue() {
        sendingQueue.launchIn(lifecycleScope)
    }

    // ==================== 导航目标定义 ====================
    /**
     * NavTarget - 登录后流程的导航目标密封接口
     *
     * 定义了用户登录后所有可能的导航目标，每个目标对应一个子节点。
     * 实现Parcelable以支持状态保存和恢复。
     *
     * 导航目标分类：
     * - 核心页面：Home、Room、Settings、UserProfile
     * - 创建功能：CreateRoom、CreateSpace
     * - 安全功能：SecureBackup、LinkNewDevice
     * - 引导流程：Ftue
     * - 外部入口：RoomDirectory、IncomingShare、IncomingVerificationRequest
     * - 内部占位：Placeholder、LoggedInPermanent
     */
    sealed interface NavTarget : Parcelable {
        /**
         * Placeholder - 占位符导航目标
         *
         * 在BackStack初始化时使用的临时目标，
         * 会在FTUE状态检查完成后被替换为实际的首页或引导页
         */
        @Parcelize
        data object Placeholder : NavTarget

        /**
         * LoggedInPermanent - 永久显示的登录组件
         *
         * 定义在PermanentNavModel中，会始终显示在屏幕上。
         * 通常包含底部导航栏等全局UI组件（LoggedInNode）
         */
        @Parcelize
        data object LoggedInPermanent : NavTarget

        /**
         * Home - 首页/房间列表
         *
         * 显示用户加入的房间列表，是登录后的默认页面。
         * 支持房间搜索、创建房间快捷入口等功能
         */
        @Parcelize
        data object Home : NavTarget

        /**
         * Room - 房间详情页面
         *
         * 加载并显示特定房间的聊天界面。
         *
         * @param roomIdOrAlias 房间ID或房间别名
         * @param serverNames 解析房间时使用的服务器名称列表
         * @param trigger 进入房间的分析触发来源
         * @param roomDescription 房间描述信息（从房间目录进入时）
         * @param initialElement 房间内的初始导航目标
         * @param targetId 目标唯一标识，用于区分同一房间的多次访问
         */
        @Parcelize
        data class Room(
            val roomIdOrAlias: RoomIdOrAlias,
            val serverNames: List<String> = emptyList(),
            val trigger: JoinedRoomAnalyticsEvent.Trigger? = null,
            val roomDescription: RoomDescription? = null,
            val initialElement: RoomNavigationTarget = RoomNavigationTarget.Root(),
            val targetId: UUID = UUID.randomUUID(),
        ) : NavTarget

        /**
         * UserProfile - 用户资料页面
         *
         * 显示指定用户的详细信息，包括头像、昵称、房间列表等
         *
         * @param userId 要查看的用户ID
         */
        @Parcelize
        data class UserProfile(
            val userId: UserId,
        ) : NavTarget

        /**
         * Settings - 设置页面
         *
         * 提供应用设置入口，支持多级子页面
         *
         * @param initialElement 初始显示的设置子页面
         */
        @Parcelize
        data class Settings(
            val initialElement: PreferencesEntryPoint.InitialTarget = PreferencesEntryPoint.InitialTarget.Root
        ) : NavTarget

        /**
         * StartChat - 去发起聊天
         *
         */
        @Parcelize
        data object StartChat : NavTarget

        /**
         * CreateRoom - 创建房间页面
         *
         * 提供创建新房间的界面，支持房间名称、隐私设置、成员邀请等配置
         */
        @Parcelize
        data object CreateRoom : NavTarget
        /**
         * CreateSpace - 创建空间页面
         *
         * 提供创建新空间的界面（Space是房间的集合）
         */
        @Parcelize
        data object CreateSpace : NavTarget

        /**
         * SecureBackup - 安全备份页面
         *
         * 管理密钥备份和恢复，包括设置恢复密钥、验证备份状态等功能
         *
         * @param initialElement 初始显示的备份子页面
         */
        @Parcelize
        data class SecureBackup(
            val initialElement: SecureBackupEntryPoint.InitialTarget = SecureBackupEntryPoint.InitialTarget.Root
        ) : NavTarget

        /**
         * Ftue - 首次使用引导页面
         *
         * 新用户或FTUE未完成时显示的引导流程，
         * 包括通知设置、消息加密说明、同步设置等
         */
        @Parcelize
        data object Ftue : NavTarget

        @Parcelize
        data object SessionVerification : NavTarget

        /**
         * LinkNewDevice - 链接新设备页面
         *
         * 用于在新设备上验证并链接现有会话，
         * 包含二维码扫描或手动输入验证码的流程
         */
        @Parcelize
        data object LinkNewDevice : NavTarget

        /**
         * RoomDirectory - 房间目录页面
         *
         * 浏览和搜索公共房间的目录，
         * 支持按名称、主题等条件筛选房间
         */
        @Parcelize
        data object RoomDirectory : NavTarget

        /**
         * IncomingShare - 外部分享接收页面
         *
         * 处理来自其他应用的分享意图，
         * 如分享链接、文本或媒体内容到Matrix房间
         *
         * @param intent 来自外部应用的分享意图
         */
        @Parcelize
        data class IncomingShare(val intent: Intent) : NavTarget

        /**
         * IncomingVerificationRequest - 收到的验证请求页面
         *
         * 显示来自其他设备的会话验证请求，
         * 用户可以接受或拒绝验证
         *
         * @param data 验证请求数据
         */
        @Parcelize
        data class IncomingVerificationRequest(val data: VerificationRequest.Incoming) : NavTarget
    }

    // ==================== 导航目标解析 ====================
    /**
     * resolve - 根据导航目标创建对应的子节点
     *
     * 这是Appyx导航框架的核心方法，
     * 当导航到某个NavTarget时，此方法负责创建并返回对应的Node实例。
     *
     * 每个case的处理流程：
     * 1. 创建必要的回调接口实现（用于子节点与父节点通信）
     * 2. 准备输入参数（Inputs/Params）
     * 3. 调用对应的EntryPoint创建Node
     * 4. 将Node作为返回值
     *
     * @param navTarget 要解析的导航目标
     * @param buildContext 构建上下文
     * @return 对应的Node实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            // 占位符 - 返回空节点
            NavTarget.Placeholder -> emptyNode(buildContext)

            // 永久组件 - 创建LoggedInNode（包含底部导航栏等）
            NavTarget.LoggedInPermanent -> {
                val callback = object : LoggedInNode.Callback {
                    override fun navigateToNotificationTroubleshoot() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.NotificationTroubleshoot))
                    }
                }
                createNode<LoggedInNode>(buildContext, listOf(callback))
            }

            // 首页 - 创建房间列表页面
            NavTarget.Home -> {
                // 创建Home页面的回调，处理各种导航请求
                val callback = object : HomeEntryPoint.Callback {
                    override fun navigateToRoom(roomId: RoomId, joinedRoom: JoinedRoom?) {
                        backstack.push(
                            NavTarget.Room(
                                roomIdOrAlias = roomId.toRoomIdOrAlias(),
                                initialElement = RoomNavigationTarget.Root(joinedRoom = joinedRoom)
                            )
                        )
                    }

                    override fun navigateToSettings() {
                        backstack.push(NavTarget.Settings())
                    }

                    override fun navigateToUserProfile(matrixUser: MatrixUser) {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.UserProfile(matrixUser)))
                    }

                    override fun navigateToUserQrCode(matrixUser: MatrixUser) {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.UserQrCode(matrixUser)))
                    }

                    override fun navigateToNotificationSettings() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.NotificationSettings))
                    }

                    override fun navigateToLockScreenSettings() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.LockScreenSettings))
                    }

                    override fun navigateToAdvancedSettings() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.AdvancedSettings))
                    }

                    override fun navigateToAbout() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.About))
                    }

                    override fun navigateToBlockedUsers() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.BlockedUsers))
                    }

                    override fun navigateToSignOut() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.SignOut))
                    }

                    override fun navigateToStartChat() {
                        overlay.show(NavTarget.StartChat)
                    }

                    override fun navigateToCreateRoom() {
                        overlay.show(NavTarget.CreateRoom)
                    }

                    override fun navigateToCreateSpace() {
                        backstack.push(NavTarget.CreateSpace)
                    }

                    override fun navigateToSetUpRecovery() {
                        backstack.push(NavTarget.SecureBackup(initialElement = SecureBackupEntryPoint.InitialTarget.Root))
                    }

                    override fun navigateToEnterRecoveryKey() {
                        backstack.push(NavTarget.SessionVerification)
                    }

                    override fun navigateToRoomSettings(roomId: RoomId) {
                        backstack.push(NavTarget.Room(roomId.toRoomIdOrAlias(), initialElement = RoomNavigationTarget.Details))
                    }

                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun navigateToScanQrCode() {
                        overlay.show(NavTarget.StartChat)
                    }
                }
                homeEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }

            // 房间详情 - 创建房间导航节点
            is NavTarget.Room -> {
                val joinedRoomCallback = object : JoinedRoomLoadedFlowNode.Callback {
                    override fun navigateToRoom(roomId: RoomId, serverNames: List<String>) {
                        backstack.push(NavTarget.Room(roomId.toRoomIdOrAlias(), serverNames))
                    }

                    override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) {
                        when (data) {
                            // 用户链接 - 应在MessagesNode中处理，记录错误日志
                            is PermalinkData.UserLink -> {
                                Timber.e("User link clicked: ${data.userId}.")
                            }
                            // 房间链接 - 导航到指定房间和事件
                            is PermalinkData.RoomLink -> {
                                val target = NavTarget.Room(
                                    roomIdOrAlias = data.roomIdOrAlias,
                                    serverNames = data.viaParameters,
                                    trigger = JoinedRoomAnalyticsEvent.Trigger.Timeline,
                                    initialElement = RoomNavigationTarget.Root(data.eventId),
                                )
                                if (pushToBackstack) {
                                    backstack.push(target)
                                } else {
                                    backstack.replace(target)
                                }
                            }
                            // 备用链接和邮件邀请链接 - 应在MessagesNode中处理
                            is PermalinkData.FallbackLink,
                            is PermalinkData.RoomEmailInviteLink -> {
                                // 无操作
                            }
                        }
                    }

                    override fun navigateToGlobalNotificationSettings() {
                        backstack.push(NavTarget.Settings(PreferencesEntryPoint.InitialTarget.NotificationSettings))
                    }
                }
                val inputs = RoomFlowNode.Inputs(
                    roomIdOrAlias = navTarget.roomIdOrAlias,
                    roomDescription = Optional.ofNullable(navTarget.roomDescription),
                    serverNames = navTarget.serverNames,
                    trigger = Optional.ofNullable(navTarget.trigger),
                    initialElement = navTarget.initialElement
                )
                createNode<RoomFlowNode>(buildContext, plugins = listOf(inputs, joinedRoomCallback))
            }

            // 用户资料 - 创建用户资料页面
            is NavTarget.UserProfile -> {
                val callback = object : UserProfileEntryPoint.Callback {
                    override fun navigateToRoom(roomId: RoomId) {
                        backstack.push(NavTarget.Room(roomId.toRoomIdOrAlias()))
                    }
                }
                userProfileEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = UserProfileEntryPoint.Params(userId = navTarget.userId),
                    callback = callback,
                )
            }

            // 设置页面 - 创建设置导航节点
            is NavTarget.Settings -> {
                val callback = object : PreferencesEntryPoint.Callback {
                    override fun navigateToAddAccount() {
                        callback.navigateToAddAccount()
                    }

                    override fun navigateToLinkNewDevice() {
                        backstack.push(NavTarget.LinkNewDevice)
                    }

                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun navigateToSecureBackup() {
                        backstack.push(NavTarget.SecureBackup())
                    }

                    override fun navigateToRoomNotificationSettings(roomId: RoomId) {
                        backstack.push(NavTarget.Room(roomId.toRoomIdOrAlias(), initialElement = RoomNavigationTarget.NotificationSettings))
                    }

                    override fun navigateToEvent(roomId: RoomId, eventId: EventId) {
                        backstack.push(NavTarget.Room(roomId.toRoomIdOrAlias(), initialElement = RoomNavigationTarget.Root(eventId)))
                    }
                }
                val inputs = PreferencesEntryPoint.Params(navTarget.initialElement)
                preferencesEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = inputs,
                    callback = callback,
                )
            }

            // 发起聊天 - 启动发起聊天流程
            NavTarget.StartChat -> {
                val callback = object : StartChatEntryPoint.Callback {
                    override fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>) {
                        // StartChat 是通过 overlay 展示的，进入房间前需要先关闭它
                        overlay.hide()
                        // 保留上一页（通常是 Home），进入房间后可正常返回
                        backstack.push(NavTarget.Room(roomIdOrAlias = roomIdOrAlias, serverNames = serverNames))
                    }

                    override fun navigateToRoomDirectory() {
                        backstack.push(NavTarget.RoomDirectory)
                    }
                }

                startChatEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                )
            }

            // 创建房间 - 启动房间创建流程
            is NavTarget.CreateRoom -> {
                val callback = object : CreateRoomEntryPoint.Callback {
                    override fun onRoomCreated(roomId: RoomId) {
                        // 创建成功后，替换当前页面为新房间
                        overlay.hide()
                        backstack.push(NavTarget.Room(roomIdOrAlias = RoomIdOrAlias.Id(roomId), serverNames = emptyList()))
                    }
                }
                createRoomEntryPoint.createNode(
                    isSpace = false,
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback
                )
            }

            // 创建空间 - 启动空间创建流程
            is NavTarget.CreateSpace -> {
                val callback = object : CreateRoomEntryPoint.Callback {
                    override fun onRoomCreated(roomId: RoomId) {
                        // 创建成功后，替换当前页面为新房间
                        backstack.replace(NavTarget.Room(roomIdOrAlias = RoomIdOrAlias.Id(roomId), serverNames = emptyList()))
                    }
                }
                createRoomEntryPoint.createNode(isSpace = true, parentNode = this, buildContext = buildContext, callback = callback)
            }

            // 安全备份 - 创建安全备份页面
            is NavTarget.SecureBackup -> {
                secureBackupEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = SecureBackupEntryPoint.Params(initialElement = navTarget.initialElement),
                    callback = object : SecureBackupEntryPoint.Callback {
                        override fun onDone() {
                            backstack.pop()
                        }
                    },
                )
            }

            // 首次引导 - 创建FTUE页面
            NavTarget.Ftue -> {
                ftueEntryPoint.createNode(this, buildContext)
            }

            NavTarget.SessionVerification -> {
                val callback = object : FtueSessionVerificationFlowNode.Callback {
                    override fun onBack() {
                        backstack.pop()
                    }

                    override fun onDone() {
                        backstack.pop()
                    }
                }
                createNode<FtueSessionVerificationFlowNode>(buildContext, listOf(callback))
            }

            // 链接新设备 - 创建设备链接页面
            NavTarget.LinkNewDevice -> {
                val callback = object : LinkNewDeviceEntryPoint.Callback {
                    override fun onDone() {
                        backstack.pop()
                    }
                }
                linkNewDeviceEntryPoint.createNode(this, buildContext, callback)
            }

            // 房间目录 - 创建房间目录页面
            NavTarget.RoomDirectory -> {
                roomDirectoryEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    callback = object : RoomDirectoryEntryPoint.Callback {
                        override fun navigateToRoom(roomDescription: RoomDescription) {
                            backstack.push(
                                NavTarget.Room(
                                    roomIdOrAlias = roomDescription.roomId.toRoomIdOrAlias(),
                                    roomDescription = roomDescription,
                                    trigger = JoinedRoomAnalyticsEvent.Trigger.RoomDirectory,
                                )
                            )
                        }
                    },
                )
            }

            // 外部分享 - 处理分享意图
            is NavTarget.IncomingShare -> {
                shareEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = ShareEntryPoint.Params(intent = navTarget.intent),
                    callback = object : ShareEntryPoint.Callback {
                        override fun onDone(roomIds: List<RoomId>) {
                            // 关闭分享接收页面
                            backstack.pop()

                            // 如果只分享到一个房间，自动导航到该房间
                            roomIds.singleOrNull()?.let { roomId ->
                                sessionCoroutineScope.launch {
                                    // 等待分享页面关闭
                                    backstack.elements.first { it.lastOrNull()?.key?.navTarget !is NavTarget.IncomingShare }

                                    // 附加到目标房间
                                    attachRoom(roomId.toRoomIdOrAlias(), clearBackstack = false)
                                }
                            }
                        }
                    },
                )
            }

            // 验证请求 - 创建验证请求页面
            is NavTarget.IncomingVerificationRequest -> {
                incomingVerificationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = IncomingVerificationEntryPoint.Params(navTarget.data),
                    callback = object : IncomingVerificationEntryPoint.Callback {
                        override fun onDone() {
                            backstack.pop()
                        }
                    },
                )
            }
        }
    }

    // ==================== 外部附加方法 ====================
    /**
     * attachRoom - 以编程方式附加房间页面
     *
     * 用于从外部（如外部Intent处理）导航到房间页面。
     * 与resolve方法不同，attachRoom会：
     * 1. 等待Home页面加载完成
     * 2. 通过AttachRoomOperation自定义导航行为
     * 3. 返回创建的房间节点
     *
     * @param roomIdOrAlias 房间ID或别名
     * @param serverNames 服务器名称列表（用于解析房间）
     * @param trigger 进入房间的分析触发来源
     * @param eventId 可选的事件ID，直接跳转到指定消息
     * @param clearBackstack 是否清空回栈（为true时只保留Home页面）
     * @return 创建的RoomFlowNode实例
     */
    suspend fun attachRoom(
        roomIdOrAlias: RoomIdOrAlias,
        serverNames: List<String> = emptyList(),
        trigger: JoinedRoomAnalyticsEvent.Trigger? = null,
        eventId: EventId? = null,
        clearBackstack: Boolean,
    ): RoomFlowNode {
        // 等待Home页面加载完成
        waitForNavTargetAttached { navTarget ->
            navTarget is NavTarget.Home
        }
        // 附加子节点并执行房间附加操作
        attachChild<RoomFlowNode> {
            val roomNavTarget = NavTarget.Room(
                roomIdOrAlias = roomIdOrAlias,
                serverNames = serverNames,
                trigger = trigger,
                initialElement = RoomNavigationTarget.Root(eventId = eventId)
            )
            backstack.accept(AttachRoomOperation(roomNavTarget, clearBackstack))
        }

        // 等待正确的房间节点附加完成
        // 避免返回到仍在显示的旧节点实例
        return waitForChildAttached<RoomFlowNode, NavTarget> {
            it is NavTarget.Room &&
                it.roomIdOrAlias == roomIdOrAlias &&
                it.initialElement is RoomNavigationTarget.Root &&
                it.initialElement.eventId == eventId
        }
    }

    /**
     * attachUser - 以编程方式附加用户资料页面
     *
     * 用于从外部导航到指定用户的资料页面
     *
     * @param userId 要查看的用户ID
     */
    suspend fun attachUser(userId: UserId) {
        // 等待Home页面加载完成
        waitForNavTargetAttached { navTarget ->
            navTarget is NavTarget.Home
        }
        attachChild<Node> {
            backstack.push(
                NavTarget.UserProfile(
                    userId = userId,
                )
            )
        }
    }

    /**
     * attachIncomingShare - 以编程方式附加分享接收页面
     *
     * 处理来自外部应用的分享Intent
     *
     * @param intent 包含分享内容的Intent
     */
    internal suspend fun attachIncomingShare(intent: Intent) {
        // 等待Home页面加载完成
        waitForNavTargetAttached { navTarget ->
            navTarget is NavTarget.Home
        }
        attachChild<Node> {
            backstack.push(
                NavTarget.IncomingShare(intent)
            )
        }
    }

    // ==================== UI 渲染 ====================
    /**
     * View - Compose UI 渲染方法
     *
     * 负责渲染登录后流程的UI，包含以下层次：
     * 1. ElementThemeApp - 应用主题容器，应用企业主题
     * 2. ConnectivityIndicatorContainer - 网络状态指示器容器
     * 3. Box - 内容容器
     * 4. BackstackView - 渲染BackStack中的当前页面
     * 5. PermanentChild - 渲染永久组件（底部导航栏等）
     *
     * UI显示逻辑：
     * - 始终显示BackStackView（当前页面内容）
     * - 当FTUE完成时，显示PermanentChild（LoggedInNode）
     * - 网络状态指示器浮动在最上层
     *
     * @param modifier 外部传入的修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 从企业服务获取语义颜色
        val colors by remember {
            enterpriseService.semanticColorsFlow(sessionId = matrixClient.sessionId)
        }.collectAsState(SemanticColorsLightDark.default)

        // 应用主题
        ElementThemeApp(
            appPreferencesStore = appPreferencesStore,
            compoundLight = colors.light,
            compoundDark = colors.dark,
            buildMeta = buildMeta,
        ) {
            // 获取网络在线状态
            val isOnline by syncService.isOnline.collectAsState()

            // 网络连接指示器容器
            ConnectivityIndicatorContainer(
                isOnline = isOnline,
                modifier = modifier,
            ) { contentModifier ->
                Box(modifier = contentModifier) {
                    // 获取FTUE状态
                    val ftueState by ftueService.state.collectAsState()

                    // 渲染当前页面（BackStack顶部节点）
                    BackstackView()

                    // FTUE完成后，显示永久组件（底部导航栏等）
                    if (ftueState is FtueState.Complete) {
                        PermanentChild(permanentNavModel = permanentNavModel, navTarget = NavTarget.LoggedInPermanent)
                    }

                    OverlayView(transitionHandler = remember { JumpToEndTransitionHandler() })
                }
            }
        }
    }
}

/**
 * AttachRoomOperation - 房间附加操作
 *
 * 自定义BackStackOperation实现，用于控制房间页面的附加行为。
 * 相比默认的Push操作，提供以下特殊逻辑：
 * 1. clearBackstack=true 时：清空其他页面，只保留Home（stash状态）
 * 2. clearBackstack=false 时：激活已存在的房间页面，或推入新页面
 *
 * 使用场景：
 * - 从外部Intent进入房间时（clearBackstack=true）
 * - 在已打开的房间之间切换时（clearBackstack=false）
 */
@Parcelize
private class AttachRoomOperation(
    val roomTarget: LoggedInFlowNode.NavTarget.Room,  // 要附加的目标房间
    val clearBackstack: Boolean,                       // 是否清空回栈
) : BackStackOperation<LoggedInFlowNode.NavTarget> {

    /**
     * 检查操作是否适用
     * 始终返回true，因为此操作处理所有情况
     */
    override fun isApplicable(elements: NavElements<LoggedInFlowNode.NavTarget, BackStack.State>) = true

    /**
     * invoke - 执行导航操作
     *
     * 核心逻辑：
     * 1. 如果clearBackstack=true：
     *    - 将除了Home以外的所有页面移除
     *    - 将Home页面设为stash状态
     *    - 创建新的房间页面并设为active状态
     *
     * 2. 如果clearBackstack=false：
     *    - 查找是否已存在相同房间的页面
     *    - 如果存在：激活该页面，其他页面设为stash
     *    - 如果不存在：推入新房间页面
     *
     * @param elements 当前的导航元素列表
     * @return 转换后的导航元素列表
     */
    override fun invoke(elements: BackStackElements<LoggedInFlowNode.NavTarget>): BackStackElements<LoggedInFlowNode.NavTarget> {
        return if (clearBackstack) {
            // 清空回栈模式：只保留Home页面（设为stash），添加新房间页面
            elements.mapNotNull { element ->
                if (element.key.navTarget == LoggedInFlowNode.NavTarget.Home) {
                    // 将Home页面设为stash状态
                    element.transitionTo(STASHED, this)
                } else {
                    null  // 移除其他页面
                }
            } + BackStackElement(
                key = NavKey(roomTarget),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = this
            )
        } else {
            // 激活现有页面或推入新页面
            val existingRoomElement = elements.find {
                val roomNavTarget = it.key.navTarget as? LoggedInFlowNode.NavTarget.Room
                roomNavTarget?.roomIdOrAlias == roomTarget.roomIdOrAlias
            }
            if (existingRoomElement != null) {
                // 找到已存在的房间页面，激活它，其他页面设为stash
                elements.mapNotNull { element ->
                    if (element == existingRoomElement) {
                        null  // 保留现有元素，稍后单独处理
                    } else {
                        element.transitionTo(STASHED, this)
                    }
                } + existingRoomElement.transitionTo(ACTIVE, this)
            } else {
                // 没有已存在的房间页面，推入新页面
                Push<LoggedInFlowNode.NavTarget>(roomTarget).invoke(elements)
            }
        }
    }
}
