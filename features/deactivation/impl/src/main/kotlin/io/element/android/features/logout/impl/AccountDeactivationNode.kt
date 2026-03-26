/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.di.SessionScope

/**
 * 账户停用界面节点
 *
 * 继承自 Node，是账户停用功能的根节点。
 * 负责协调 Presenter 和 View，呈现账户停用界面。
 *
 * @property presenter 账户停用Presenter，负责业务逻辑处理
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class AccountDeactivationNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: AccountDeactivationPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 渲染账户停用界面
     *
     * @param modifier 界面修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 获取Presenter生成的状态
        val state = presenter.present()
        // 渲染账户停用视图
        AccountDeactivationView(
            state = state,
            onBackClick = ::navigateUp,
            modifier = modifier,
        )
    }
}
