/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.features.userprofile.shared.UserProfileNodeHelper
import io.element.android.features.userprofile.shared.UserProfileView
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkBuilder
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 房间成员详情节点
 *
 * 负责显示和管理房间成员详情页面的节点。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 Node 基类，处理成员资料展示和交互。
 *
 * @see Node 应用节点基类
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomMemberDetailsNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** 分析服务，用于跟踪用户行为 */
    private val analyticsService: AnalyticsService,
    /** 永久链接构建器 */
    private val permalinkBuilder: PermalinkBuilder,
    /** 房间成员详情 Presenter 工厂 */
    presenterFactory: RoomMemberDetailsPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 房间成员详情输入数据类
     *
     * 实现 NodeInputs 接口，定义节点所需的输入数据。
     *
     * @property roomMemberId 房间成员的 UserID
     * @see NodeInputs 节点输入接口
     * @see UserId 用户ID
     */
    data class RoomMemberDetailsInput(
        val roomMemberId: UserId,
    ) : NodeInputs

    /** 输入数据 */
    private val inputs = inputs<RoomMemberDetailsInput>()
    /** 用户资料节点辅助回调 */
    private val callback = inputs<UserProfileNodeHelper.Callback>()
    /** 房间成员详情 Presenter */
    private val presenter = presenterFactory.create(inputs.roomMemberId)
    /** 用户资料节点辅助工具 */
    private val userProfileNodeHelper = UserProfileNodeHelper(inputs.roomMemberId)

    /**
     * 初始化订阅生命周期事件
     *
     * 订阅节点的生命周期事件，当页面恢复时发送分析屏幕事件。
     */
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.User))
            }
        )
    }

    /**
     * 渲染房间成员详情视图
     *
     * 重写 View 方法，使用 Compose 框架渲染成员详情界面。
     * 显示用户资料、分享用户、发起通话等功能。
     *
     * @param modifier 视图修饰符
     * @see Compose Composable 注解
     */
    @Composable
    override fun View(modifier: Modifier) {
        val context = LocalContext.current

        fun onShareUser() {
            userProfileNodeHelper.onShareUser(context, permalinkBuilder)
        }

        fun navigateToRoom(roomId: RoomId) {
            callback.navigateToRoom(roomId)
        }

        fun onStartCall(roomId: RoomId) {
            callback.startCall(roomId)
        }

        val state = presenter.present()

        UserProfileView(
            state = state,
            modifier = modifier,
            goBack = this::navigateUp,
            onShareUser = ::onShareUser,
            onOpenDm = ::navigateToRoom,
            onStartCall = ::onStartCall,
            openAvatarPreview = callback::navigateToAvatarPreview,
            onVerifyClick = callback::startVerifyUserFlow,
        )
    }
}
