/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.details

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
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs

/**
 * 依赖项许可证详情节点
 *
 * 表示许可证详情页面节点，负责展示单个依赖项的许可证信息。
 * 继承自 Node，作为 Appyx 导航体系中的页面组件。
 *
 * @property Inputs 输入数据类，包含许可证项目信息
 * @see DependenciesDetailsView 许可证详情视图
 */
@ContributesNode(AppScope::class)
@AssistedInject
class DependenciesDetailsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : Node(
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 节点输入数据
     *
     * @property licenseItem 许可证项目信息
     */
    data class Inputs(
        val licenseItem: DependencyLicenseItem,
    ) : NodeInputs

    private val licenseItem = inputs<Inputs>().licenseItem

    /**
     * 渲染许可证详情视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        DependenciesDetailsView(
            modifier = modifier,
            licenseItem = licenseItem,
            onBack = ::navigateUp
        )
    }
}
