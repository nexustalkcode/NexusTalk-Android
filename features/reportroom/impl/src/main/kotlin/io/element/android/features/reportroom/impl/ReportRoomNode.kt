/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 举报房间页面的节点类
 *
 * 继承自 Appyx 框架的 Node 类，作为举报房间功能的主页面
 * 使用依赖注入机制创建，负责协调 Presenter 和 View 的交互
 * 绑定到 SessionScope 作用域，与用户会话生命周期一致
 *
 * @param buildContext 构建上下文，包含节点构建所需的信息
 * @param plugins 插件列表，用于接收外部传入的参数
 * @param presenterFactory 用于创建 Presenter 的工厂实例
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ReportRoomNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ReportRoomPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入参数的数据类
     *
     * 包含举报房间所需的房间ID信息
     *
     * @property roomId 要举报的房间ID
     */
    data class Inputs(val roomId: RoomId) : NodeInputs

    /** 从插件中获取的房间ID */
    private val roomId = inputs<Inputs>().roomId

    /** 举报房间的 Presenter 实例，负责业务逻辑处理 */
    private val presenter: ReportRoomPresenter = presenterFactory.create(roomId = roomId)

    /**
     * 创建节点的视图层
     *
     * 使用 Compose 框架渲染举报房间的界面
     *
     * @param modifier 视图修饰符，用于控制布局和样式
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 从 Presenter 获取当前状态
        val state = presenter.present()
        // 渲染举报房间视图
        ReportRoomView(
            state = state,
            modifier = modifier,
            onBackClick = ::navigateUp,
        )
    }
}
