/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.root

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.viewfolder.api.ViewFolderEntryPoint
import io.element.android.features.viewfolder.impl.file.ViewFileNode
import io.element.android.features.viewfolder.impl.folder.ViewFolderNode
import io.element.android.features.viewfolder.impl.model.Item
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.inputs
import kotlinx.parcelize.Parcelize

/**
 * 文件夹浏览流程节点
 *
 * 作为文件夹浏览功能的主节点，管理文件夹和文件查看的导航流程。
 * 使用 BackStack 实现页面导航，支持在文件夹层级间导航和查看文件内容。
 *
 * @property NavTarget 导航目标类型，包含根目录、文件夹和文件三种状态
 * @property Inputs 输入数据类，包含浏览的根路径
 * @see ViewFolderNode 文件夹浏览节点
 * @see ViewFileNode 文件查看节点
 */
@ContributesNode(AppScope::class)
@AssistedInject
class ViewFolderFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : BaseFlowNode<ViewFolderFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 导航目标密封类
     *
     * 定义文件夹浏览流程中的各种导航状态：
     * - Root: 初始根目录
     * - Folder: 文件夹导航
     * - File: 文件查看
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 根目录目标
         */
        @Parcelize
        data object Root : NavTarget

        /**
         * 文件夹导航目标
         *
         * @property path 文件夹路径
         */
        @Parcelize
        data class Folder(
            val path: String,
        ) : NavTarget

        /**
         * 文件查看目标
         *
         * @property path 文件路径
         * @property name 文件名称
         */
        @Parcelize
        data class File(
            val path: String,
            val name: String,
        ) : NavTarget
    }

    /**
     * 节点输入数据
     *
     * @property rootPath 浏览的根路径
     */
    data class Inputs(
        val rootPath: String,
    ) : NodeInputs

    private val callback: ViewFolderEntryPoint.Callback = callback()
    private val inputs: Inputs = inputs()

    /**
     * 解析导航目标并创建对应的节点
     *
     * @param navTarget 导航目标
     * @param buildContext 构建上下文
     * @return 解析后的节点实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Root -> {
                createViewFolderNode(
                    buildContext,
                    inputs = ViewFolderNode.Inputs(
                        canGoUp = false,
                        path = inputs.rootPath,
                    )
                )
            }
            is NavTarget.Folder -> {
                createViewFolderNode(
                    buildContext,
                    inputs = ViewFolderNode.Inputs(
                        canGoUp = true,
                        path = navTarget.path,
                    )
                )
            }
            is NavTarget.File -> {
                val callback: ViewFileNode.Callback = object : ViewFileNode.Callback {
                    override fun onBackClick() {
                        backstack.pop()
                    }
                }
                val inputs = ViewFileNode.Inputs(
                    path = navTarget.path,
                    name = navTarget.name,
                )
                createNode<ViewFileNode>(buildContext, plugins = listOf(inputs, callback))
            }
        }
    }

    /**
     * 创建文件夹节点
     *
     * @param buildContext 构建上下文
     * @param inputs 节点输入数据
     * @return ViewFolderNode 实例
     */
    private fun createViewFolderNode(
        buildContext: BuildContext,
        inputs: ViewFolderNode.Inputs,
    ): Node {
        val callback: ViewFolderNode.Callback = object : ViewFolderNode.Callback {
            override fun onBackClick() {
                callback.onDone()
            }

            override fun navigateToItem(item: Item) {
                when (item) {
                    Item.Parent -> {
                        // Should not happen when in Root since parent is not accessible from root (canGoUp set to false)
                        backstack.pop()
                    }
                    is Item.Folder -> {
                        backstack.push(NavTarget.Folder(path = item.path))
                    }
                    is Item.File -> {
                        backstack.push(NavTarget.File(path = item.path, name = item.name))
                    }
                }
            }
        }
        return createNode<ViewFolderNode>(buildContext, plugins = listOf(inputs, callback))
    }

    /**
     * 渲染流程节点的视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }
}
