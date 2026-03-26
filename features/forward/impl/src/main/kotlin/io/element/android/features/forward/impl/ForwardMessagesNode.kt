/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.forward.api.ForwardEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.timeline.TimelineProvider
import io.element.android.libraries.roomselect.api.RoomSelectEntryPoint
import io.element.android.libraries.roomselect.api.RoomSelectMode
import kotlinx.parcelize.Parcelize

/**
 * 转发消息节点
 *
 * 转发功能的主要节点，继承自 [ParentNode]。
 * 负责管理消息转发流程，包括房间选择和消息转发操作。
 *
 * 该节点使用永久导航模型 [PermanentNavModel]，因为转发功能是一个单一的页面流程。
 * 它整合了房间选择功能 [RoomSelectEntryPoint] 和消息转发 presenter，
 * 用于实现选择目标房间并转发消息的完整流程。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenterFactory Presenter 工厂，用于创建转发消息 Presenter
 * @property roomSelectEntryPoint 房间选择功能入口点
 *
 * @see ForwardMessagesPresenter
 * @see ForwardMessagesView
 * @see RoomSelectEntryPoint
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ForwardMessagesNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ForwardMessagesPresenter.Factory,
    private val roomSelectEntryPoint: RoomSelectEntryPoint,
) : ParentNode<ForwardMessagesNode.NavTarget>(
    navModel = PermanentNavModel(
        navTargets = setOf(NavTarget),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 导航目标
     *
     * 使用 [Parcelize] 注解以支持状态保存和恢复。
     * 转发功能只有一个导航目标，即转发流程本身。
     */
    @Parcelize
    object NavTarget : Parcelable

    /**
     * 输入参数数据类
     *
     * 定义了转发消息节点所需的输入参数。
     *
     * @property eventId 要转发的事件 ID
     * @property timelineProvider 时间线提供者，用于获取消息时间线并进行转发操作
     */
    data class Inputs(
        val eventId: EventId,
        val timelineProvider: TimelineProvider,
    ) : NodeInputs

    /** 输入参数获取器 */
    private val inputs = inputs<Inputs>()
    /** 转发完成回调 */
    private val callback: ForwardEntryPoint.Callback = callback()
    /** 转发消息 Presenter 实例 */
    private val presenter = presenterFactory.create(inputs.eventId.value, inputs.timelineProvider)

    /**
     * 解析导航目标
     *
     * 当导航到转发消息节点时，此方法会被调用。
     * 它会创建房间选择节点 [RoomSelectEntryPoint]，并设置相应的回调。
     *
     * @param navTarget 导航目标
     * @param buildContext 构建上下文
     * @return Node 房间选择节点
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        // 创建房间选择回调，处理房间选择完成和取消事件
        val callback = object : RoomSelectEntryPoint.Callback {
            /**
             * 房间选择完成事件处理
             *
             * 当用户选择目标房间后触发，调用 presenter 进行消息转发
             *
             * @param roomIds 用户选择的目标房间 ID 列表
             */
            override fun onRoomSelected(roomIds: List<RoomId>) {
                presenter.onRoomSelected(roomIds)
            }

            /**
             * 取消选择事件处理
             *
             * 当用户取消房间选择时触发
             */
            override fun onCancel() {
                callback.onDone(emptyList())
            }
        }

        // 创建房间选择节点，使用转发模式
        return roomSelectEntryPoint.createNode(
            parentNode = this,
            buildContext = buildContext,
            params = RoomSelectEntryPoint.Params(mode = RoomSelectMode.Forward),
            callback = callback,
        )
    }

    /**
     * 渲染转发消息界面
     *
     * 使用 Compose 框架渲染转发消息界面。
     * 界面包含两个部分：
     * 1. 子节点（房间选择界面）
     * 2. 转发状态视图（处理转发成功/失败等状态）
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        Box(modifier = modifier) {
            // 渲染房间选择屏幕
            Children(
                navModel = navModel,
            )

            // 获取 presenter 生成的状态
            val state = presenter.present()
            // 渲染转发消息视图
            ForwardMessagesView(
                state = state,
                onForwardSuccess = callback::onDone,
            )
        }
    }
}
