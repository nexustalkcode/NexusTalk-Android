/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.features.roommembermoderation.api.ModerationAction
import io.element.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.element.android.features.roommembermoderation.api.RoomMemberModerationRenderer
import io.element.android.libraries.architecture.appyx.launchMolecule
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 房间成员列表节点
 *
 * 负责显示和管理房间成员列表页面的节点。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 Node 基类，实现 RoomMemberListNavigator 接口处理导航。
 *
 * @see Node 应用节点基类
 * @see RoomMemberListNavigator 成员列表导航接口
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomMemberListNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** 房间成员列表 Presenter */
    private val presenter: RoomMemberListPresenter,
    /** 分析服务，用于跟踪用户行为 */
    private val analyticsService: AnalyticsService,
    /** 成员 moderation 渲染器 */
    private val roomMemberModerationRenderer: RoomMemberModerationRenderer,
) : Node(buildContext, plugins = plugins), RoomMemberListNavigator {
    /**
     * 成员列表回调接口
     *
     * 定义成员列表页面需要与外部交互的回调方法。
     *
     * @see Plugin 插件接口基类
     */
    interface Callback : Plugin {
        /**
         * 导航到房间成员详情
         *
         * @param roomMemberId 成员的 UserID
         */
        fun navigateToRoomMemberDetails(roomMemberId: UserId)

        /**
         * 导航到邀请成员页面
         */
        fun navigateToInviteMembers()
    }

    /** 回调接口实例 */
    private val callback: Callback = callback()
    /** 状态流，用于管理 UI 状态 */
    private val stateFlow = launchMolecule { presenter.present() }

    /**
     * 初始化订阅生命周期事件
     *
     * 订阅节点的生命周期事件，当页面恢复时发送分析屏幕事件。
     */
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomMembers))
            }
        )
    }

    /**
     * 打开成员详情页面
     *
     * 实现 RoomMemberListNavigator 接口方法。
     *
     * @param roomMemberId 成员的 UserID
     */
    override fun openRoomMemberDetails(roomMemberId: UserId) {
        callback.navigateToRoomMemberDetails(roomMemberId)
    }

    /**
     * 打开邀请成员页面
     *
     * 实现 RoomMemberListNavigator 接口方法。
     */
    override fun openInviteMembers() {
        callback.navigateToInviteMembers()
    }

    /**
     * 退出成员列表页面
     *
     * 实现 RoomMemberListNavigator 接口方法，返回上一页。
     */
    override fun exitRoomMemberList() {
        navigateUp()
    }

    /**
     * 渲染成员列表视图
     *
     * 重写 View 方法，使用 Compose 框架渲染成员列表界面。
     *
     * @param modifier 视图修饰符
     * @see Compose Composable 注解
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state by stateFlow.collectAsState()
        RoomMemberListView(
            state = state,
            modifier = modifier,
            navigator = this,
        )
        roomMemberModerationRenderer.Render(
            state = state.moderationState,
            onSelectAction = { action, target ->
                when (action) {
                    is ModerationAction.DisplayProfile -> openRoomMemberDetails(target.userId)
                    else -> state.moderationState.eventSink(RoomMemberModerationEvents.ProcessAction(action, target))
                }
            },
            modifier = Modifier,
        )
    }
}

/**
 * 房间成员列表导航器接口
 *
 * 定义房间成员列表页面的导航回调方法。
 * 实现此接口的类可以处理成员列表的导航逻辑。
 *
 * @see UserId 用户ID
 */
interface RoomMemberListNavigator {
    /**
     * 退出成员列表
     *
     * 返回到上一页面。
     */
    fun exitRoomMemberList() {}

    /**
     * 打开成员详情
     *
     * @param roomMemberId 成员的 UserID
     */
    fun openRoomMemberDetails(roomMemberId: UserId) {}

    /**
     * 打开邀请成员
     *
     * 导航到邀请新成员页面。
     */
    fun openInviteMembers() {}
}
