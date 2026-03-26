/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.changeaccountprovider

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

/**
 * 更改账户提供商节点
 *
 * 允许用户更改所选账户提供商（homeserver）的页面节点。
 * 负责管理页面的生命周期和视图渲染。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenter 业务逻辑 presenter
 * @see ChangeAccountProviderView 页面视图
 * @see ChangeAccountProviderPresenter 业务逻辑处理
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ChangeAccountProviderNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: ChangeAccountProviderPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 更改账户提供商回调接口
     */
    interface Callback : Plugin {
        /** 完成时的回调 */
        fun onDone()
        /** 导航到搜索账户提供商页面 */
        fun navigateToSearchAccountProvider()
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val context = LocalContext.current
        ChangeAccountProviderView(
            state = state,
            modifier = modifier,
            onBackClick = ::navigateUp,
            onLearnMoreClick = { openLearnMorePage(context) },
            onSuccess = callback::onDone,
            onOtherProviderClick = callback::navigateToSearchAccountProvider,
        )
    }
}
