/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl

import android.app.Activity
import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.replace
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.linknewdevice.api.LinkNewDeviceEntryPoint
import io.element.android.features.linknewdevice.impl.screens.desktop.DesktopNoticeNode
import io.element.android.features.linknewdevice.impl.screens.error.ErrorNode
import io.element.android.features.linknewdevice.impl.screens.error.ErrorScreenType
import io.element.android.features.linknewdevice.impl.screens.number.EnterNumberNode
import io.element.android.features.linknewdevice.impl.screens.qrcode.ShowQrCodeNode
import io.element.android.features.linknewdevice.impl.screens.root.LinkNewDeviceRootNode
import io.element.android.features.linknewdevice.impl.screens.scan.ScanQrCodeNode
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.linknewdevice.ErrorType
import io.element.android.libraries.matrix.api.linknewdevice.LinkDesktopStep
import io.element.android.libraries.matrix.api.linknewdevice.LinkMobileStep
import io.element.android.libraries.matrix.api.logs.LoggerTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import timber.log.Timber

private val tag = LoggerTag("LinkNewDeviceFlowNode", LoggerTags.linkNewDevice)

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 新设备关联总流程节点。
 *
 * 该节点统一编排“关联移动端”和“关联桌面端”两套子流程，
 * 并负责把底层 handler 的步骤状态映射为 Appyx back stack 上的具体页面。
 */
class LinkNewDeviceFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val linkNewMobileHandler: LinkNewMobileHandler,
    private val linkNewDesktopHandler: LinkNewDesktopHandler,
) : BaseFlowNode<LinkNewDeviceFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    private val callback: LinkNewDeviceEntryPoint.Callback = callback()
    private var activity: Activity? = null
    private var darkTheme: Boolean = false

    /**
     * 在节点构建完成后建立对移动端和桌面端 handler 的状态订阅。
     */
    override fun onBuilt() {
        super.onBuilt()
        // 保存协程 Job，便于生命周期结束时取消
        var linkMobileHandlerJob: Job? = null
        var linkDesktopHandlerJob: Job? = null

        lifecycle.subscribe(
            onCreate = {
                // 每次进入流程时重置处理器，确保从干净状态开始
                linkNewMobileHandler.reset()
                linkNewDesktopHandler.reset()
                @Suppress("AssignedValueIsNeverRead")
                // 监听移动端流程状态
                linkMobileHandlerJob = observeLinkNewMobileHandler()
                @Suppress("AssignedValueIsNeverRead")
                // 监听桌面端流程状态
                linkDesktopHandlerJob = observeLinkNewDesktopHandler()
            },
            onDestroy = {
                // 避免生命周期结束后继续收集状态
                linkMobileHandlerJob?.cancel()
                linkDesktopHandlerJob?.cancel()
            }
        )
    }

    /**
     * 流程中会出现的导航目标。
     */
    sealed interface NavTarget : Parcelable {
        // 根节点：显示设备类型选择或不支持提示
        @Parcelize
        data object Root : NavTarget

        // 移动端：展示二维码
        @Parcelize
        data class MobileShowQrCode(
            val data: String,
        ) : NavTarget

        // 移动端：输入校验码
        @Parcelize
        data object MobileEnterNumber : NavTarget

        // 桌面端：提示说明页
        @Parcelize
        data object DesktopNotice : NavTarget

        // 桌面端：扫码页
        @Parcelize
        data object DesktopScanQrCode : NavTarget

        // 错误页
        @Parcelize
        data class Error(
            val errorScreenType: ErrorScreenType,
        ) : NavTarget
    }

    /**
     * 监听移动端关联流程的状态变化，并驱动页面跳转。
     */
    private fun observeLinkNewMobileHandler(): Job {
        Timber.tag(tag.value).d("startObservingLinkNewMobileHandler")
        return linkNewMobileHandler.stepFlow
            .onEach { linkMobileStep ->
                Timber.tag(tag.value).d("step: ${linkMobileStep::class.java.simpleName}")
                when (linkMobileStep) {
                    LinkMobileStep.Uninitialized -> Unit
                    LinkMobileStep.Done -> {
                        // 流程完成，通知上层关闭
                        callback.onDone()
                    }
                    is LinkMobileStep.Error -> {
                        // 移动端流程出现错误，跳转错误页
                        navigateToError(linkMobileStep.errorType)
                    }
                    is LinkMobileStep.QrReady -> {
                        // 二维码准备好后展示给用户
                        backstack.push(NavTarget.MobileShowQrCode(linkMobileStep.data))
                    }
                    is LinkMobileStep.QrScanned -> {
                        // 二维码已被桌面端扫到，进入输入校验码流程
                        backstack.replace(NavTarget.MobileEnterNumber)
                    }
                    LinkMobileStep.Starting -> {
                        // 目前不会收到该状态，保持不变
                    }
                    LinkMobileStep.SyncingSecrets -> {
                        // 当前没有收到 Done，视为完成
                        callback.onDone()
                    }
                    is LinkMobileStep.WaitingForAuth -> {
                        // 需要用户在浏览器完成验证
                        navigateToBrowser(linkMobileStep.verificationUri)
                    }
                }
            }
            .launchIn(sessionCoroutineScope)
    }

    /**
     * 监听桌面端关联流程的状态变化，并驱动页面跳转。
     */
    private fun observeLinkNewDesktopHandler(): Job {
        Timber.tag(tag.value).d("startObservingLinkNewDesktopHandler")
        return linkNewDesktopHandler.stepFlow.onEach { linkDesktopStep ->
            Timber.tag(tag.value).d("step: ${linkDesktopStep::class.java.simpleName}")
            when (linkDesktopStep) {
                LinkDesktopStep.Done -> callback.onDone()
                is LinkDesktopStep.Error -> {
                    // 桌面端流程错误，显示错误页
                    navigateToError(linkDesktopStep.errorType)
                }
                is LinkDesktopStep.EstablishingSecureChannel -> Unit
                is LinkDesktopStep.InvalidQrCode -> {
                    // 扫码页自身会处理该错误
                }
                LinkDesktopStep.Starting -> Unit
                LinkDesktopStep.SyncingSecrets -> Unit
                LinkDesktopStep.Uninitialized -> Unit
                is LinkDesktopStep.WaitingForAuth -> {
                    // 需要用户在浏览器完成授权
                    navigateToBrowser(linkDesktopStep.verificationUri)
                }
            }
        }
            .launchIn(sessionCoroutineScope)
    }

    /**
     * 把底层 SDK 的错误类型映射为 UI 层使用的错误页类型。
     *
     * @param errorType SDK 返回的错误类型。
     */
    private fun navigateToError(errorType: ErrorType) {
        // 将底层错误映射到 UI 错误页类型
        // TODO Update this mapping
        val error = when (errorType) {
            is ErrorType.DeviceIdAlreadyInUse -> ErrorScreenType.UnknownError
            is ErrorType.InvalidCheckCode -> ErrorScreenType.InsecureChannelDetected
            is ErrorType.MissingSecretsBackup -> ErrorScreenType.UnknownError
            is ErrorType.NotFound -> ErrorScreenType.Expired
            is ErrorType.UnableToCreateDevice -> ErrorScreenType.UnknownError
            is ErrorType.Unknown -> ErrorScreenType.UnknownError
            is ErrorType.UnsupportedProtocol -> ErrorScreenType.UnknownError
        }
        // 直接压栈错误页即可，退出错误页时会重置为新根
        backstack.push(NavTarget.Error(error))
    }

    /**
     * 根据导航目标创建对应子节点。
     *
     * @param navTarget 当前需要解析的导航目标。
     * @param buildContext 子节点的构建上下文。
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                val callback = object : LinkNewDeviceRootNode.Callback {
                    override fun onDone() {
                        // 根页完成，直接透传到入口回调
                        callback.onDone()
                    }

                    override fun linkDesktopDevice() {
                        // 切到桌面端流程前先重置处理器
                        linkNewDesktopHandler.reset()
                        backstack.push(NavTarget.DesktopNotice)
                    }
                }
                createNode<LinkNewDeviceRootNode>(buildContext, listOf(callback))
            }
            NavTarget.DesktopNotice -> {
                val callback = object : DesktopNoticeNode.Callback {
                    override fun navigateBack() {
                        // 返回上一层
                        backstack.pop()
                    }

                    override fun navigateToQrCodeScanner() {
                        // 进入桌面端扫码页
                        backstack.push(NavTarget.DesktopScanQrCode)
                    }
                }
                createNode<DesktopNoticeNode>(buildContext, listOf(callback))
            }
            NavTarget.DesktopScanQrCode -> {
                val callback = object : ScanQrCodeNode.Callback {
                    override fun cancel() {
                        // 取消扫码，回到上一页
                        backstack.pop()
                    }
                }
                createNode<ScanQrCodeNode>(buildContext, listOf(callback))
            }
            NavTarget.MobileEnterNumber -> {
                val callback = object : EnterNumberNode.Callback {
                    override fun navigateToWrongNumberError() {
                        // 输入校验码错误，展示不匹配错误页
                        backstack.push(NavTarget.Error(ErrorScreenType.Mismatch2Digits))
                    }

                    override fun navigateBack() {
                        // 返回上一层
                        backstack.pop()
                    }
                }
                createNode<EnterNumberNode>(buildContext, listOf(callback))
            }
            is NavTarget.MobileShowQrCode -> {
                val callback = object : ShowQrCodeNode.Callback {
                    override fun navigateBack() {
                        // 退出二维码页时重置移动端处理器
                        linkNewMobileHandler.reset()
                        backstack.pop()
                    }
                }
                val inputs = ShowQrCodeNode.Inputs(
                    data = navTarget.data,
                )
                createNode<ShowQrCodeNode>(buildContext, listOf(inputs, callback))
            }
            is NavTarget.Error -> {
                val callback = object : ErrorNode.Callback {
                    override fun onRetry() {
                        // 重试时清空两个处理器并回到根页
                        linkNewMobileHandler.reset()
                        linkNewDesktopHandler.reset()
                        backstack.newRoot(NavTarget.Root)
                    }
                }
                createNode<ErrorNode>(buildContext, listOf(callback, navTarget.errorScreenType))
            }
        }
    }

    /**
     * 在浏览器中打开关联流程要求访问的授权链接。
     *
     * @param url 需要打开的授权地址。
     */
    private fun navigateToBrowser(url: String) {
        // 使用自定义标签页打开验证链接
        activity?.openUrlInChromeCustomTab(null, darkTheme, url)
    }

    /**
     * 渲染当前流程的 back stack。
     *
     * @param modifier 应用于根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 记录 Activity 引用和当前主题，用于打开浏览器
        activity = requireNotNull(LocalActivity.current)
        darkTheme = !ElementTheme.isLightTheme
        DisposableEffect(Unit) {
            onDispose {
                // 避免持有失效的 Activity
                activity = null
            }
        }
        BackstackView()
    }
}
