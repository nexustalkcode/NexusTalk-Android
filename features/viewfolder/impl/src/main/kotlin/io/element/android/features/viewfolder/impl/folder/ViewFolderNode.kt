/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.viewfolder.impl.model.Item
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs

/**
 * 文件夹视图节点
 *
 * 表示文件夹浏览的页面节点，负责连接 Presenter 和 View。
 * 继承自 Node，作为 Appyx 导航体系中的页面组件。
 *
 * @property Inputs 输入数据类，包含是否可返回和路径信息
 * @property Callback 回调接口，处理用户交互事件
 * @see ViewFolderPresenter 文件夹视图 Presenter
 * @see ViewFolderView 文件夹视图组件
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ViewFolderNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ViewFolderPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入数据
     *
     * @property canGoUp 是否可以返回上一级
     * @property path 当前浏览路径
     */
    data class Inputs(
        val canGoUp: Boolean,
        val path: String,
    ) : NodeInputs

    /**
     * 文件夹节点回调接口
     */
    interface Callback : Plugin {
        /**
         * 返回按钮点击
         */
        fun onBackClick()
        /**
         * 导航到项目
         *
         * @param item 要导航到的项目
         */
        fun navigateToItem(item: Item)
    }

    private val callback: Callback = callback()
    private val inputs: Inputs = inputs()

    private val presenter = presenterFactory.create(
        canGoUp = inputs.canGoUp,
        path = inputs.path,
    )

    /**
     * 渲染文件夹视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        ViewFolderView(
            state = state,
            modifier = modifier,
            onNavigateTo = callback::navigateToItem,
            onBackClick = callback::onBackClick,
        )
    }
}
