/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.annotations.ContributesNode
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.forward.api.ForwardEntryPoint
import io.element.android.features.knockrequests.api.list.KnockRequestsListEntryPoint
import io.element.android.features.location.api.Location
import io.element.android.features.location.api.LocationService
import io.element.android.features.location.api.SendLocationEntryPoint
import io.element.android.features.location.api.ShowLocationEntryPoint
import io.element.android.features.messages.api.MessagesEntryPoint
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.preview.AttachmentsPreviewNode
import io.element.android.features.messages.impl.pinned.DefaultPinnedEventsTimelineProvider
import io.element.android.features.messages.impl.pinned.list.PinnedMessagesListNode
import io.element.android.features.messages.impl.report.ReportMessageNode
import io.element.android.features.messages.impl.threads.ThreadedMessagesNode
import io.element.android.features.messages.impl.timeline.TimelineController
import io.element.android.features.messages.impl.timeline.debug.EventDebugInfoNode
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContentWithAttachment
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.features.messages.impl.timeline.model.event.duration
import io.element.android.features.poll.api.create.CreatePollEntryPoint
import io.element.android.features.poll.api.create.CreatePollMode
import io.element.android.libraries.architecture.BackstackWithOverlayBox
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.overlay.Overlay
import io.element.android.libraries.architecture.overlay.operation.hide
import io.element.android.libraries.architecture.overlay.operation.show
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.dateformatter.api.DateFormatter
import io.element.android.libraries.dateformatter.api.DateFormatterMode
import io.element.android.libraries.dateformatter.api.toHumanReadableDuration
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.media.MediaSource
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.alias.matches
import io.element.android.libraries.matrix.api.room.joinedRoomMembers
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.element.android.libraries.matrix.api.widget.CallWidgetMode
import io.element.android.libraries.matrix.ui.messages.RoomMemberProfilesCache
import io.element.android.libraries.matrix.ui.messages.RoomNamesCache
import io.element.android.libraries.mediaviewer.api.MediaInfo
import io.element.android.libraries.mediaviewer.api.MediaViewerEntryPoint
import io.element.android.libraries.textcomposer.mentions.LocalMentionSpanUpdater
import io.element.android.libraries.textcomposer.mentions.MentionSpanTheme
import io.element.android.libraries.textcomposer.mentions.MentionSpanUpdater
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.milliseconds

/**
 * 消息流程节点
 *
 * Appyx FlowNode，管理消息页面的整体导航流程。
 * 包含消息列表、媒体查看器、附件预览、位置查看、事件调试、转发、举报、投票、固定消息、线程等多种子页面。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property roomListService 房间列表服务
 * @property sessionId 会话 ID
 * @property sendLocationEntryPoint 发送位置入口点
 * @property showLocationEntryPoint 查看位置入口点
 * @property createPollEntryPoint 创建投票入口点
 * @property elementCallEntryPoint 视频通话入口点
 * @property mediaViewerEntryPoint 媒体查看器入口点
 * @property forwardEntryPoint 转发入口点
 * @property analyticsService 分析服务
 * @property locationService 位置服务
 * @property room 房间
 * @property roomMemberProfilesCache 房间成员头像缓存
 * @property roomNamesCache 房间名称缓存
 * @property mentionSpanUpdater 提及更新器
 * @property mentionSpanTheme 提及主题
 * @property pinnedEventsTimelineProvider 固定事件时间线提供者
 * @property timelineController 时间线控制器
 * @property knockRequestsListEntryPoint 敲门请求列表入口点
 * @property dateFormatter 日期格式化器
 * @property coroutineDispatchers 协程调度器
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class MessagesFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val roomListService: RoomListService,
    private val sessionId: SessionId,
    private val sendLocationEntryPoint: SendLocationEntryPoint,
    private val showLocationEntryPoint: ShowLocationEntryPoint,
    private val createPollEntryPoint: CreatePollEntryPoint,
    private val elementCallEntryPoint: ElementCallEntryPoint,
    private val mediaViewerEntryPoint: MediaViewerEntryPoint,
    private val forwardEntryPoint: ForwardEntryPoint,
    private val analyticsService: AnalyticsService,
    private val locationService: LocationService,
    private val room: BaseRoom,
    private val roomMemberProfilesCache: RoomMemberProfilesCache,
    private val roomNamesCache: RoomNamesCache,
    private val mentionSpanUpdater: MentionSpanUpdater,
    private val mentionSpanTheme: MentionSpanTheme,
    private val pinnedEventsTimelineProvider: DefaultPinnedEventsTimelineProvider,
    private val timelineController: TimelineController,
    private val knockRequestsListEntryPoint: KnockRequestsListEntryPoint,
    private val dateFormatter: DateFormatter,
    private val coroutineDispatchers: CoroutineDispatchers,
) : BaseFlowNode<MessagesFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = plugins.filterIsInstance<MessagesEntryPoint.Params>().first().initialTarget.toNavTarget(),
        savedStateMap = buildContext.savedStateMap,
    ),
    overlay = Overlay(
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
), MessagesEntryPoint.NodeProxy {
    /**
     * 导航目标密封接口
     *
     * 定义消息流程中的各个页面目标。
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 消息列表页面
         *
         * @property focusedEventId 聚焦的事件 ID（可选）
         */
        @Parcelize
        data class Messages(val focusedEventId: EventId?) : NavTarget

        /**
         * 媒体查看器页面
         *
         * @property mode 媒体查看模式
         * @property eventId 事件 ID（可选）
         * @property mediaInfo 媒体信息
         * @property mediaSource 媒体源
         * @property thumbnailSource 缩略图源（可选）
         */
        @Parcelize
        data class MediaViewer(
            val mode: MediaViewerEntryPoint.MediaViewerMode,
            val eventId: EventId?,
            val mediaInfo: MediaInfo,
            val mediaSource: MediaSource,
            val thumbnailSource: MediaSource?,
        ) : NavTarget

        /**
         * 附件预览页面
         *
         * @property timelineMode 时间线模式
         * @property attachment 附件
         * @property inReplyToEventId 回复的事件 ID（可选）
         */
        @Parcelize
        data class AttachmentPreview(val timelineMode: Timeline.Mode, val attachment: Attachment, val inReplyToEventId: EventId?) : NavTarget

        /**
         * 位置查看器页面
         *
         * @property location 位置信息
         * @property description 描述（可选）
         */
        @Parcelize
        data class LocationViewer(val location: Location, val description: String?) : NavTarget

        /**
         * 事件调试信息页面
         *
         * @property eventId 事件 ID（可选）
         * @property debugInfo 调试信息
         */
        @Parcelize
        data class EventDebugInfo(val eventId: EventId?, val debugInfo: TimelineItemDebugInfo) : NavTarget

        /**
         * 转发事件页面
         *
         * @property eventId 事件 ID
         * @property fromPinnedEvents 是否来自固定消息
         */
        @Parcelize
        data class ForwardEvent(
            val eventId: EventId,
            val fromPinnedEvents: Boolean,
        ) : NavTarget

        /**
         * 举报消息页面
         *
         * @property eventId 事件 ID
         * @property senderId 发送者 ID
         */
        @Parcelize
        data class ReportMessage(val eventId: EventId, val senderId: UserId) : NavTarget

        /**
         * 发送位置页面
         *
         * @property timelineMode 时间线模式
         */
        @Parcelize
        data class SendLocation(val timelineMode: Timeline.Mode) : NavTarget

        /**
         * 创建投票页面
         *
         * @property timelineMode 时间线模式
         */
        @Parcelize
        data class CreatePoll(val timelineMode: Timeline.Mode) : NavTarget

        /**
         * 编辑投票页面
         *
         * @property timelineMode 时间线模式
         * @property eventId 事件 ID
         */
        @Parcelize
        data class EditPoll(val timelineMode: Timeline.Mode, val eventId: EventId) : NavTarget

        /** 固定消息列表页面 */
        @Parcelize
        data object PinnedMessagesList : NavTarget

        /** 敲门请求列表页面 */
        @Parcelize
        data object KnockRequestsList : NavTarget

        /**
         * 线程页面
         *
         * @property threadRootId 线程根 ID
         * @property focusedEventId 聚焦的事件 ID（可选）
         */
        @Parcelize
        data class Thread(val threadRootId: ThreadId, val focusedEventId: EventId?) : NavTarget
    }

    private val callback: MessagesEntryPoint.Callback = callback()

    override fun onBuilt() {
        super.onBuilt()
        lifecycle.subscribe(
            onDestroy = {
                timelineController.close()
            }
        )
        setupCacheUpdaters()

        pinnedEventsTimelineProvider.launchIn(lifecycleScope)
    }

    private fun setupCacheUpdaters() {
        room.membersStateFlow
            .onEach { membersState ->
                withContext(coroutineDispatchers.computation) {
                    roomMemberProfilesCache.replace(membersState.joinedRoomMembers())
                }
            }
            .launchIn(lifecycleScope)

        roomListService
            .allRooms
            .summaries
            .onEach {
                withContext(coroutineDispatchers.computation) {
                    roomNamesCache.replace(it)
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Messages -> {
                val callback = object : MessagesNode.Callback {
                    override fun navigateToRoomDetails() {
                        callback.navigateToRoomDetails()
                    }

                    override fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event): Boolean {
                        return processEventClick(
                            timelineMode = timelineMode,
                            event = event,
                        )
                    }

                    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
                        backstack.push(
                            NavTarget.AttachmentPreview(
                                attachment = attachments.first(),
                                timelineMode = Timeline.Mode.Live,
                                inReplyToEventId = inReplyToEventId,
                            )
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData) {
                        callback.handlePermalinkClick(data, pushToBackstack = true)
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun forwardEvent(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId, fromPinnedEvents = false))
                    }

                    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
                        backstack.push(NavTarget.ReportMessage(eventId, senderId))
                    }

                    override fun navigateToSendLocation() {
                        backstack.push(NavTarget.SendLocation(Timeline.Mode.Live))
                    }

                    override fun navigateToCreatePoll() {
                        backstack.push(NavTarget.CreatePoll(Timeline.Mode.Live))
                    }

                    override fun navigateToEditPoll(eventId: EventId) {
                        backstack.push(NavTarget.EditPoll(Timeline.Mode.Live, eventId))
                    }

                    override fun navigateToRoomCall(roomId: RoomId, callMode: CallWidgetMode) {
                        val callType = CallType.RoomCall(
                            sessionId = sessionId,
                            roomId = roomId,
                            mode = callMode,
                        )
                        analyticsService.captureInteraction(Interaction.Name.MobileRoomCallButton)
                        elementCallEntryPoint.startCall(callType)
                    }

                    override fun navigateToPinnedMessagesList() {
                        backstack.push(NavTarget.PinnedMessagesList)
                    }

                    override fun navigateToKnockRequestsList() {
                        backstack.push(NavTarget.KnockRequestsList)
                    }

                    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
                        backstack.push(NavTarget.Thread(threadRootId, focusedEventId))
                    }
                }
                val inputs = MessagesNode.Inputs(focusedEventId = navTarget.focusedEventId)
                createNode<MessagesNode>(buildContext, listOf(callback, inputs))
            }
            is NavTarget.MediaViewer -> {
                val params = MediaViewerEntryPoint.Params(
                    mode = navTarget.mode,
                    eventId = navTarget.eventId,
                    mediaInfo = navTarget.mediaInfo,
                    mediaSource = navTarget.mediaSource,
                    thumbnailSource = navTarget.thumbnailSource,
                    canShowInfo = true,
                )
                val callback = object : MediaViewerEntryPoint.Callback {
                    override fun onDone() {
                        overlay.hide()
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        this@MessagesFlowNode.viewInTimeline(eventId)
                    }

                    override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) {
                        // Need to go to the parent because of the overlay
                        callback.forwardEvent(eventId, fromPinnedEvents)
                    }
                }
                mediaViewerEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback
                )
            }
            is NavTarget.AttachmentPreview -> {
                val inputs = AttachmentsPreviewNode.Inputs(
                    attachment = navTarget.attachment,
                    timelineMode = navTarget.timelineMode,
                    inReplyToEventId = navTarget.inReplyToEventId,
                )
                createNode<AttachmentsPreviewNode>(buildContext, listOf(inputs))
            }
            is NavTarget.LocationViewer -> {
                val inputs = ShowLocationEntryPoint.Inputs(navTarget.location, navTarget.description)
                showLocationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    inputs = inputs,
                )
            }
            is NavTarget.EventDebugInfo -> {
                val inputs = EventDebugInfoNode.Inputs(navTarget.eventId, navTarget.debugInfo)
                createNode<EventDebugInfoNode>(buildContext, listOf(inputs))
            }
            is NavTarget.ForwardEvent -> {
                val timelineProvider = if (navTarget.fromPinnedEvents) {
                    pinnedEventsTimelineProvider
                } else {
                    timelineController
                }
                val params = ForwardEntryPoint.Params(navTarget.eventId, timelineProvider)
                val callback = object : ForwardEntryPoint.Callback {
                    override fun onDone(roomIds: List<RoomId>) {
                        backstack.pop()
                        roomIds.singleOrNull()?.let { roomId ->
                            callback.navigateToRoom(roomId)
                        }
                    }
                }
                forwardEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
            is NavTarget.ReportMessage -> {
                val inputs = ReportMessageNode.Inputs(navTarget.eventId, navTarget.senderId)
                createNode<ReportMessageNode>(buildContext, listOf(inputs))
            }
            is NavTarget.SendLocation -> {
                sendLocationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    timelineMode = navTarget.timelineMode,
                )
            }
            is NavTarget.CreatePoll -> {
                createPollEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = CreatePollEntryPoint.Params(
                        timelineMode = navTarget.timelineMode,
                        mode = CreatePollMode.NewPoll
                    ),
                )
            }
            is NavTarget.EditPoll -> {
                createPollEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = CreatePollEntryPoint.Params(
                        timelineMode = navTarget.timelineMode,
                        mode = CreatePollMode.EditPoll(eventId = navTarget.eventId)
                    ),
                )
            }
            NavTarget.PinnedMessagesList -> {
                val callback = object : PinnedMessagesListNode.Callback {
                    override fun handleEventClick(event: TimelineItem.Event) {
                        processEventClick(
                            timelineMode = Timeline.Mode.PinnedEvents,
                            event = event,
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        this@MessagesFlowNode.viewInTimeline(eventId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData.RoomLink) {
                        callback.handlePermalinkClick(data, pushToBackstack = !room.matches(data.roomIdOrAlias))
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun handleForwardEventClick(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId = eventId, fromPinnedEvents = true))
                    }
                }
                createNode<PinnedMessagesListNode>(buildContext, plugins = listOf(callback))
            }
            NavTarget.KnockRequestsList -> {
                knockRequestsListEntryPoint.createNode(this, buildContext)
            }
            is NavTarget.Thread -> {
                val inputs = ThreadedMessagesNode.Inputs(
                    threadRootEventId = navTarget.threadRootId,
                    focusedEventId = navTarget.focusedEventId,
                )
                val callback = object : ThreadedMessagesNode.Callback {
                    override fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event): Boolean {
                        return processEventClick(
                            timelineMode = timelineMode,
                            event = event,
                        )
                    }

                    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
                        backstack.push(
                            NavTarget.AttachmentPreview(
                                attachment = attachments.first(),
                                timelineMode = Timeline.Mode.Thread(navTarget.threadRootId),
                                inReplyToEventId = inReplyToEventId,
                            )
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData) {
                        callback.handlePermalinkClick(data, pushToBackstack = true)
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun handleForwardEventClick(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId, fromPinnedEvents = false))
                    }

                    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
                        backstack.push(NavTarget.ReportMessage(eventId, senderId))
                    }

                    override fun navigateToSendLocation() {
                        backstack.push(NavTarget.SendLocation(Timeline.Mode.Thread(navTarget.threadRootId)))
                    }

                    override fun navigateToCreatePoll() {
                        backstack.push(NavTarget.CreatePoll(Timeline.Mode.Thread(navTarget.threadRootId)))
                    }

                    override fun navigateToEditPoll(eventId: EventId) {
                        backstack.push(NavTarget.EditPoll(Timeline.Mode.Thread(navTarget.threadRootId), eventId))
                    }

                    override fun navigateToRoomCall(roomId: RoomId, callMode: CallWidgetMode) {
                        val callType = CallType.RoomCall(
                            sessionId = sessionId,
                            roomId = roomId,
                            mode = callMode,
                        )
                        analyticsService.captureInteraction(Interaction.Name.MobileRoomCallButton)
                        elementCallEntryPoint.startCall(callType)
                    }

                    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
                        backstack.push(NavTarget.Thread(threadRootId, focusedEventId))
                    }
                }
                createNode<ThreadedMessagesNode>(buildContext, listOf(inputs, callback))
            }
        }
    }

    private fun viewInTimeline(eventId: EventId) {
        val permalinkData = PermalinkData.RoomLink(
            roomIdOrAlias = room.roomId.toRoomIdOrAlias(),
            eventId = eventId,
        )
        callback.handlePermalinkClick(permalinkData, pushToBackstack = false)
    }

    private fun processEventClick(
        timelineMode: Timeline.Mode,
        event: TimelineItem.Event,
    ): Boolean {
        val navTarget = when (event.content) {
            is TimelineItemImageContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                )
            }
            is TimelineItemVideoContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                )
            }
            is TimelineItemFileContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                )
            }
            is TimelineItemAudioContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = null,
                )
            }
            is TimelineItemLocationContent -> {
                NavTarget.LocationViewer(
                    location = event.content.location,
                    description = event.content.description,
                ).takeIf { locationService.isServiceAvailable() }
            }
            else -> null
        }
        return when (navTarget) {
            is NavTarget.MediaViewer -> {
                overlay.show(navTarget)
                true
            }
            is NavTarget.LocationViewer -> {
                backstack.push(navTarget)
                true
            }
            else -> false
        }
    }

    private fun buildMediaViewerNavTarget(
        mode: MediaViewerEntryPoint.MediaViewerMode,
        event: TimelineItem.Event,
        content: TimelineItemEventContentWithAttachment,
        mediaSource: MediaSource,
        thumbnailSource: MediaSource?,
    ): NavTarget {
        return NavTarget.MediaViewer(
            mode = mode,
            eventId = event.eventId,
            mediaInfo = MediaInfo(
                filename = content.filename,
                fileSize = content.fileSize,
                caption = content.caption,
                mimeType = content.mimeType,
                formattedFileSize = content.formattedFileSize,
                fileExtension = content.fileExtension,
                senderId = event.senderId,
                senderName = event.safeSenderName,
                senderAvatar = event.senderAvatar.url,
                dateSent = dateFormatter.format(
                    event.sentTimeMillis,
                    mode = DateFormatterMode.Day,
                ),
                dateSentFull = dateFormatter.format(
                    timestamp = event.sentTimeMillis,
                    mode = DateFormatterMode.Full,
                ),
                waveform = (content as? TimelineItemVoiceContent)?.waveform,
                duration = content.duration()?.toHumanReadableDuration(),
            ),
            mediaSource = mediaSource,
            thumbnailSource = thumbnailSource,
        )
    }

    override suspend fun attachThread(threadId: ThreadId, focusedEventId: EventId?) {
        // Wait until we have the UI for the main timeline attached
        waitForChildAttached<MessagesNode>()
        // Give some time for the items in the main timeline to be received, otherwise loading the focused thread root id won't work
        // (look at TimelineItemIndexer and firstProcessLatch for more info)
        delay(10.milliseconds)
        // Then push the new threads screen on top
        backstack.push(NavTarget.Thread(threadId, focusedEventId))
    }

    @Composable
    override fun View(modifier: Modifier) {
        mentionSpanTheme.updateStyles()
        CompositionLocalProvider(
            LocalMentionSpanUpdater provides mentionSpanUpdater
        ) {
            BackstackWithOverlayBox(modifier)
        }
    }
}
