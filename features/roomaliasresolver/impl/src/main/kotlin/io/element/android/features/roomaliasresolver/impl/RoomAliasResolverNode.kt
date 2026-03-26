/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.roomaliasesolver.api.RoomAliasResolverEntryPoint
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

/**
 * 房间别名解析器节点
 *
 * 该类是房间视图别名解析功能的节点，继承自 Appyx 框架的 Node 类。
 * 负责协调 Presenter 和 View 之间的交互，管理界面状态和导航。
 *
 * 使用 @ContributesNode 注解将其注册到 SessionScope，
 * 使用 @AssistedInject 注解实现依赖注入。
 *
 * @see RoomAliasResolverPresenter 负责业务逻辑和数据处理
 * @see RoomAliasResolverView 负责界面渲染
 * @see RoomAliasResolverEntryPoint.Callback 回调接口
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class RoomAliasResolverNode(
    /** 构建上下文，包含节点构建所需的信息 */
    @Assisted buildContext: BuildContext,
    /** 插件列表，包含输入参数和回调 */
    @Assisted plugins: List<Plugin>,
    /** Presenter 工厂，用于创建业务逻辑处理类 */
    presenterFactory: RoomAliasResolverPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /** 解析结果回调，用于通知父模块解析结果 */
    private val callback: RoomAliasResolverEntryPoint.Callback = callback()

    /** 输入参数，包含要解析的房间别名 */
    private val inputs = inputs<RoomAliasResolverEntryPoint.Params>()

    /** 业务逻辑处理类 */
    private val presenter = presenterFactory.create(
        inputs.roomAlias
    )

    /**
     * 渲染视图
     *
     * @param modifier 视图修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 获取Presenter处理后的状态
        val state = presenter.present()
        // 渲染解析视图
        RoomAliasResolverView(
            state = state,
            onSuccess = callback::onAliasResolved,
            onBackClick = ::navigateUp,
            modifier = modifier
        )
    }
}
