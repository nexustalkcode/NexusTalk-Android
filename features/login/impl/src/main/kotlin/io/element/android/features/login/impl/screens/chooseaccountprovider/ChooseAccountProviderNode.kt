/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.chooseaccountprovider

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
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.matrix.api.auth.OidcDetails

/**
 * 选择账户提供商节点
 *
 * 允许用户选择要登录的账户提供商的页面节点。
 * 根据服务器支持的认证方式导航到相应的登录流程。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenter 业务逻辑 presenter
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ChooseAccountProviderNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: ChooseAccountProviderPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 选择账户提供商回调接口
     */
    interface Callback : Plugin {
        /** 导航到密码登录页面 */
        fun navigateToLoginPassword()
        /** 导航到 OIDC 登录 */
        fun navigateToOidc(oidcDetails: OidcDetails)
        /** 导航到账户创建页面 */
        fun navigateToCreateAccount(url: String)
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val context = LocalContext.current
        ChooseAccountProviderView(
            state = state,
            modifier = modifier,
            onBackClick = ::navigateUp,
            onOidcDetails = callback::navigateToOidc,
            onNeedLoginPassword = callback::navigateToLoginPassword,
            onLearnMoreClick = { openLearnMorePage(context) },
            onCreateAccountContinue = callback::navigateToCreateAccount,
        )
    }
}
