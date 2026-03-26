/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.knockrequests.api.banner.KnockRequestsBannerRenderer
import io.element.android.features.messages.impl.actionlist.ActionListPresenter
import io.element.android.features.messages.impl.actionlist.model.TimelineItemActionPostProcessor
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.messagecomposer.MessageComposerEvent
import io.element.android.features.messages.impl.messagecomposer.MessageComposerPresenter
import io.element.android.features.messages.impl.timeline.TimelineController
import io.element.android.features.messages.impl.timeline.TimelineEvents
import io.element.android.features.messages.impl.timeline.TimelinePresenter
import io.element.android.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.di.TimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.roommembermoderation.api.ModerationAction
import io.element.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.element.android.features.roommembermoderation.api.RoomMemberModerationRenderer
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.androidutils.system.openUrlInExternalApp
import io.element.android.libraries.androidutils.system.toast
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.designsystem.utils.OnLifecycleEvent
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.analytics.toAnalyticsViewRoom
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.alias.matches
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.element.android.libraries.mediaplayer.api.MediaPlayer
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction.LoadMessagesUi
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.finishLongRunningTransaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 消息节点类
 *
 * 消息功能模块的主要节点，负责管理和协调消息页面的各种组件。
 * 集成时间线、消息撰写器、动作列表等功能模块。
 *
 * 使用 @ContributesNode 注解向 RoomScope 贡献节点，
 * 使用 @AssistedInject 实现依赖注入。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property context 应用上下文
 * @property sessionCoroutineScope 会话协程作用域
 * @property room 已加入的房间实例
 * @property analyticsService 分析服务
 * @property messageComposerPresenterFactory 消息撰写器Presenter工厂
 * @property timelinePresenterFactory 时间线Presenter工厂
 * @property presenterFactory 消息Presenter工厂
 * @property actionListPresenterFactory 动作列表Presenter工厂
 * @property timelineItemPresenterFactories 时间线项Presenter工厂
 * @property mediaPlayer 媒体播放器
 * @property permalinkParser 永久链接解析器
 * @property knockRequestsBannerRenderer 敲门前请Banner渲染器
 * @property roomMemberModerationRenderer 房间成员 moderation 渲染器
 *
 * @see MessagesNavigator 消息导航器
 * @see TimelineController 时间线控制器
 * @see MessagesPresenter 消息Presenter
 * @see Node Appyx核心节点
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class MessagesNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    @ApplicationContext private val context: Context,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val room: JoinedRoom,
    private val analyticsService: AnalyticsService,
    messageComposerPresenterFactory: MessageComposerPresenter.Factory,
    timelinePresenterFactory: TimelinePresenter.Factory,
    presenterFactory: MessagesPresenter.Factory,
    actionListPresenterFactory: ActionListPresenter.Factory,
    private val timelineItemPresenterFactories: TimelineItemPresenterFactories,
    private val mediaPlayer: MediaPlayer,
    private val permalinkParser: PermalinkParser,
    private val knockRequestsBannerRenderer: KnockRequestsBannerRenderer,
    private val roomMemberModerationRenderer: RoomMemberModerationRenderer,
) : Node(buildContext, plugins = plugins), MessagesNavigator {
    /**
     * 消息节点输入数据类
     *
     * @property focusedEventId 聚焦的事件ID（可选），用于打开页面时定位到特定消息
     */
    data class Inputs(
        val focusedEventId: EventId?,
    ) : NodeInputs

    private val inputs = inputs<Inputs>()
    private val callback: Callback = callback()

    private val timelineController = TimelineController(room, room.liveTimeline)
    private val presenter = presenterFactory.create(
        navigator = this,
        composerPresenter = messageComposerPresenterFactory.create(timelineController, this),
        timelinePresenter = timelinePresenterFactory.create(timelineController = timelineController, this),
        actionListPresenter = actionListPresenterFactory.create(
            postProcessor = TimelineItemActionPostProcessor.Default,
            timelineMode = timelineController.mainTimelineMode(),
        ),
        timelineController = timelineController,
    )

    /**
     * 消息节点回调接口
     *
     * 定义消息节点与父节点之间的通信协议，处理各种用户交互事件。
     */
    interface Callback : Plugin {
        /**
         * 处理事件点击
         *
         * @param timelineMode 时间线模式
         * @param event 时间线事件
         * @return 是否处理了该事件
         */
        fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event): Boolean

        /**
         * 导航到附件预览页面
         *
         * @param attachments 附件列表
         * @param inReplyToEventId 回复的事件ID
         */
        fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?)

        /**
         * 导航到房间成员详情页面
         *
         * @param userId 用户ID
         */
        fun navigateToRoomMemberDetails(userId: UserId)

        /**
         * 处理永久链接点击
         *
         * @param data 永久链接数据
         */
        fun handlePermalinkClick(data: PermalinkData)

        /**
         * 导航到事件调试信息页面
         *
         * @param eventId 事件ID
         * @param debugInfo 调试信息
         */
        fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)

        /**
         * 转发事件
         *
         * @param eventId 事件ID
         */
        fun forwardEvent(eventId: EventId)

        /**
         * 导航到举报消息页面
         *
         * @param eventId 事件ID
         * @param senderId 发送者ID
         */
        fun navigateToReportMessage(eventId: EventId, senderId: UserId)

        /** 导航到发送位置页面 */
        fun navigateToSendLocation()

        /** 导航到创建投票页面 */
        fun navigateToCreatePoll()

        /**
         * 导航到编辑投票页面
         *
         * @param eventId 投票事件ID
         */
        fun navigateToEditPoll(eventId: EventId)

        /**
         * 导航到房间通话页面
         *
         * @param roomId 房间ID
         */
        fun navigateToRoomCall(roomId: RoomId)

        /**
         * 导航到线程页面
         *
         * @param threadRootId 线程根事件ID
         * @param focusedEventId 聚焦的事件ID
         */
        fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?)

        /** 导航到房间详情页面 */
        fun navigateToRoomDetails()

        /** 导航到固定消息列表页面 */
        fun navigateToPinnedMessagesList()

        /** 导航到敲门前请求列表页面 */
        fun navigateToKnockRequestsList()
    }

    override fun onBuilt() {
        super.onBuilt()
        lifecycle.subscribe(
            onCreate = {
                sessionCoroutineScope.launch { analyticsService.capture(room.toAnalyticsViewRoom()) }
            },
            onResume = {
                analyticsService.finishLongRunningTransaction(LoadMessagesUi)
            },
            onDestroy = {
                mediaPlayer.close()
            }
        )
    }

    private fun onLinkClick(
        activity: Activity,
        darkTheme: Boolean,
        url: String,
        eventSink: (TimelineEvents) -> Unit,
        customTab: Boolean
    ) {
        when (val permalink = permalinkParser.parse(url)) {
            is PermalinkData.UserLink -> {
                // Open the room member profile, it will fallback to
                // the user profile if the user is not in the room
                callback.navigateToRoomMemberDetails(permalink.userId)
            }
            is PermalinkData.RoomLink -> {
                handleRoomLinkClick(permalink, eventSink)
            }
            is PermalinkData.FallbackLink -> {
                if (customTab) {
                    activity.openUrlInChromeCustomTab(null, darkTheme, url)
                } else {
                    activity.openUrlInExternalApp(url)
                }
            }
            is PermalinkData.RoomEmailInviteLink -> {
                activity.openUrlInChromeCustomTab(null, darkTheme, url)
            }
        }
    }

    private fun handleRoomLinkClick(
        roomLink: PermalinkData.RoomLink,
        eventSink: (TimelineEvents) -> Unit,
    ) {
        if (room.matches(roomLink.roomIdOrAlias)) {
            val eventId = roomLink.eventId
            if (eventId != null) {
                eventSink(TimelineEvents.FocusOnEvent(eventId))
            } else {
                // Click on the same room, ignore
                displaySameRoomToast()
            }
        } else {
            callback.handlePermalinkClick(roomLink)
        }
    }

    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
        callback.navigateToEventDebugInfo(eventId, debugInfo)
    }

    override fun forwardEvent(eventId: EventId) {
        callback.forwardEvent(eventId)
    }

    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
        callback.navigateToReportMessage(eventId, senderId)
    }

    override fun navigateToEditPoll(eventId: EventId) {
        callback.navigateToEditPoll(eventId)
    }

    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
        callback.navigateToPreviewAttachments(attachments, inReplyToEventId)
    }

    override fun navigateToRoom(roomId: RoomId, eventId: EventId?, serverNames: List<String>) {
        if (roomId == room.roomId) {
            displaySameRoomToast()
        } else {
            val permalinkData = PermalinkData.RoomLink(roomId.toRoomIdOrAlias(), eventId, viaParameters = serverNames.toImmutableList())
            callback.handlePermalinkClick(permalinkData)
        }
    }

    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
        callback.navigateToThread(threadRootId, focusedEventId)
    }

    private fun displaySameRoomToast() {
        context.toast(CommonStrings.screen_room_permalink_same_room_android)
    }

    override fun close() = navigateUp()

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = ElementTheme.isLightTheme.not()
        CompositionLocalProvider(
            LocalTimelineItemPresenterFactories provides timelineItemPresenterFactories,
        ) {
            val state = presenter.present()

            BackHandler {
                state.eventSink(MessagesEvents.MarkAsFullyReadAndExit)
            }

            OnLifecycleEvent { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> state.composerState.eventSink(MessageComposerEvent.SaveDraft)
                    else -> Unit
                }
            }
            MessagesView(
                state = state,
                onBackClick = { state.eventSink(MessagesEvents.MarkAsFullyReadAndExit) },
                onRoomDetailsClick = callback::navigateToRoomDetails,
                onEventContentClick = { isLive, event ->
                    if (isLive) {
                        callback.handleEventClick(timelineController.mainTimelineMode(), event)
                    } else {
                        val detachedTimelineMode = timelineController.detachedTimelineMode()
                        if (detachedTimelineMode != null) {
                            callback.handleEventClick(detachedTimelineMode, event)
                        } else {
                            false
                        }
                    }
                },
                onUserDataClick = callback::navigateToRoomMemberDetails,
                onLinkClick = { url, customTab ->
                    onLinkClick(
                        activity = activity,
                        darkTheme = isDark,
                        url = url,
                        eventSink = state.timelineState.eventSink,
                        customTab = customTab,
                    )
                },
                onSendLocationClick = callback::navigateToSendLocation,
                onCreatePollClick = callback::navigateToCreatePoll,
                onJoinCallClick = { callback.navigateToRoomCall(room.roomId) },
                onViewAllPinnedMessagesClick = callback::navigateToPinnedMessagesList,
                modifier = modifier,
                knockRequestsBannerView = {
                    knockRequestsBannerRenderer.View(
                        modifier = Modifier,
                        onViewRequestsClick = callback::navigateToKnockRequestsList,
                    )
                },
            )
            roomMemberModerationRenderer.Render(
                state = state.roomMemberModerationState,
                onSelectAction = { action, target ->
                    when (action) {
                        is ModerationAction.DisplayProfile -> callback.navigateToRoomMemberDetails(target.userId)
                        else -> state.roomMemberModerationState.eventSink(RoomMemberModerationEvents.ProcessAction(action, target))
                    }
                },
                modifier = Modifier,
            )

            var focusedEventId by rememberSaveable {
                mutableStateOf(inputs.focusedEventId)
            }
            LaunchedEffect(focusedEventId) {
                if (focusedEventId != null) {
                    state.timelineState.eventSink(TimelineEvents.FocusOnEvent(focusedEventId!!))
                    focusedEventId = null
                }
            }
        }
    }
}
