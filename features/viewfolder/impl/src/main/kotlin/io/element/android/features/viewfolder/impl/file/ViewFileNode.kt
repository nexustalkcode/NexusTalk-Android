/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs

/**
 * 文件查看节点
 *
 * 表示文件查看的页面节点，负责连接 Presenter 和 View。
 * 继承自 Node，作为 Appyx 导航体系中的页面组件。
 *
 * @property Inputs 输入数据类，包含文件路径和名称
 * @property Callback 回调接口，处理用户交互事件
 * @see ViewFilePresenter 文件查看 Presenter
 * @see ViewFileView 文件查看组件
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ViewFileNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ViewFilePresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入数据
     *
     * @property path 文件路径
     * @property name 文件名称
     */
    data class Inputs(
        val path: String,
        val name: String,
    ) : NodeInputs

    /**
     * 文件节点回调接口
     */
    interface Callback : Plugin {
        /**
         * 返回按钮点击
         */
        fun onBackClick()
    }

    private val callback: Callback = callback()
    private val inputs: Inputs = inputs()

    private val presenter = presenterFactory.create(
        path = inputs.path,
        name = inputs.name,
    )

    /**
     * 渲染文件视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        ViewFileView(
            state = state,
            modifier = modifier,
            onBackClick = callback::onBackClick,
        )
    }
}
