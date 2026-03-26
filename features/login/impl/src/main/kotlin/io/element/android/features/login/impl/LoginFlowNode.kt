/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl

import android.app.Activity
import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.api.LoginEntryPoint
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.qrcode.QrCodeLoginFlowNode
import io.element.android.features.login.impl.screens.changeaccountprovider.ChangeAccountProviderNode
import io.element.android.features.login.impl.screens.chooseaccountprovider.ChooseAccountProviderNode
import io.element.android.features.login.impl.screens.confirmaccountprovider.ConfirmAccountProviderNode
import io.element.android.features.login.impl.screens.createaccount.CreateAccountNode
import io.element.android.features.login.impl.screens.loginpassword.LoginPasswordNode
import io.element.android.features.login.impl.screens.onboarding.OnBoardingNode
import io.element.android.features.login.impl.screens.searchaccountprovider.SearchAccountProviderNode
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.oidc.api.OidcAction
import io.element.android.libraries.oidc.api.OidcActionFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * 登录流程节点
 *
 * Appyx FlowNode，管理整个登录流程的导航和状态。
 * 包含从初始页面到各种登录方式（二维码、密码、OIDC等）的完整登录流程。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property accountProviderDataSource 账户提供商数据源
 * @property oidcActionFlow OIDC 操作流程
 * @property appCoroutineScope 应用协程作用域
 */
@ContributesNode(AppScope::class)
@AssistedInject
class LoginFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val oidcActionFlow: OidcActionFlow,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope,
) : BaseFlowNode<LoginFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.OnBoarding,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 节点参数
     *
     * @property accountProvider 账户提供商 URL
     * @property loginHint 登录提示
     */
    data class Params(
        val accountProvider: String?,
        val loginHint: String?,
    ) : NodeInputs

    private val callback: LoginEntryPoint.Callback = callback()
    private var activity: Activity? = null
    private var darkTheme: Boolean = false

    private var externalAppStarted = false

    override fun onBuilt() {
        super.onBuilt()
        lifecycle.subscribe(
            onResume = {
                if (externalAppStarted) {
                    externalAppStarted = false
                    // 检测自定义 Chrome 标签页是否已关闭的解决方案
                    // 如果没有即将执行的 OIDC 操作（这将结束此节点），
                    // 则认为用户通过按返回或关闭自定义 Chrome 标签页取消了登录
                    lifecycleScope.launch {
                        delay(5000)
                        oidcActionFlow.post(OidcAction.GoBack(toUnblock = true))
                    }
                }
            }
        )
    }

    /**
     * 导航目标密封接口
     *
     * 定义登录流程中的各个页面目标。
     */
    sealed interface NavTarget : Parcelable {
        /** 初始页面 */
        @Parcelize
        data object OnBoarding : NavTarget

        /** 二维码登录页面 */
        @Parcelize
        data object QrCode : NavTarget

        /** 确认账户提供商页面 */
        @Parcelize
        data class ConfirmAccountProvider(
            val isAccountCreation: Boolean,
        ) : NavTarget

        /** 选择账户提供商页面 */
        @Parcelize
        data object ChooseAccountProvider : NavTarget

        /** 更改账户提供商页面 */
        @Parcelize
        data object ChangeAccountProvider : NavTarget

        /** 搜索账户提供商页面 */
        @Parcelize
        data object SearchAccountProvider : NavTarget

        /** 登录密码页面 */
        @Parcelize
        data object LoginPassword : NavTarget

        /** 创建账户页面 */
        @Parcelize
        data class CreateAccount(val url: String) : NavTarget
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.OnBoarding -> {
                val callback = object : OnBoardingNode.Callback {
                    override fun navigateToSignUpFlow() {
                        backstack.push(
                            NavTarget.ConfirmAccountProvider(isAccountCreation = true)
                        )
                    }

                    override fun navigateToSignInFlow(mustChooseAccountProvider: Boolean) {
                        backstack.push(
                            if (mustChooseAccountProvider) {
                                NavTarget.ChooseAccountProvider
                            } else {
                                NavTarget.ConfirmAccountProvider(isAccountCreation = false)
                            }
                        )
                    }

                    override fun navigateToQrCode() {
                        backstack.push(NavTarget.QrCode)
                    }

                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun navigateToOidc(oidcDetails: OidcDetails) {
                        navigateToMas(oidcDetails)
                    }

                    override fun navigateToCreateAccount(url: String) {
                        backstack.push(NavTarget.CreateAccount(url))
                    }

                    override fun navigateToLoginPassword() {
                        backstack.push(NavTarget.LoginPassword)
                    }

                    override fun onDone() {
                        callback.onDone()
                    }
                }
                val params = inputs<Params>()
                val inputs = OnBoardingNode.Params(
                    accountProvider = params.accountProvider,
                    loginHint = params.loginHint,
                )
                createNode<OnBoardingNode>(buildContext, listOf(callback, inputs))
            }
            NavTarget.ChooseAccountProvider -> {
                val callback = object : ChooseAccountProviderNode.Callback {
                    override fun navigateToOidc(oidcDetails: OidcDetails) {
                        navigateToMas(oidcDetails)
                    }

                    override fun navigateToCreateAccount(url: String) {
                        backstack.push(NavTarget.CreateAccount(url))
                    }

                    override fun navigateToLoginPassword() {
                        backstack.push(NavTarget.LoginPassword)
                    }
                }
                createNode<ChooseAccountProviderNode>(buildContext, listOf(callback))
            }
            NavTarget.QrCode -> {
                createNode<QrCodeLoginFlowNode>(buildContext)
            }
            is NavTarget.ConfirmAccountProvider -> {
                val inputs = ConfirmAccountProviderNode.Inputs(
                    isAccountCreation = navTarget.isAccountCreation,
                )
                val callback = object : ConfirmAccountProviderNode.Callback {
                    override fun navigateToOidc(oidcDetails: OidcDetails) {
                        navigateToMas(oidcDetails)
                    }

                    override fun navigateToCreateAccount(url: String) {
                        backstack.push(NavTarget.CreateAccount(url))
                    }

                    override fun navigateToLoginPassword() {
                        backstack.push(NavTarget.LoginPassword)
                    }

                    override fun navigateToChangeAccountProvider() {
                        backstack.push(NavTarget.ChangeAccountProvider)
                    }
                }
                createNode<ConfirmAccountProviderNode>(buildContext, plugins = listOf(inputs, callback))
            }
            NavTarget.ChangeAccountProvider -> {
                val callback = object : ChangeAccountProviderNode.Callback {
                    override fun onDone() {
                        // Go back to the Account Provider screen
                        val confirmAccountProvider = backstack.elements.value.firstOrNull {
                            it.key.navTarget is NavTarget.ConfirmAccountProvider
                        }?.key?.navTarget ?: NavTarget.ConfirmAccountProvider(isAccountCreation = false)
                        backstack.singleTop(confirmAccountProvider)
                    }

                    override fun navigateToSearchAccountProvider() {
                        backstack.push(NavTarget.SearchAccountProvider)
                    }
                }

                createNode<ChangeAccountProviderNode>(buildContext, plugins = listOf(callback))
            }
            NavTarget.SearchAccountProvider -> {
                val callback = object : SearchAccountProviderNode.Callback {
                    override fun onDone() {
                        // Go back to the Account Provider screen
                        val confirmAccountProvider = backstack.elements.value.firstOrNull {
                            it.key.navTarget is NavTarget.ConfirmAccountProvider
                        }?.key?.navTarget ?: NavTarget.ConfirmAccountProvider(isAccountCreation = false)
                        backstack.singleTop(confirmAccountProvider)
                    }
                }

                createNode<SearchAccountProviderNode>(buildContext, plugins = listOf(callback))
            }
            NavTarget.LoginPassword -> {
                createNode<LoginPasswordNode>(buildContext)
            }
            is NavTarget.CreateAccount -> {
                val inputs = CreateAccountNode.Inputs(
                    url = navTarget.url,
                )
                createNode<CreateAccountNode>(buildContext, listOf(inputs))
            }
        }
    }

    private fun navigateToMas(oidcDetails: OidcDetails) {
        activity?.let {
            externalAppStarted = true
            it.openUrlInChromeCustomTab(null, darkTheme, oidcDetails.url)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        activity = requireNotNull(LocalActivity.current)
        darkTheme = !ElementTheme.isLightTheme
        DisposableEffect(Unit) {
            onDispose {
                activity = null
                appCoroutineScope.launch {
                    accountProviderDataSource.reset()
                }
            }
        }
        BackstackView()
    }
}
