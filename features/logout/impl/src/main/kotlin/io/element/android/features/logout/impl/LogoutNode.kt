/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
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
import io.element.android.features.logout.api.LogoutEntryPoint
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

/**
 * 退出登录节点
 *
 * 继承自 Node，是退出登录功能的入口组件。
 * 负责组合 presenter 和 view，渲染退出登录界面。
 *
 * @property buildContext 构建上下文，包含节点创建所需的信息
 * @property plugins 插件列表
 * @property presenter 退出登录界面逻辑控制器
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class LogoutNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: LogoutPresenter,
) : Node(buildContext, plugins = plugins) {
    // 退出登录流程回调接口实例
    private val callback: LogoutEntryPoint.Callback = callback()

    /**
     * 渲染退出登录界面视图
     *
     * @param modifier 样式修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 从 presenter 获取当前状态
        val state = presenter.present()
        LogoutView(
            state = state,
            onChangeRecoveryKeyClick = callback::navigateToSecureBackup,
            onBackClick = ::navigateUp,
            modifier = modifier,
        )
    }
}
