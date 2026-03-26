/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.messages.impl.actionlist.ActionListPresenter
import io.element.android.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.di.TimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.androidutils.system.copyToClipboard
import io.element.android.libraries.androidutils.system.openUrlInExternalApp
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 固定消息列表节点
 *
 * 固定消息列表页面的入口节点，继承自Appyx框架的Node类。
 * 负责管理页面的生命周期、视图渲染和导航。
 *
 * @property buildContext 构建上下文，包含节点构建所需的信息
 * @property plugins 插件列表，用于扩展节点功能
 * @property presenterFactory 固定消息列表Presenter工厂
 * @property actionListPresenterFactory 操作列表Presenter工厂
 * @property timelineItemPresenterFactories 时间线项展示器工厂
 * @property permalinkParser 永久链接解析器
 *
 * @see Node Appyx框架的节点基类
 * @see PinnedMessagesListNavigator 导航器接口
 * @see PinnedMessagesListPresenter 固定消息列表Presenter
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class PinnedMessagesListNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: PinnedMessagesListPresenter.Factory,
    actionListPresenterFactory: ActionListPresenter.Factory,
    /**
     * 时间线项展示器工厂
     *
     * 负责创建各种类型时间线项的展示器，用于渲染消息内容。
     */
    private val timelineItemPresenterFactories: TimelineItemPresenterFactories,
    /**
     * 永久链接解析器
     *
     * 解析用户分享的永久链接，支持跳转到房间、用户、事件等。
     */
    private val permalinkParser: PermalinkParser,
) : Node(buildContext, plugins = plugins), PinnedMessagesListNavigator {
    /**
     * 回调接口
     *
     * 定义节点需要通知父节点的回调方法。
     * 用于处理事件点击、用户资料查看、时间线跳转等操作。
     */
    interface Callback : Plugin {
        /**
         * 处理事件点击
         *
         * @param event 被点击的时间线事件
         */
        fun handleEventClick(event: TimelineItem.Event)

        /**
         * 导航到房间成员详情页
         *
         * @param userId 用户ID
         */
        fun navigateToRoomMemberDetails(userId: UserId)

        /**
         * 在时间线中查看事件
         *
         * @param eventId 事件ID
         */
        fun viewInTimeline(eventId: EventId)

        /**
         * 处理永久链接点击
         *
         * @param data 房间永久链接数据
         */
        fun handlePermalinkClick(data: PermalinkData.RoomLink)

        /**
         * 导航到事件调试信息页面
         *
         * @param eventId 事件ID（可为null）
         * @param debugInfo 调试信息
         */
        fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)

        /**
         * 处理转发事件点击
         *
         * @param eventId 事件ID
         */
        fun handleForwardEventClick(eventId: EventId)
    }

    /** 回调接口实例 */
    private val callback: Callback = callback()
    /** 固定消息列表Presenter */
    private val presenter = presenterFactory.create(
        navigator = this,
        actionListPresenter = actionListPresenterFactory.create(
            postProcessor = PinnedMessagesListTimelineActionPostProcessor(),
            timelineMode = Timeline.Mode.PinnedEvents,
        )
    )

    /**
     * 处理链接点击
     *
     * 解析URL并根据链接类型执行相应操作：
     * - 用户链接：导航到房间成员资料页
     * - 房间链接：调用回调处理
     * - 其他链接：在外部浏览器中打开
     *
     * @param context Android上下文
     * @param url 点击的URL
     */
    private fun onLinkClick(context: Context, url: String) {
        when (val permalink = permalinkParser.parse(url)) {
            is PermalinkData.UserLink -> {
                // 打开房间成员资料，如果用户不在房间中则回退到用户资料
                callback.navigateToRoomMemberDetails(permalink.userId)
            }
            is PermalinkData.RoomLink -> {
                callback.handlePermalinkClick(permalink)
            }
            is PermalinkData.FallbackLink,
            is PermalinkData.RoomEmailInviteLink -> {
                context.openUrlInExternalApp(url)
            }
        }
    }

    /**
     * 在时间线中查看事件
     *
     * @param eventId 事件ID
     */
    override fun viewInTimeline(eventId: EventId) {
        callback.viewInTimeline(eventId)
    }

    /**
     * 导航到事件调试信息页面
     *
     * @param eventId 事件ID
     * @param debugInfo 调试信息
     */
    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
        callback.navigateToEventDebugInfo(eventId, debugInfo)
    }

    /**
     * 转发事件
     *
     * @param eventId 事件ID
     */
    override fun forwardEvent(eventId: EventId) {
        callback.handleForwardEventClick(eventId)
    }

    /**
     * 渲染视图
     *
     * Composable函数，渲染固定消息列表的UI。
     * 提供各种交互回调：
     * - 返回按钮点击
     * - 事件点击
     * - 用户头像点击
     * - 链接点击和长按
     *
     * @param modifier Compose修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        CompositionLocalProvider(
            LocalTimelineItemPresenterFactories provides timelineItemPresenterFactories,
        ) {
            val context = LocalContext.current
            val toastMessage = stringResource(CommonStrings.common_copied_to_clipboard)
            val view = LocalView.current
            val state = presenter.present()
            PinnedMessagesListView(
                state = state,
                onBackClick = ::navigateUp,
                onEventClick = callback::handleEventClick,
                onUserDataClick = { callback.navigateToRoomMemberDetails(it.userId) },
                onLinkClick = { link -> onLinkClick(context, link.url) },
                onLinkLongClick = {
                    view.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS
                    )
                    context.copyToClipboard(
                        text = it.url,
                        toastMessage = toastMessage,
                    )
                },
                modifier = modifier
            )
        }
    }
}
