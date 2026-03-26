/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.features.leaveroom.api.LeaveRoomRenderer
import io.element.android.libraries.androidutils.system.startSharePlainTextIntent
import io.element.android.libraries.architecture.appyx.launchMolecule
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import io.element.android.libraries.androidutils.R as AndroidUtilsR

/**
 * 房间详情节点
 *
 * 负责显示和管理房间详情页面的节点。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 Node 基类，处理房间详情界面的展示和交互。
 *
 * @see Node 应用节点基类
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomDetailsNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** 房间详情 Presenter */
    private val presenter: RoomDetailsPresenter,
    /** 基础房间 */
    private val room: BaseRoom,
    /** 分析服务，用于跟踪用户行为 */
    private val analyticsService: AnalyticsService,
    /** 离开房间渲染器 */
    private val leaveRoomRenderer: LeaveRoomRenderer,
) : Node(buildContext, plugins = plugins) {
    /**
     * 房间详情回调接口
     *
     * 定义房间详情页面需要与外部交互的回调方法。
     *
     * @see Plugin 插件接口基类
     */
    interface Callback : Plugin {
        /**
         * 导航到房间成员列表
         */
        fun navigateToRoomMemberList()

        /**
         * 导航到邀请成员
         */
        fun navigateToInviteMembers()

        /**
         * 导航到房间编辑
         */
        fun navigateToRoomDetailsEdit()

        /**
         * 导航到房间通知设置
         */
        fun navigateToRoomNotificationSettings()

        /**
         * 导航到头像预览
         *
         * @param name 名称
         * @param url 头像URL
         */
        fun navigateToAvatarPreview(name: String, url: String)

        /**
         * 导航到投票历史
         */
        fun navigateToPollHistory()

        /**
         * 导航到媒体库
         */
        fun navigateToMediaGallery()

        /**
         * 导航到管理设置
         */
        fun navigateToAdminSettings()

        /**
         * 导航到固定消息列表
         */
        fun navigateToPinnedMessagesList()

        /**
         * 导航到敲门请求列表
         */
        fun navigateToKnockRequestsList()

        /**
         * 导航到安全与隐私设置
         */
        fun navigateToSecurityAndPrivacy()

        /**
         * 导航到房间成员详情
         *
         * @param userId 成员的 UserID
         */
        fun navigateToRoomMemberDetails(userId: UserId)

        /**
         * 导航到房间通话
         */
        fun navigateToRoomCall()

        /**
         * 导航到举报房间
         */
        fun navigateToReportRoom()

        /**
         * 导航到离开时选择新管理员
         */
        fun navigateToSelectNewOwnersWhenLeaving()
    }

    /** 回调接口实例 */
    private val callback: Callback = callback()

    /**
     * 初始化订阅生命周期事件
     *
     * 订阅节点的生命周期事件，当页面恢复时发送分析屏幕事件。
     */
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomDetails))
            }
        )
    }

    /**
     * 分享房间
     *
     * 协程扩展函数，用于获取房间永久链接并启动分享意图。
     *
     * @param context Android 上下文
     */
    private fun CoroutineScope.onShareRoom(context: Context) = launch {
        room.getPermalink()
            .onSuccess { permalink ->
                context.startSharePlainTextIntent(
                    activityResultLauncher = null,
                    chooserTitle = context.getString(R.string.screen_room_details_share_room_title),
                    text = permalink,
                    noActivityFoundMessage = context.getString(AndroidUtilsR.string.error_no_compatible_app_found)
                )
            }
            .onFailure {
                Timber.e(it)
            }
    }

    /** 状态流，用于管理 UI 状态 */
    private val stateFlow = launchMolecule { presenter.present() }

    /**
     * 当选择新管理员时的回调
     *
     * 在用户选择新管理员后触发离开房间流程。
     */
    fun onNewOwnersSelected() {
        stateFlow.value.eventSink(RoomDetailsEvent.LeaveRoom(needsConfirmation = false))
    }

    @Composable
    override fun View(modifier: Modifier) {
        val context = LocalContext.current
        val state by stateFlow.collectAsState()

        fun onShareRoom() {
            lifecycleScope.onShareRoom(context)
        }

        fun onActionClick(action: RoomDetailsAction) {
            when (action) {
                RoomDetailsAction.Edit -> {
                    callback.navigateToRoomDetailsEdit()
                }
                RoomDetailsAction.AddTopic -> {
                    callback.navigateToRoomDetailsEdit()
                }
            }
        }

        RoomDetailsView(
            state = state,
            modifier = modifier,
            goBack = ::navigateUp,
            onActionClick = ::onActionClick,
            onShareRoom = ::onShareRoom,
            openRoomMemberList = callback::navigateToRoomMemberList,
            openRoomNotificationSettings = callback::navigateToRoomNotificationSettings,
            invitePeople = callback::navigateToInviteMembers,
            openAvatarPreview = callback::navigateToAvatarPreview,
            openPollHistory = callback::navigateToPollHistory,
            openMediaGallery = callback::navigateToMediaGallery,
            openAdminSettings = callback::navigateToAdminSettings,
            onJoinCallClick = callback::navigateToRoomCall,
            onPinnedMessagesClick = callback::navigateToPinnedMessagesList,
            onKnockRequestsClick = callback::navigateToKnockRequestsList,
            onSecurityAndPrivacyClick = callback::navigateToSecurityAndPrivacy,
            onProfileClick = callback::navigateToRoomMemberDetails,
            onReportRoomClick = callback::navigateToReportRoom,
            leaveRoomView = {
                leaveRoomRenderer.Render(
                    state = state.leaveRoomState,
                    onSelectNewOwners = { callback.navigateToSelectNewOwnersWhenLeaving() },
                    modifier = Modifier
                )
            }
        )
    }
}
