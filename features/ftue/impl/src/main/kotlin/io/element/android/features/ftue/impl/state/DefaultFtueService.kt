/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.state

import android.Manifest
import android.os.Build
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.features.ftue.api.state.FtueService
import io.element.android.features.ftue.api.state.FtueState
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.libraries.core.coroutine.mapState
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import io.element.android.libraries.matrix.api.verification.SessionVerifiedStatus
import io.element.android.libraries.permissions.api.PermissionStateProvider
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.toolbox.api.sdk.BuildVersionSdkIntProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private const val startupTraceTag = "StartupTrace"

/**
 * FTUE 服务默认实现类
 *
 * 该类是 FtueService 接口的默认实现，负责管理首次用户体验流程的核心逻辑。
 * 使用 @ContributesBinding 注解将其绑定到 SessionScope，
 * 使用 @SingleIn 注解确保每个会话只有一个实例。
 *
 * 主要职责：
 * - 监听会话验证状态，确定是否需要进行会话验证
 * - 计算并更新 FTUE 流程的当前步骤
 * - 判断是否需要请求通知权限
 * - 判断是否需要设置锁屏
 * - 管理 FTUE 完成状态
 *
 * @param sdkVersionProvider SDK 版本提供者，用于判断 Android 版本
 * @param sessionCoroutineScope 会话级别的协程作用域
 * @param analyticsService 分析服务
 * @param permissionStateProvider 权限状态提供者
 * @param lockScreenService 锁屏服务
 * @param sessionVerificationService 会话验证服务
 * @param sessionPreferencesStore 会话偏好设置存储
 */
@ContributesBinding(SessionScope::class)
@SingleIn(SessionScope::class)
class DefaultFtueService(
    private val sdkVersionProvider: BuildVersionSdkIntProvider,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val analyticsService: AnalyticsService,
    private val permissionStateProvider: PermissionStateProvider,
    private val lockScreenService: LockScreenService,
    private val sessionVerificationService: SessionVerificationService,
    private val sessionPreferencesStore: SessionPreferencesStore,
) : FtueService {
    /**
     * 用户是否需要确认会话验证成功
     *
     * 用于确保用户在会话验证屏幕确认后再继续 FTUE 流程。
     * 当会话未验证时设置为 true，验证完成后设置为 false。
     */
    private val userNeedsToConfirmSessionVerificationSuccess = MutableStateFlow(false)

    /**
     * FTUE 步骤状态流
     *
     * 存储当前 FTUE 流程的内部状态，用于驱动导航流程。
     */
    val ftueStepStateFlow = MutableStateFlow<InternalFtueState>(InternalFtueState.Unknown)

    /**
     * FTUE 公开状态
     *
     * 将内部 FTUE 状态映射为公开的 FtueState 状态。
     * 这是 FtueService 接口要求的公开状态属性。
     */
    override val state = ftueStepStateFlow
        .mapState {
            when (it) {
                is InternalFtueState.Unknown -> FtueState.Unknown
                is InternalFtueState.Incomplete -> FtueState.Incomplete
                is InternalFtueState.Complete -> FtueState.Complete
            }
        }

    init {
        // 这里直接输出 FTUE 内部状态，避免外层只看到 Incomplete 却不知道下一步是什么。
        ftueStepStateFlow
            .onEach { internalState ->
                Timber.tag(startupTraceTag).i("DefaultFtueService.internalState=%s", internalState)
            }
            .launchIn(sessionCoroutineScope)

        combine(
            sessionVerificationService.sessionVerifiedStatus.onEach { sessionVerifiedStatus ->
                Timber.tag(startupTraceTag).i("DefaultFtueService.sessionVerifiedStatus=%s", sessionVerifiedStatus)
                if (sessionVerifiedStatus == SessionVerifiedStatus.NotVerified) {
                    // Ensure we wait for the user to confirm the session verified screen before going further
                    userNeedsToConfirmSessionVerificationSuccess.value = true
                    Timber.tag(startupTraceTag).i("DefaultFtueService.requireSessionVerificationConfirmation=true")
                }
            },
            userNeedsToConfirmSessionVerificationSuccess,
        ) { sessionVerifiedStatus, userNeedsConfirmation ->
            Timber.tag(startupTraceTag).i(
                "DefaultFtueService.combine tick sessionVerifiedStatus=%s userNeedsConfirmation=%s",
                sessionVerifiedStatus,
                userNeedsConfirmation,
            )
            updateFtueStep()
        }
            .launchIn(sessionCoroutineScope)
    }

    /**
     * 更新 FTUE 步骤（带完成步骤参数）
     *
     * 当用户完成某个 FTUE 步骤后调用此方法，计算下一步应该显示什么。
     * 如果没有更多步骤，则将 FTUE 标记为完成。
     *
     * @param completedStep 用户刚完成的 FTUE 步骤
     */
    fun updateFtueStep(completedStep: FtueStep) = sessionCoroutineScope.launch {
        Timber.tag(startupTraceTag).i("DefaultFtueService.updateFtueStep completedStep=%s", completedStep)
        val step = getNextStep(completedStep)
        if (step == null) {
            // 跳过分析页时视为已询问过用户（不展示即不收集）
            analyticsService.setDidAskUserConsent()
            sessionPreferencesStore.setFtueCompleted(true)
            ftueStepStateFlow.value = InternalFtueState.Complete
        } else {
            ftueStepStateFlow.value = InternalFtueState.Incomplete(step)
        }
    }

    /**
     * 更新 FTUE 步骤（无参数版本）
     *
     * 无参数版本，用于在状态变化时自动重新计算下一步。
     * 只有当状态不是 Complete 时才更新。
     * 当没有更多步骤时，标记分析权限已询问并将 FTUE 标记为完成。
     */
    fun updateFtueStep() = sessionCoroutineScope.launch {
        // 如果已经是完成状态，不再更新
        if (ftueStepStateFlow.value is InternalFtueState.Complete) {
            Timber.tag(startupTraceTag).i("DefaultFtueService.updateFtueStep skipped because already complete")
            return@launch
        }
        val step = getNextStep(null)
        Timber.tag(startupTraceTag).i("DefaultFtueService.updateFtueStep nextStep=%s", step)
        ftueStepStateFlow.value = when (step) {
            null -> InternalFtueState.Complete
            else -> InternalFtueState.Incomplete(step)
        }
        if (step == null) {
            analyticsService.setDidAskUserConsent()
            sessionPreferencesStore.setFtueCompleted(true)
        }
    }

    /**
     * 获取下一步 FTUE 步骤
     *
     * 根据当前已完成的步骤和系统状态，计算下一步应该显示的 FTUE 步骤。
     *
     * @param completedStep 已完成的步骤，null 表示首次计算
     * @return 下一个 FtueStep，如果没有更多步骤则返回 null
     */
    private suspend fun getNextStep(completedStep: FtueStep? = null): FtueStep? =
        when (completedStep) {
            null -> {
                // 已完成 FTUE 的账号在冷启动恢复时不需要再等待会话验证状态初始化，
                // 否则会先进入 WaitingForInitialState 对应的空白占位页，造成可感知白屏。
                val isFtueCompleted = sessionPreferencesStore.isFtueCompleted().first()
                val isSessionVerificationStateReady = isSessionVerificationStateReady()
                Timber.tag(startupTraceTag).i(
                    "DefaultFtueService.getNextStep completedStep=null isFtueCompleted=%s sessionVerificationReady=%s sessionVerifiedStatus=%s",
                    isFtueCompleted,
                    isSessionVerificationStateReady,
                    sessionVerificationService.sessionVerifiedStatus.value,
                )
                if (isFtueCompleted) {
                    null
                } else if (!isSessionVerificationStateReady) {
                    FtueStep.WaitingForInitialState
                } else {
                    getNextStep(FtueStep.WaitingForInitialState)
                }
            }
            FtueStep.WaitingForInitialState -> {
                val isFtueCompleted = sessionPreferencesStore.isFtueCompleted().first()
                Timber.tag(startupTraceTag).i(
                    "DefaultFtueService.getNextStep completedStep=%s isFtueCompleted=%s",
                    completedStep,
                    isFtueCompleted,
                )
                if (isFtueCompleted) {
                    null
                } else {
                    FtueStep.Welcome
                }
            }
            // Even when FTUE has already been completed for this account, we still want to
            // prompt session verification after a fresh login if the session is not verified.
            FtueStep.SessionVerification -> {
                val isFtueCompleted = sessionPreferencesStore.isFtueCompleted().first()
                Timber.tag(startupTraceTag).i(
                    "DefaultFtueService.getNextStep completedStep=%s isFtueCompleted=%s userNeedsConfirmation=%s",
                    completedStep,
                    isFtueCompleted,
                    userNeedsToConfirmSessionVerificationSuccess.value,
                )
                if (isFtueCompleted) {
                    null
                } else {
                    FtueStep.Welcome
                }
            }
            FtueStep.Welcome -> {
                val shouldAskNotificationPermissions = shouldAskNotificationPermissions()
                Timber.tag(startupTraceTag).i(
                    "DefaultFtueService.getNextStep completedStep=%s shouldAskNotificationPermissions=%s",
                    completedStep,
                    shouldAskNotificationPermissions,
                )
                if (shouldAskNotificationPermissions) {
                    FtueStep.NotificationsOptIn
                } else {
                    getNextStep(FtueStep.NotificationsOptIn)
                }
            }
            FtueStep.NotificationsOptIn -> {
                val shouldDisplayLockscreenSetup = shouldDisplayLockscreenSetup()
                Timber.tag(startupTraceTag).i(
                    "DefaultFtueService.getNextStep completedStep=%s shouldDisplayLockscreenSetup=%s",
                    completedStep,
                    shouldDisplayLockscreenSetup,
                )
                if (shouldDisplayLockscreenSetup) {
                    FtueStep.LockscreenSetup
                } else {
                    getNextStep(FtueStep.LockscreenSetup)
                }
            }
            // 引导中不再展示分析页，锁屏设置完成后直接结束 FTUE
            FtueStep.LockscreenSetup -> {
                Timber.tag(startupTraceTag).i("DefaultFtueService.getNextStep completedStep=%s -> complete", completedStep)
                null
            }
            FtueStep.AnalyticsOptIn -> {
                Timber.tag(startupTraceTag).i("DefaultFtueService.getNextStep completedStep=%s -> complete", completedStep)
                null
            }
        }

    /**
     * 检查会话验证状态是否已准备好
     *
     * @return 如果会话验证状态不是 Unknown，返回 true
     */
    private fun isSessionVerificationStateReady(): Boolean {
        return sessionVerificationService.sessionVerifiedStatus.value != SessionVerifiedStatus.Unknown
    }

    /**
     * 检查会话是否未验证
     *
     * @return 如果会话状态为 NotVerified 且不能跳过验证，返回 true
     */
    /**
     * 检查是否可以跳过会话验证
     *
     * @return 如果用户之前选择跳过验证，返回 true
     */
    /**
     * 检查是否需要进行分析权限请求
     *
     * @return 如果用户还未被询问过分析权限，返回 true
     */
    private suspend fun needsAnalyticsOptIn(): Boolean {
        return analyticsService.didAskUserConsentFlow.first().not()
    }

    /**
     * 检查是否应该请求通知权限
     *
     * 仅在 Android 13+ (API 33) 上检查，因为 POST_NOTIFICATIONS 是 13+ 才有的权限。
     * 如果权限未授予且未被拒绝，则返回 true。
     *
     * @return 是否应该显示通知权限请求
     */
    private suspend fun shouldAskNotificationPermissions(): Boolean {
        return if (sdkVersionProvider.isAtLeast(Build.VERSION_CODES.TIRAMISU)) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val isPermissionDenied = permissionStateProvider.isPermissionDenied(permission).first()
            val isPermissionGranted = permissionStateProvider.isPermissionGranted(permission)
            !isPermissionGranted && !isPermissionDenied
        } else {
            false
        }
    }

    /**
     * 检查是否应该显示锁屏设置
     *
     * @return 如果需要设置锁屏功能，返回 true
     */
    private suspend fun shouldDisplayLockscreenSetup(): Boolean {
        return lockScreenService.isSetupRequired().first()
    }

    /**
     * 用户完成会话验证回调
     *
     * 当用户成功完成会话验证后调用此方法，
     * 将 userNeedsToConfirmSessionVerificationSuccess 设置为 false，
     * 允许 FTUE 流程继续进行。
     */
    fun onUserCompletedSessionVerification() {
        // 这里补充关闭 FTUE 前后的关键状态，方便判断 onDone 触发后是否真的推进了 FTUE。
        Timber.tag(startupTraceTag).i(
            "DefaultFtueService.onUserCompletedSessionVerification before userNeedsConfirmation=%s sessionVerifiedStatus=%s ftueState=%s",
            userNeedsToConfirmSessionVerificationSuccess.value,
            sessionVerificationService.sessionVerifiedStatus.value,
            ftueStepStateFlow.value,
        )
        userNeedsToConfirmSessionVerificationSuccess.value = false
        Timber.tag(startupTraceTag).i(
            "DefaultFtueService.onUserCompletedSessionVerification after userNeedsConfirmation=%s sessionVerifiedStatus=%s ftueState=%s",
            userNeedsToConfirmSessionVerificationSuccess.value,
            sessionVerificationService.sessionVerifiedStatus.value,
            ftueStepStateFlow.value,
        )
    }
}

/**
 * FTUE 步骤密封接口
 *
 * 定义首次用户体验流程中的各个步骤。
 * 每个 FtueStep 代表引导流程中的一个独立阶段。
 *
 * 步骤顺序：
 * 1. WaitingForInitialState - 等待初始状态
 * 2. Welcome - 欢迎页面
 * 3. SessionVerification - 会话验证
 * 4. NotificationsOptIn - 通知权限请求
 * 5. AnalyticsOptIn - 分析权限请求（已不再使用）
 * 6. LockscreenSetup - 锁屏设置
 */
sealed interface FtueStep {
    /**
     * 等待初始状态
     *
     * 用于等待 FtueService 初始化完成，确定下一步骤。
     */
    data object WaitingForInitialState : FtueStep

    /**
     * 欢迎页面
     *
     * 显示应用欢迎界面，介绍 NexusTalk 应用。
     */
    data object Welcome : FtueStep

    /**
     * 会话验证
     *
     * 引导用户验证会话安全性，确认设备信任关系。
     */
    data object SessionVerification : FtueStep

    /**
     * 通知权限请求
     *
     * 引导用户选择是否接收应用通知。
     */
    data object NotificationsOptIn : FtueStep

    /**
     * 分析权限请求
     *
     * 引导用户选择是否允许发送匿名使用统计数据以改进产品。
     * 注意：当前版本中此步骤已被跳过，不再显示。
     */
    data object AnalyticsOptIn : FtueStep

    /**
     * 锁屏设置
     *
     * 引导用户设置应用内锁屏功能，增强隐私保护。
     */
    data object LockscreenSetup : FtueStep
}
