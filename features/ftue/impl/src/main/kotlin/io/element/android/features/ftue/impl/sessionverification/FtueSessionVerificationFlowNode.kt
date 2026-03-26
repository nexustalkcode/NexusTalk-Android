/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModeNode
import io.element.android.features.securebackup.api.SecureBackupEntryPoint
import io.element.android.features.verifysession.api.OutgoingVerificationEntryPoint
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.designsystem.utils.OpenUrlInTabView
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.verification.VerificationRequest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * FTUE 会话验证流程节点
 *
 * 这是首次用户体验中会话验证流程的根节点，继承自 BaseFlowNode。
 * 使用 BackStack 导航模型管理验证流程中的各个步骤。
 *
 * 主要职责：
 * - 管理会话验证的导航流程
 * - 提供多种验证方式供用户选择（使用另一台设备、输入恢复密钥、重置身份）
 * - 处理验证完成后的回调
 *
 * 验证流程步骤：
 * 1. Root：选择验证方式页面
 * 2. UseAnotherDevice：使用另一台设备验证
 * 3. EnterRecoveryKey：输入恢复密钥
 * 4. ResetIdentity：重置身份
 *
 * @param buildContext 构建上下文
 * @param plugins 插件列表
 * @param outgoingVerificationEntryPoint 外出验证入口点
 * @param secureBackupEntryPoint 安全备份入口点
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class FtueSessionVerificationFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val outgoingVerificationEntryPoint: OutgoingVerificationEntryPoint,
    private val secureBackupEntryPoint: SecureBackupEntryPoint,
) : BaseFlowNode<FtueSessionVerificationFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 会话验证流程导航目标密封接口
     *
     * 定义会话验证流程中的各个导航步骤。
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 根节点 - 选择验证方式页面
         *
         * 用户可以选择使用另一台设备、输入恢复密钥或重置身份进行验证。
         */
        @Parcelize
        data object Root : NavTarget

        /**
         * 使用另一台设备验证
         *
         * 引导用户使用其他已登录的设备完成验证。
         */
        @Parcelize
        data object UseAnotherDevice : NavTarget

        /**
         * 输入恢复密钥验证
         *
         * 引导用户输入之前保存的恢复密钥完成验证。
         */
        @Parcelize
        data object EnterRecoveryKey : NavTarget

        /**
         * 重置身份
         *
         * 允许用户在无法完成验证时重置身份。
         */
        @Parcelize
        data object ResetIdentity : NavTarget
    }

    /**
     * 会话验证完成回调接口
     *
     * 定义会话验证流程完成后的回调方法。
     */
    interface Callback : Plugin {
        fun onBack()

        /**
         * 验证完成回调
         *
         * 当用户成功完成会话验证后调用。
         */
        fun onDone()
    }

    private val callback: Callback = callback()

    private val secureBackupEntryPointCallback = object : SecureBackupEntryPoint.Callback {
        override fun onDone() {
            lifecycleScope.launch {
                // Move to the completed state view in the verification flow
                backstack.newRoot(NavTarget.UseAnotherDevice)
            }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Root -> {
                val callback = object : ChooseSelfVerificationModeNode.Callback {
                    override fun onBack() {
                        callback.onBack()
                    }

                    override fun navigateToUseAnotherDevice() {
                        backstack.push(NavTarget.UseAnotherDevice)
                    }

                    override fun navigateToUseRecoveryKey() {
                        backstack.push(NavTarget.EnterRecoveryKey)
                    }

                    override fun navigateToResetKey() {
                        backstack.push(NavTarget.ResetIdentity)
                    }

                    override fun navigateToLearnMoreAboutEncryption() {
                        learnMoreUrl.value = LearnMoreConfig.DEVICE_VERIFICATION_URL
                    }
                }
                createNode<ChooseSelfVerificationModeNode>(buildContext, plugins = listOf(callback))
            }
            is NavTarget.UseAnotherDevice -> {
                outgoingVerificationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = OutgoingVerificationEntryPoint.Params(
                        showDeviceVerifiedScreen = true,
                        verificationRequest = VerificationRequest.Outgoing.CurrentSession,
                    ),
                    callback = object : OutgoingVerificationEntryPoint.Callback {
                        override fun onDone() {
                            callback.onDone()
                        }

                        override fun onBack() {
                            backstack.pop()
                        }

                        override fun navigateToLearnMoreAboutEncryption() {
                            // Note that this callback is never called. The "Learn more" link is not displayed
                            // for the self session interactive verification.
                        }
                    }
                )
            }
            is NavTarget.EnterRecoveryKey -> {
                secureBackupEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = SecureBackupEntryPoint.Params(SecureBackupEntryPoint.InitialTarget.EnterRecoveryKey),
                    callback = secureBackupEntryPointCallback
                )
            }
            is NavTarget.ResetIdentity -> {
                secureBackupEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = SecureBackupEntryPoint.Params(SecureBackupEntryPoint.InitialTarget.ResetIdentity),
                    callback = object : SecureBackupEntryPoint.Callback {
                        override fun onDone() {
                            callback.onDone()
                        }
                    },
                )
            }
        }
    }

    private val learnMoreUrl = mutableStateOf<String?>(null)

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()

        OpenUrlInTabView(learnMoreUrl)
    }
}
