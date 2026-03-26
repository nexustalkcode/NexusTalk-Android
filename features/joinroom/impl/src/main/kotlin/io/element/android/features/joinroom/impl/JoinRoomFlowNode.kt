/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.invite.api.InviteData
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteView
import io.element.android.features.invite.api.declineandblock.DeclineInviteAndBlockEntryPoint
import io.element.android.features.joinroom.api.JoinRoomEntryPoint
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import kotlinx.parcelize.Parcelize

/**
 * 加入房间流程节点
 *
 * 管理加入房间功能的导航流程，包括主页面和拒绝邀请并阻止用户页面。
 * 继承自 BaseFlowNode，使用 backstack 管理导航状态。
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class JoinRoomFlowNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** JoinRoomPresenter 工厂 */
    presenterFactory: JoinRoomPresenter.Factory,
    /** 接受/拒绝邀请视图 */
    private val acceptDeclineInviteView: AcceptDeclineInviteView,
    /** 拒绝邀请并阻止用户入口点 */
    private val declineAndBlockEntryPoint: DeclineInviteAndBlockEntryPoint
) : BaseFlowNode<JoinRoomFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    /** 从插件中获取输入参数 */
    private val inputs: JoinRoomEntryPoint.Inputs = inputs()
    /** 创建 Presenter 实例 */
    private val presenter = presenterFactory.create(
        inputs.roomId,
        inputs.roomIdOrAlias,
        inputs.roomDescription,
        inputs.serverNames,
        inputs.trigger,
    )

    /**
     * 导航目标密封接口
     *
     * 定义了加入房间流程中的所有导航目标状态。
     */
    sealed interface NavTarget : Parcelable {
        /** 根页面 - 加入房间主页面 */
        @Parcelize
        data object Root : NavTarget

        /**
         * 拒绝邀请并阻止用户页面
         *
         * @property inviteData 邀请数据
         */
        @Parcelize
        data class DeclineInviteAndBlockUser(val inviteData: InviteData) : NavTarget
    }

    /**
     * 解析导航目标并返回对应的节点
     *
     * @param navTarget 导航目标
     * @param buildContext 构建上下文
     * @return Node 对应的节点实例
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.DeclineInviteAndBlockUser -> declineAndBlockEntryPoint.createNode(
                parentNode = this,
                buildContext = buildContext,
                inviteData = navTarget.inviteData,
            )
            NavTarget.Root -> rootNode(buildContext)
        }
    }

    /**
     * 渲染视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        BackstackView(modifier)
    }

    /**
     * 创建根节点
     *
     * @param buildContext 构建上下文
     * @return Node 根节点实例
     */
    private fun rootNode(buildContext: BuildContext): Node {
        return node(buildContext) { modifier ->
            val state = presenter.present()
            JoinRoomView(
                state = state,
                onBackClick = ::navigateUp,
                onJoinSuccess = {},
                onForgetSuccess = ::navigateUp,
                onCancelKnockSuccess = {},
                onKnockSuccess = {},
                onDeclineInviteAndBlockUser = {
                    backstack.push(
                        NavTarget.DeclineInviteAndBlockUser(it)
                    )
                },
                modifier = modifier
            )
            acceptDeclineInviteView.Render(
                state = state.acceptDeclineInviteState,
                onAcceptInviteSuccess = {},
                onDeclineInviteSuccess = {},
                modifier = Modifier
            )
        }
    }
}
