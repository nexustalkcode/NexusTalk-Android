/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.qrcode

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.replace
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.login.impl.di.QrCodeLoginBindings
import io.element.android.features.login.impl.di.QrCodeLoginGraph
import io.element.android.features.login.impl.screens.qrcode.confirmation.QrCodeConfirmationNode
import io.element.android.features.login.impl.screens.qrcode.confirmation.QrCodeConfirmationStep
import io.element.android.features.login.impl.screens.qrcode.error.QrCodeErrorNode
import io.element.android.features.login.impl.screens.qrcode.intro.QrCodeIntroNode
import io.element.android.features.login.impl.screens.qrcode.scan.QrCodeScanNode
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.DependencyInjectionGraphOwner
import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData
import io.element.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.element.android.libraries.matrix.api.auth.qrlogin.QrLoginException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

/**
 * 二维码登录流程节点
 *
 * 管理整个二维码登录流程的 Appyx FlowNode。
 * 负责导航到二维码扫描、确认、错误等各个子页面。
 * 监听二维码登录状态并根据登录步骤更新界面。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property qrCodeLoginGraphFactory 二维码登录依赖图工厂
 * @property coroutineDispatchers 协程调度器
 * @see QrCodeIntroNode 二维码登录介绍页面
 * @see QrCodeScanNode 二维码扫描页面
 * @see QrCodeConfirmationNode 二维码确认页面
 * @see QrCodeErrorNode 二维码错误页面
 */
@ContributesNode(AppScope::class)
@AssistedInject
class QrCodeLoginFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    qrCodeLoginGraphFactory: QrCodeLoginGraph.Factory,
    private val coroutineDispatchers: CoroutineDispatchers,
) : BaseFlowNode<QrCodeLoginFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Initial,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
), DependencyInjectionGraphOwner {
    private var authenticationJob: Job? = null

    override val graph = qrCodeLoginGraphFactory.create()
    private val qrCodeLoginManager by lazy { bindings<QrCodeLoginBindings>().qrCodeLoginManager() }

    /**
     * 二维码登录导航目标密封接口
     *
     * 定义二维码登录流程中的各个页面目标。
     */
    sealed interface NavTarget : Parcelable {
        /** 初始页面 - 显示二维码登录介绍 */
        @Parcelize
        data object Initial : NavTarget

        /** 二维码扫描页面 */
        @Parcelize
        data object QrCodeScan : NavTarget

        /**
         * 二维码确认页面
         *
         * @property step 确认步骤，包含验证码等信息
         */
        @Parcelize
        data class QrCodeConfirmation(val step: QrCodeConfirmationStep) : NavTarget

        /**
         * 错误页面
         *
         * @property errorType 错误类型
         */
        @Parcelize
        data class Error(val errorType: QrCodeErrorScreenType) : NavTarget
    }

    override fun onBuilt() {
        super.onBuilt()

        observeLoginStep()
    }

    fun isLoginInProgress(): Boolean {
        return authenticationJob?.isActive == true
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun observeLoginStep() {
        lifecycleScope.launch {
            qrCodeLoginManager.currentLoginStep
                .collect { step ->
                    when (step) {
                        is QrCodeLoginStep.EstablishingSecureChannel -> {
                            backstack.replace(NavTarget.QrCodeConfirmation(QrCodeConfirmationStep.DisplayCheckCode(step.checkCode)))
                        }
                        is QrCodeLoginStep.WaitingForToken -> {
                            backstack.replace(NavTarget.QrCodeConfirmation(QrCodeConfirmationStep.DisplayVerificationCode(step.userCode)))
                        }
                        is QrCodeLoginStep.Failed -> {
                            when (val error = step.error) {
                                is QrLoginException.OtherDeviceNotSignedIn -> {
                                    // Do nothing here, it'll be handled in the scan QR screen
                                }
                                is QrLoginException.Cancelled -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.Cancelled))
                                }
                                is QrLoginException.Expired -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.Expired))
                                }
                                is QrLoginException.Declined -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.Declined))
                                }
                                is QrLoginException.ConnectionInsecure -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.InsecureChannelDetected))
                                }
                                is QrLoginException.LinkingNotSupported -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.ProtocolNotSupported))
                                }
                                is QrLoginException.SlidingSyncNotAvailable -> {
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.SlidingSyncNotAvailable))
                                }
                                is QrLoginException.OidcMetadataInvalid -> {
                                    Timber.e(error, "OIDC metadata is invalid")
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.UnknownError))
                                }
                                else -> {
                                    Timber.e(error, "Unknown error found")
                                    backstack.replace(NavTarget.Error(QrCodeErrorScreenType.UnknownError))
                                }
                            }
                        }
                        else -> Unit
                    }
                }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Initial -> {
                val callback = object : QrCodeIntroNode.Callback {
                    override fun cancel() {
                        navigateUp()
                    }

                    override fun navigateToQrCodeScan() {
                        backstack.push(NavTarget.QrCodeScan)
                    }
                }
                createNode<QrCodeIntroNode>(buildContext, plugins = listOf(callback))
            }
            is NavTarget.QrCodeScan -> {
                val callback = object : QrCodeScanNode.Callback {
                    override fun handleScannedCode(qrCodeLoginData: MatrixQrCodeLoginData) {
                        lifecycleScope.startAuthentication(qrCodeLoginData)
                    }

                    override fun cancel() {
                        backstack.pop()
                    }
                }
                createNode<QrCodeScanNode>(buildContext, plugins = listOf(callback))
            }
            is NavTarget.QrCodeConfirmation -> {
                val callback = object : QrCodeConfirmationNode.Callback {
                    override fun onCancel() = reset()
                }
                createNode<QrCodeConfirmationNode>(buildContext, plugins = listOf(navTarget.step, callback))
            }
            is NavTarget.Error -> {
                val callback = object : QrCodeErrorNode.Callback {
                    override fun onRetry() = reset()
                }
                createNode<QrCodeErrorNode>(buildContext, plugins = listOf(navTarget.errorType, callback))
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun reset() {
        authenticationJob?.cancel()
        authenticationJob = null
        qrCodeLoginManager.reset()
        backstack.newRoot(NavTarget.Initial)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun CoroutineScope.startAuthentication(qrCodeLoginData: MatrixQrCodeLoginData) {
        authenticationJob = launch(coroutineDispatchers.main) {
            qrCodeLoginManager.authenticate(qrCodeLoginData)
                .onSuccess {
                    authenticationJob = null
                }
                .onFailure { throwable ->
                    Timber.e(throwable, "QR code authentication failed")
                    authenticationJob = null
                }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }
}

/**
 * 二维码错误屏幕类型密封接口
 *
 * 定义二维码登录过程中可能出现的各种错误类型。
 * 实现 NodeInputs 接口用于节点输入，实现 Parcelable 用于状态保存。
 */
@Immutable
sealed interface QrCodeErrorScreenType : NodeInputs, Parcelable {
    /** 用户取消登录 */
    @Parcelize
    data object Cancelled : QrCodeErrorScreenType

    /** 二维码已过期 */
    @Parcelize
    data object Expired : QrCodeErrorScreenType

    /** 检测到不安全连接 */
    @Parcelize
    data object InsecureChannelDetected : QrCodeErrorScreenType

    /** 对方设备拒绝了登录请求 */
    @Parcelize
    data object Declined : QrCodeErrorScreenType

    /** 协议不支持 */
    @Parcelize
    data object ProtocolNotSupported : QrCodeErrorScreenType

    /** Sliding Sync 不可用 */
    @Parcelize
    data object SlidingSyncNotAvailable : QrCodeErrorScreenType

    /** 未知错误 */
    @Parcelize
    data object UnknownError : QrCodeErrorScreenType
}
