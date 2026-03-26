/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.licenses.impl.model.DependencyLicenseItem
import io.element.android.libraries.architecture.callback

/**
 * 依赖项许可证列表节点
 *
 * 表示许可证列表的页面节点，负责连接 Presenter 和 View。
 * 继承自 Node，作为 Appyx 导航体系中的页面组件。
 *
 * @property Callback 回调接口，处理导航到许可证详情事件
 * @see DependencyLicensesListPresenter 许可证列表 Presenter
 * @see DependencyLicensesListView 许可证列表视图
 */
@ContributesNode(AppScope::class)
@AssistedInject
class DependencyLicensesListNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: DependencyLicensesListPresenter,
) : Node(
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 列表节点回调接口
     */
    interface Callback : Plugin {
        /**
         * 导航到许可证详情
         *
         * @param license 要查看的许可证项目
         */
        fun navigateToLicense(license: DependencyLicenseItem)
    }

    private val callback: Callback = callback()

    /**
     * 渲染许可证列表视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        DependencyLicensesListView(
            state = state,
            onBackClick = ::navigateUp,
            onOpenLicense = callback::navigateToLicense,
        )
    }
}
