/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.addpeople

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.invitepeople.api.InvitePeoplePresenter
import io.element.android.features.invitepeople.api.InvitePeopleRenderer
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 添加人员节点
 *
 * 创建房间流程中的"添加人员"步骤节点，负责展示邀请人员的界面。
 * 用户可以在此界面中搜索并邀请其他用户加入已创建的聊天室。
 *
 * @property buildContext 构建上下文，包含节点构建所需的信息
 * @property plugins 插件列表，用于扩展节点功能
 * @property invitePeoplePresenterFactory 邀请人员 presenter 工厂，用于创建邀请 presenter 实例
 * @property invitePeopleRenderer 邀请人员渲染器，用于渲染邀请人员界面
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class AddPeopleNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    invitePeoplePresenterFactory: InvitePeoplePresenter.Factory,
    private val invitePeopleRenderer: InvitePeopleRenderer,
) : Node(buildContext, plugins = plugins) {
    /**
     * 输入数据类
     *
     * 定义创建此节点时需要传入的输入参数
     *
     * @property roomId 房间 ID，标识已创建的房间，用于后续添加成员
     */
    data class Inputs(
        val roomId: RoomId,
    ) : NodeInputs

    /**
     * 回调接口
     *
     * 定义节点完成后需要通知父节点的回调方法
     */
    interface Callback : Plugin {
        /**
         * 完成回调
         *
         * 当用户完成添加人员操作后调用，通知流程结束
         */
        fun onFinish()
    }

    /** 回调接口实例，用于通知父节点操作完成 */
    private val callback: Callback = callback()
    /** 房间 ID，从输入参数中获取 */
    private val roomId = inputs<Inputs>().roomId
    /** 邀请人员 Presenter，负责处理邀请人员的业务逻辑 */
    private val invitePeoplePresenter = invitePeoplePresenterFactory.create(
        joinedRoom = null,
        roomId = roomId,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = invitePeoplePresenter.present()
        AddPeopleView(
            state = state,
            onFinish = callback::onFinish,
        ) {
            invitePeopleRenderer.Render(state, Modifier)
        }
    }
}
