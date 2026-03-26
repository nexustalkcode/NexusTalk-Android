/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.licenses.impl.details.DependenciesDetailsNode
import io.element.android.features.licenses.impl.list.DependencyLicensesListNode
import io.element.android.features.licenses.impl.model.DependencyLicenseItem
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.createNode
import kotlinx.parcelize.Parcelize

/**
 * 依赖项许可流程节点
 *
 * 作为开源许可证功能的主节点，管理许可证列表和详情页的导航流程。
 * 使用 BackStack 实现页面导航，支持在列表和详情之间切换。
 *
 * @property NavTarget 导航目标类型，包含列表和详情两种状态
 * @see DependencyLicensesListNode 许可证列表节点
 * @see DependenciesDetailsNode 许可证详情节点
 */
@ContributesNode(AppScope::class)
@AssistedInject
class DependenciesFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : BaseFlowNode<DependenciesFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.LicensesList,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 导航目标密封类
     *
     * 定义许可证查看流程中的各种导航状态：
     * - LicensesList: 许可证列表页
     * - LicenseDetails: 许可证详情页
     */
    sealed interface NavTarget : Parcelable {
        /** 许可证列表目标 */
        @Parcelize
        data object LicensesList : NavTarget

        /**
         * 许可证详情目标
         *
         * @property license 依赖项许可证信息
         */
        @Parcelize
        data class LicenseDetails(val license: DependencyLicenseItem) : NavTarget
    }

    /**
     * 解析导航目标并创建对应的节点
     *
     * @param navTarget 导航目标
     * @param buildContext 构建上下文
     * @return 解析后的节点实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.LicensesList -> {
                val callback = object : DependencyLicensesListNode.Callback {
                    override fun navigateToLicense(license: DependencyLicenseItem) {
                        backstack.push(NavTarget.LicenseDetails(license))
                    }
                }
                createNode<DependencyLicensesListNode>(buildContext, listOf(callback))
            }
            is NavTarget.LicenseDetails -> {
                createNode<DependenciesDetailsNode>(buildContext, listOf(DependenciesDetailsNode.Inputs(navTarget.license)))
            }
        }
    }

    /**
     * 渲染流程节点的视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        BackstackView(modifier)
    }
}
