/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.invite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.features.invitepeople.api.InvitePeoplePresenter
import io.element.android.features.invitepeople.api.InvitePeopleRenderer
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 房间邀请成员节点
 *
 * 负责显示和管理房间邀请成员界面的节点。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 Node 基类，处理 UI 渲染和用户交互。
 *
 * @see Node 应用节点基类
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomInviteMembersNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** 分析服务，用于跟踪用户行为 */
    private val analyticsService: AnalyticsService,
    /** 邀请人员渲染器 */
    private val invitePeopleRenderer: InvitePeopleRenderer,
    /** 已加入的房间 */
    room: JoinedRoom,
    /** 邀请人员 Presenter 工厂 */
    invitePeoplePresenterFactory: InvitePeoplePresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 初始化订阅生命周期事件
     *
     * 订阅节点的生命周期事件，当页面恢复时发送分析屏幕事件。
     */
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.Invites))
            }
        )
    }

    /** 邀请人员 Presenter 实例 */
    private val invitePeoplePresenter = invitePeoplePresenterFactory.create(
        joinedRoom = room,
        roomId = room.roomId,
    )

    /**
     * 渲染邀请成员视图
     *
     * 重写 View 方法，使用 Compose 框架渲染邀请成员界面。
     * 订阅 Presenter 产生的状态，并根据状态变化执行相应操作。
     *
     * @param modifier 视图修饰符
     * @see Compose Composable 注解
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = invitePeoplePresenter.present()

        // Once invites have been sent successfully, close the Invite view.
        // 当邀请发送成功后，关闭邀请视图
        LaunchedEffect(state.sendInvitesAction) {
            if (state.sendInvitesAction.isReady()) {
                navigateUp()
            }
        }

        RoomInviteMembersView(
            state = state,
            modifier = modifier,
            onBackClick = { navigateUp() }
        ) {
            invitePeopleRenderer.Render(state, Modifier)
        }
    }
}
