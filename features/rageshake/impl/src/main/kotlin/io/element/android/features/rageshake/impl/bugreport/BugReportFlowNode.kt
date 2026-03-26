/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.bugreport

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
import io.element.android.features.rageshake.api.bugreport.BugReportEntryPoint
import io.element.android.features.viewfolder.api.ViewFolderEntryPoint
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import kotlinx.parcelize.Parcelize

/**
 * 问题报告流程节点
 *
 * 管理问题报告功能的导航流程，包括主表单页面和日志查看页面。
 * 使用 BaseFlowNode 作为基类，支持页面间的导航。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property viewFolderEntryPoint 日志文件夹查看入口点
 */
@ContributesNode(AppScope::class)
@AssistedInject
class BugReportFlowNode(
    @Assisted val buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val viewFolderEntryPoint: ViewFolderEntryPoint,
) : BaseFlowNode<BugReportFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    private val callback: BugReportEntryPoint.Callback = callback()

    /**
     * 导航目标密封接口
     *
     * 定义问题报告流程中的各个导航目标。
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 根页面 - 问题报告表单
         *
         * 问题报告的主页面，用户可以填写问题描述和选择附件。
         */
        @Parcelize
        data object Root : NavTarget

        /**
         * 日志查看页面
         *
         * 用于查看设备日志的页面。
         *
         * @param rootPath 日志目录的根路径
         */
        @Parcelize
        data class ViewLogs(
            val rootPath: String,
        ) : NavTarget
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                val callback = object : BugReportNode.Callback {
                    override fun onDone() {
                        callback.onDone()
                    }

                    override fun navigateToViewLogs(basePath: String) {
                        backstack.push(NavTarget.ViewLogs(rootPath = basePath))
                    }
                }
                createNode<BugReportNode>(buildContext, listOf(callback))
            }
            is NavTarget.ViewLogs -> {
                val callback = object : ViewFolderEntryPoint.Callback {
                    override fun onDone() {
                        backstack.pop()
                    }
                }
                val params = ViewFolderEntryPoint.Params(
                    rootPath = navTarget.rootPath,
                )
                viewFolderEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }
}
