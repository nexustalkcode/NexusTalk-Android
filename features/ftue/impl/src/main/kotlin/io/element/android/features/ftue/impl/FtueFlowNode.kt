/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl

/**
 * FtueFlowNode - 首次用户体验（FTUE）引导流程管理节点
 *
 * 该类是 Appyx 框架中的导航节点，负责管理用户首次使用应用时的引导流程。
 * 它继承自 BaseFlowNode，使用 BackStack 作为导航模型来管理不同的引导步骤。
 *
 * 主要职责：
 * 1. 根据 FtueService 的状态显示相应的引导步骤界面
 * 2. 管理会话验证、通知授权、 analytics 授权和锁屏设置等引导流程
 * 3. 处理各步骤完成后的回调，更新引导进度状态
 *
 * 导航流程遵循以下顺序：
 * 1. Placeholder（占位符，用于等待初始状态）
 * 2. SessionVerification（会话验证）
 * 3. NotificationsOptIn（通知授权）
 * 4. AnalyticsOptIn（分析授权）
 * 5. LockScreenSetup（锁屏设置）
 */
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.replace
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.analytics.api.AnalyticsEntryPoint
import io.element.android.features.ftue.impl.notifications.NotificationsOptInNode
import io.element.android.features.ftue.impl.sessionverification.FtueSessionVerificationFlowNode
import io.element.android.features.ftue.impl.state.DefaultFtueService
import io.element.android.features.ftue.impl.state.FtueStep
import io.element.android.features.ftue.impl.state.InternalFtueState
import io.element.android.features.ftue.impl.welcome.WelcomeNode
import io.element.android.features.lockscreen.api.LockScreenEntryPoint
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.ui.common.nodes.emptyNode
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import timber.log.Timber

private const val startupTraceTag = "StartupTrace"

/**
 * FTUE 引导流程节点构造函数
 *
 * @param buildContext 构建上下文，包含节点构建所需的信息，如保存的状态映射等
 * @param plugins 插件列表，用于扩展节点功能
 * @param defaultFtueService 默认的 FTUE 服务，负责管理引导步骤状态和进度
 * @param analyticsEntryPoint Analytics 功能的入口点，用于创建 analytics 授权界面
 * @param lockScreenEntryPoint 锁屏功能的入口点，用于创建锁屏设置界面
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class FtueFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val defaultFtueService: DefaultFtueService,
    private val analyticsEntryPoint: AnalyticsEntryPoint,
    private val lockScreenEntryPoint: LockScreenEntryPoint,
) : BaseFlowNode<FtueFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Placeholder,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 导航目标密封接口，定义了 FTUE 流程中的各个步骤
     *
     * 每个 NavTarget 代表引导流程中的一个独立界面或功能模块。
     * 通过实现 Parcelable 接口，支持状态保存和恢复。
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 占位符导航目标
         * 用于引导流程初始化阶段，显示空白界面等待 FtueService 确定下一步骤
         */
        @Parcelize
        data object Placeholder : NavTarget

        /**
         * 欢迎页面导航目标
         * 显示欢迎页面，介绍 NexusTalk 应用
         */
        @Parcelize
        data object Welcome : NavTarget

        /**
         * 会话验证导航目标
         * 用户完成登录后，需要验证会话的安全性，确认设备信任关系
         */
        @Parcelize
        data object SessionVerification : NavTarget

        /**
         * 通知授权导航目标
         * 引导用户选择是否接收应用通知通知
         */
        @Parcelize
        data object NotificationsOptIn : NavTarget

        /**
         * 分析授权导航目标
         * 引导用户选择是否允许发送匿名使用统计数据以改进产品
         */
        @Parcelize
        data object AnalyticsOptIn : NavTarget

        /**
         * 锁屏设置导航目标
         * 引导用户设置应用内锁屏功能，增强隐私保护
         */
        @Parcelize
        data object LockScreenSetup : NavTarget
    }

    /**
     * 节点构建完成后的初始化方法
     *
     * 该方法在节点完成构建后立即被调用，用于设置 FTUE 流程的状态监听。
     * 通过订阅 defaultFtueService 的状态流，当引导步骤发生变化时自动切换界面。
     *
     * 工作流程：
     * 1. 监听 ftueStepStateFlow 状态流
     * 2. 过滤出未完成状态（Incomplete）
     * 3. 提取下一步骤（nextStep）
     * 4. 调用 showStep() 方法更新导航栈
     */
    override fun onBuilt() {
        super.onBuilt()
        // 这里记录 FTUE 流程节点何时开始接管页面，以及它收到的下一步到底是什么。
        Timber.tag(startupTraceTag).i("FtueFlowNode.onBuilt")
        defaultFtueService.ftueStepStateFlow
            .filterIsInstance(InternalFtueState.Incomplete::class)
            .onEach {
                Timber.tag(startupTraceTag).i("FtueFlowNode.observe incomplete nextStep=%s", it.nextStep)
                showStep(it.nextStep)
            }
            .launchIn(lifecycleScope)
    }

    /**
     * 导航目标解析方法
     *
     * 根据传入的 NavTarget 创建对应的子节点。该方法是 Appyx 框架导航系统的核心部分，
     * 负责将导航意图转换为实际的界面节点。
     *
     * 每个导航目标都关联特定的界面和回调逻辑：
     * - Placeholder：空节点，不执行任何操作
     * - SessionVerification：会话验证界面，完成后标记步骤已完成
     * - NotificationsOptIn：通知授权界面，完成后更新 FTUE 步骤
     * - AnalyticsOptIn：分析授权界面，由 analyticsEntryPoint 管理
     * - LockScreenSetup：锁屏设置界面，完成后更新 FTUE 步骤
     *
     * @param navTarget 要解析的导航目标
     * @param buildContext 构建上下文信息
     * @return 对应的 Node 实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        Timber.tag(startupTraceTag).i("FtueFlowNode.resolve navTarget=%s", navTarget)
        return when (navTarget) {
            NavTarget.Placeholder -> {
                emptyNode(buildContext)
            }
            is NavTarget.Welcome -> {
                val callback = object : WelcomeNode.Callback {
                    override fun onDone() {
                        defaultFtueService.updateFtueStep(FtueStep.Welcome)
                    }

                    override fun onPrivacyPolicyClick() {

                    }
                }
                createNode<WelcomeNode>(buildContext, listOf(callback))
            }
            is NavTarget.SessionVerification -> {
                val callback = object : FtueSessionVerificationFlowNode.Callback {
                    override fun onBack() {
                        Unit
                    }

                    override fun onDone() {
                        defaultFtueService.onUserCompletedSessionVerification()
                    }
                }
                createNode<FtueSessionVerificationFlowNode>(buildContext, listOf(callback))
            }
            NavTarget.NotificationsOptIn -> {
                val callback = object : NotificationsOptInNode.Callback {
                    override fun onNotificationsOptInFinished() {
                        defaultFtueService.updateFtueStep(FtueStep.NotificationsOptIn)
                    }
                }
                createNode<NotificationsOptInNode>(buildContext, listOf(callback))
            }
            NavTarget.AnalyticsOptIn -> {
                analyticsEntryPoint.createNode(this, buildContext)
            }
            NavTarget.LockScreenSetup -> {
                val callback = object : LockScreenEntryPoint.Callback {
                    override fun onSetupDone() {
                        defaultFtueService.updateFtueStep(FtueStep.LockscreenSetup)
                    }
                }
                lockScreenEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    navTarget = LockScreenEntryPoint.Target.Setup,
                    callback = callback,
                )
            }
        }
    }

    /**
     * 显示指定步骤的引导界面
     *
     * 该方法负责将 FTUE 步骤映射到相应的导航目标，并更新 BackStack 导航栈。
     * 使用 newRoot 操作替换整个导航栈，确保用户只能按顺序完成引导流程。
     * 注意：AnalyticsOptIn 使用 replace 操作，允许用户返回上一步重新选择。
     *
     * 步骤映射关系：
     * - WaitingForInitialState → Placeholder（等待初始状态）
     * - SessionVerification → SessionVerification（会话验证）
     * - NotificationsOptIn → NotificationsOptIn（通知授权）
     * - AnalyticsOptIn → AnalyticsOptIn（分析授权）
     * - LockscreenSetup → LockScreenSetup（锁屏设置）
     *
     * @param ftueStep 要显示的 FTUE 步骤枚举值
     */
    private fun showStep(ftueStep: FtueStep) {
        // 这里记录内部 FTUE 步骤到真实导航目标的映射，便于确认“状态变了但页面没切”是否发生。
        Timber.tag(startupTraceTag).i("FtueFlowNode.showStep ftueStep=%s", ftueStep)
        when (ftueStep) {
            FtueStep.WaitingForInitialState -> {
                backstack.newRoot(NavTarget.Placeholder)
            }
            FtueStep.Welcome -> {
                backstack.newRoot(NavTarget.Welcome)
            }
            FtueStep.SessionVerification -> {
                backstack.newRoot(NavTarget.SessionVerification)
            }
            FtueStep.NotificationsOptIn -> {
                backstack.newRoot(NavTarget.NotificationsOptIn)
            }
            FtueStep.AnalyticsOptIn -> {
                backstack.replace(NavTarget.AnalyticsOptIn)
            }
            FtueStep.LockscreenSetup -> {
                backstack.newRoot(NavTarget.LockScreenSetup)
            }
        }
    }

    /**
     * 组合式 UI 渲染方法
     *
     * 该方法使用 Appyx 框架的 BackstackView 组件来渲染当前导航栈中的内容。
     * BackstackView 会自动监听 BackStack 的状态变化，并显示当前顶部节点的界面。
     *
     * @param modifier 修饰符，用于调整布局属性
     */
    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }
}
