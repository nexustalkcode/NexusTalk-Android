/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

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
 * 账户提供商确认页面的 Appyx 节点
 *
 * 作为该页面的入口节点，继承了 Appyx 框架的 [Node] 基类，
 * 负责协调 Presenter、View 和 Callback 之间的交互
 *
 * 节点职责：
 * - 通过依赖注入获取 Presenter 实例
 * - 接收外部传入的节点输入参数（Inputs）
 * - 获取父节点提供的回调接口（Callback）
 * - 组合并渲染 [ConfirmAccountProviderView] UI 组件
 *
 * 架构位置：
 * [LoginFlowNode] -> [ConfirmAccountProviderNode] -> [ConfirmAccountProviderView]
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ConfirmAccountProviderNode(
    /** Appyx 框架构建上下文，包含节点构建所需信息 */
    @Assisted buildContext: BuildContext,
    /** 插件列表，用于接收父节点传递的回调等插件 */
    @Assisted plugins: List<Plugin>,
    /** Presenter 工厂，用于创建该页面的 Presenter 实例 */
    presenterFactory: ConfirmAccountProviderPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入参数数据类
     *
     * 定义了该节点需要从父节点接收的配置信息，
     * 实现 [NodeInputs] 接口以支持 Appyx 的输入机制
     */
    data class Inputs(
        /** 标识当前是否为账户创建（注册）流程 */
        val isAccountCreation: Boolean,
    ) : NodeInputs

    /** 从父节点获取的输入参数 */
    private val inputs: Inputs = inputs()
    /** 创建 Presenter 实例，传入所需的配置参数 */
    private val presenter = presenterFactory.create(
        ConfirmAccountProviderPresenter.Params(
            isAccountCreation = inputs.isAccountCreation,
        )
    )

    /**
     * 节点回调接口
     *
     * 定义了该节点可能触发的导航事件，
     * 实现 [Plugin] 接口以支持 Appyx 的插件机制
     *
     * 回调事件：
     * - 导航到登录密码页面
     * - 导航到 OIDC 认证页面
     * - 导航到创建账户页面
     * - 导航到更改提供商页面
     */
    interface Callback : Plugin {
        /** 导航到需要输入密码的传统登录页面 */
        fun navigateToLoginPassword()
        /** 导航到 OIDC（OpenID Connect）认证页面 */
        fun navigateToOidc(oidcDetails: OidcDetails)
        /** 导航到创建账户页面，携带提供商 URL */
        fun navigateToCreateAccount(url: String)
        /** 导航到更改账户提供商选择页面 */
        fun navigateToChangeAccountProvider()
    }

    /** 从父节点获取的回调实例 */
    private val callback: Callback = callback()

    /**
     * 渲染页面视图
     *
     * 实现父类的 [View] 方法，
     * 在此方法中组合 Presenter 和 View：
     * 1. 调用 Presenter 获取页面状态
     * 2. 获取 Android Context 用于打开链接
     * 3. 渲染 ConfirmAccountProviderView 组件
     *
     * @param modifier 修饰符，用于自定义布局样式
     */
    @Composable
    override fun View(modifier: Modifier) {
        /** 获取由 Presenter 生成的页面状态 */
        val state = presenter.present()
        /** 获取 Android Context，用于打开外部链接等操作 */
        val context = LocalContext.current
        /** 渲染账户提供商确认视图 */
        ConfirmAccountProviderView(
            state = state,
            modifier = modifier,
            /** 绑定 OIDC 详情回调 */
            onOidcDetails = callback::navigateToOidc,
            /** 绑定需要密码登录回调 */
            onNeedLoginPassword = callback::navigateToLoginPassword,
            /** 绑定创建账户继续回调 */
            onCreateAccountContinue = callback::navigateToCreateAccount,
            /** 绑定更改提供商回调 */
            onChange = callback::navigateToChangeAccountProvider,
            /** 绑定了解更多点击事件，打开帮助页面 */
            onLearnMoreClick = { openLearnMorePage(context) },
        )
    }
}
