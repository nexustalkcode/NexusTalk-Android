/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.login.impl.util.openLearnMorePage
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.matrix.api.auth.OidcDetails

/**
 * 初始页面节点
 *
 * Appyx 节点，用于管理初始页面的生命周期和导航。
 * 包含初始页面的回调接口和参数。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenterFactory Presenter 工厂
 */
@ContributesNode(AppScope::class)
@AssistedInject
class OnBoardingNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: OnBoardingPresenter.Factory,
) : Node(
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 初始页面回调接口
     */
    interface Callback : Plugin {
        /** 导航到注册流程 */
        fun navigateToSignUpFlow()
        /** 导航到登录流程 */
        fun navigateToSignInFlow(mustChooseAccountProvider: Boolean)
        /** 导航到二维码页面 */
        fun navigateToQrCode()
        /** 导航到问题报告 */
        fun navigateToBugReport()
        /** 导航到登录密码页面 */
        fun navigateToLoginPassword()
        /** 导航到 OIDC 页面 */
        fun navigateToOidc(oidcDetails: OidcDetails)
        /** 导航到创建账户页面 */
        fun navigateToCreateAccount(url: String)
        /** 完成事件 */
        fun onDone()
    }

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

    private val callback: Callback = callback()
    private val params = inputs<Params>()

    private val presenter = presenterFactory.create(
        params = params,
    )

    /**
     * 渲染初始页面视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val context = LocalContext.current
        OnBoardingView(
            state = state,
            modifier = modifier,
            onSignIn = callback::navigateToSignInFlow,
            onCreateAccount = callback::navigateToSignUpFlow,
            onSignInWithQrCode = callback::navigateToQrCode,
            onReportProblem = callback::navigateToBugReport,
            onOidcDetails = callback::navigateToOidc,
            onNeedLoginPassword = callback::navigateToLoginPassword,
            onLearnMoreClick = { openLearnMorePage(context) },
            onCreateAccountContinue = callback::navigateToCreateAccount,
            onBackClick = callback::onDone,
        )
    }
}
