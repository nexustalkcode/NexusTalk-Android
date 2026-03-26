/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.PinUnpinAction
import io.element.android.appconfig.MessageComposerConfig
import io.element.android.features.messages.api.timeline.HtmlConverterProvider
import io.element.android.features.messages.impl.actionlist.ActionListEvents
import io.element.android.features.messages.impl.actionlist.ActionListState
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.crypto.historyvisible.HistoryVisibleState
import io.element.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.element.android.features.messages.impl.link.LinkState
import io.element.android.features.messages.impl.messagecomposer.MessageComposerEvent
import io.element.android.features.messages.impl.messagecomposer.MessageComposerState
import io.element.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.element.android.features.messages.impl.timeline.MarkAsFullyRead
import io.element.android.features.messages.impl.timeline.TimelineController
import io.element.android.features.messages.impl.timeline.TimelineEvents
import io.element.android.features.messages.impl.timeline.TimelineState
import io.element.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.element.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.element.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.TimelineItemThreadInfo
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContentWithAttachment
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.messages.impl.voicemessages.composer.DefaultVoiceMessageComposerPresenter
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.element.android.features.roommembermoderation.api.RoomMemberModerationState
import io.element.android.libraries.androidutils.clipboard.ClipboardHelper
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.flatMap
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.toThreadId
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.room.RoomMembersState
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.room.powerlevels.permissionsAsState
import io.element.android.libraries.matrix.api.timeline.item.event.EventOrTransactionId
import io.element.android.libraries.matrix.ui.messages.reply.map
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.room.getDirectRoomMember
import io.element.android.libraries.recentemojis.api.AddRecentEmoji
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 消息页面 Presenter
 *
 * 负责处理消息页面的业务逻辑，管理消息发送、时间线操作、事件处理等功能。
 *
 * @property navigator 消息导航器
 * @property room 已加入的房间
 * @property composerPresenter 消息编辑器 Presenter
 * @property voiceMessageComposerPresenterFactory 语音消息编辑器 Presenter 工厂
 * @property timelinePresenter 时间线 Presenter
 * @property timelineProtectionPresenter 时间线保护 Presenter
 * @property identityChangeStatePresenter 身份更改状态 Presenter
 * @property historyVisibleStatePresenter 历史可见性状态 Presenter
 * @property linkPresenter 链接 Presenter
 * @property actionListPresenter 操作列表 Presenter
 * @property customReactionPresenter 自定义反应 Presenter
 * @property reactionSummaryPresenter 反应摘要 Presenter
 * @property readReceiptBottomSheetPresenter 已读回执底部表单 Presenter
 * @property pinnedMessagesBannerPresenter 固定消息横幅 Presenter
 * @property roomCallStatePresenter 房间通话状态 Presenter
 * @property roomMemberModerationPresenter 房间成员 moderation Presenter
 * @property snackbarDispatcher 提示消息调度器
 * @property dispatchers 协程调度器
 * @property clipboardHelper 剪贴板助手
 * @property htmlConverterProvider HTML 转换提供者
 * @property buildMeta 构建元数据
 * @property timelineController 时间线控制器
 * @property permalinkParser 链接解析器
 * @property analyticsService 分析服务
 * @property encryptionService 加密服务
 * @property featureFlagService 功能标志服务
 * @property addRecentEmoji 添加最近使用的表情
 * @property markAsFullyRead 标记为完全已读
 * @property sessionCoroutineScope 会话协程作用域
 */
@AssistedInject
class MessagesPresenter(
    @Assisted private val navigator: MessagesNavigator,
    private val room: JoinedRoom,
    @Assisted private val composerPresenter: Presenter<MessageComposerState>,
    voiceMessageComposerPresenterFactory: DefaultVoiceMessageComposerPresenter.Factory,
    @Assisted private val timelinePresenter: Presenter<TimelineState>,
    private val timelineProtectionPresenter: Presenter<TimelineProtectionState>,
    private val identityChangeStatePresenter: Presenter<IdentityChangeState>,
    private val historyVisibleStatePresenter: Presenter<HistoryVisibleState>,
    private val linkPresenter: Presenter<LinkState>,
    @Assisted private val actionListPresenter: Presenter<ActionListState>,
    private val customReactionPresenter: Presenter<CustomReactionState>,
    private val reactionSummaryPresenter: Presenter<ReactionSummaryState>,
    private val readReceiptBottomSheetPresenter: Presenter<ReadReceiptBottomSheetState>,
    private val pinnedMessagesBannerPresenter: Presenter<PinnedMessagesBannerState>,
    private val roomCallStatePresenter: Presenter<RoomCallState>,
    private val roomMemberModerationPresenter: Presenter<RoomMemberModerationState>,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val dispatchers: CoroutineDispatchers,
    private val clipboardHelper: ClipboardHelper,
    private val htmlConverterProvider: HtmlConverterProvider,
    private val buildMeta: BuildMeta,
    @Assisted private val timelineController: TimelineController,
    private val permalinkParser: PermalinkParser,
    private val analyticsService: AnalyticsService,
    private val encryptionService: EncryptionService,
    private val featureFlagService: FeatureFlagService,
    private val addRecentEmoji: AddRecentEmoji,
    private val markAsFullyRead: MarkAsFullyRead,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
) : Presenter<MessagesState> {
    /**
     * Presenter 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param navigator 消息导航器
         * @param composerPresenter 消息编辑器 Presenter
         * @param timelinePresenter 时间线 Presenter
         * @param actionListPresenter 操作列表 Presenter
         * @param timelineController 时间线控制器
         * @return MessagesPresenter 实例
         */
        fun create(
            navigator: MessagesNavigator,
            composerPresenter: Presenter<MessageComposerState>,
            timelinePresenter: Presenter<TimelineState>,
            actionListPresenter: Presenter<ActionListState>,
            timelineController: TimelineController,
        ): MessagesPresenter
    }

    private val voiceMessageComposerPresenter = voiceMessageComposerPresenterFactory.create(
        timelineMode = timelineController.mainTimelineMode()
    )

    private val markingAsReadAndExiting = AtomicBoolean(false)

    @Composable
    override fun present(): MessagesState {
        htmlConverterProvider.Update()

        val coroutineScope = rememberCoroutineScope()
        val roomInfo by room.roomInfoFlow.collectAsState()
        val localCoroutineScope = rememberCoroutineScope()
        val composerState = composerPresenter.present()
        val voiceMessageComposerState = voiceMessageComposerPresenter.present()
        val timelineState = timelinePresenter.present()
        val timelineProtectionState = timelineProtectionPresenter.present()
        val identityChangeState = identityChangeStatePresenter.present()
        val historyVisibleState = historyVisibleStatePresenter.present()
        val actionListState = actionListPresenter.present()
        val linkState = linkPresenter.present()
        val customReactionState = customReactionPresenter.present()
        val reactionSummaryState = reactionSummaryPresenter.present()
        val readReceiptBottomSheetState = readReceiptBottomSheetPresenter.present()
        val pinnedMessagesBannerState = pinnedMessagesBannerPresenter.present()
        val roomCallState = roomCallStatePresenter.present()
        val roomMemberModerationState = roomMemberModerationPresenter.present()
        val isVideoCallEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.VideoCall)
        }.collectAsState(initial = true)

        val userEventPermissions by room.permissionsAsState(UserEventPermissions.DEFAULT) { perms ->
            perms.userEventPermissions()
        }

        val roomAvatar by remember {
            derivedStateOf { roomInfo.avatarData() }
        }
        val heroes by remember {
            derivedStateOf { roomInfo.heroes().toImmutableList() }
        }

        var hasDismissedInviteDialog by rememberSaveable {
            mutableStateOf(false)
        }
        LaunchedEffect(Unit) {
            // 进入时移除未读标记但不发送已读回执
            // 因为这些由时间线处理
            withContext(dispatchers.io) {
                room.setUnreadFlag(isUnread = false)

                // 如果加密状态未知，则获取它
                if (roomInfo.isEncrypted == null) {
                    room.getUpdatedIsEncrypted()
                }
            }
        }

        val inviteProgress = remember { mutableStateOf<AsyncData<Unit>>(AsyncData.Uninitialized) }
        var showReinvitePrompt by remember { mutableStateOf(false) }
        val composerHasFocus by remember { derivedStateOf { composerState.textEditorState.hasFocus() } }
        LaunchedEffect(hasDismissedInviteDialog, composerHasFocus, roomInfo) {
            withContext(dispatchers.io) {
                showReinvitePrompt = !hasDismissedInviteDialog && composerHasFocus && roomInfo.isDm && roomInfo.activeMembersCount == 1L
            }
        }

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

        var dmUserVerificationState by remember { mutableStateOf<IdentityState?>(null) }

        val membersState by room.membersStateFlow.collectAsState()
        val dmRoomMember by room.getDirectRoomMember(membersState)
        val roomMemberIdentityStateChanges = identityChangeState.roomMemberIdentityStateChanges

        LifecycleResumeEffect(dmRoomMember, roomInfo.isEncrypted) {
            if (roomInfo.isEncrypted == true) {
                val dmRoomMemberId = dmRoomMember?.userId
                localCoroutineScope.launch {
                    dmRoomMemberId?.let { userId ->
                        dmUserVerificationState = roomMemberIdentityStateChanges.find { it.identityRoomMember.userId == userId }?.identityState
                            ?: encryptionService.getUserIdentity(userId).getOrNull()
                    }
                }
            }
            onPauseOrDispose {}
        }

        fun handleEvent(event: MessagesEvents) {
            when (event) {
                is MessagesEvents.HandleAction -> {
                    localCoroutineScope.handleTimelineAction(
                        action = event.action,
                        targetEvent = event.event,
                        composerState = composerState,
                        enableTextFormatting = composerState.showTextFormatting,
                        timelineState = timelineState,
                        timelineProtectionState = timelineProtectionState,
                    )
                }
                is MessagesEvents.ToggleReaction -> {
                    localCoroutineScope.toggleReaction(event.emoji, event.eventOrTransactionId)
                }
                is MessagesEvents.InviteDialogDismissed -> {
                    hasDismissedInviteDialog = true

                    if (event.action == InviteDialogAction.Invite) {
                        localCoroutineScope.reinviteOtherUser(inviteProgress)
                    }
                }
                is MessagesEvents.Dismiss -> actionListState.eventSink(ActionListEvents.Clear)
                is MessagesEvents.OnUserClicked -> {
                    roomMemberModerationState.eventSink(RoomMemberModerationEvents.ShowActionsForUser(event.user))
                }
                is MessagesEvents.MarkAsFullyReadAndExit -> coroutineScope.launch {
                    if (!markingAsReadAndExiting.getAndSet(true)) {
                        val latestEventId = room.liveTimeline.getLatestEventId().getOrElse {
                            Timber.w(it, "Failed to get latest event id to mark as fully read")
                            navigator.close()
                            return@launch
                        }
                        latestEventId?.let { eventId ->
                            sessionCoroutineScope.launch {
                                markAsFullyRead(room.roomId, eventId)
                            }
                        }
                        navigator.close()
                        markingAsReadAndExiting.set(false)
                    }
                }
            }
        }

        return MessagesState(
            roomId = room.roomId,
            roomName = roomInfo.name,
            roomAvatar = roomAvatar,
            heroes = heroes,
            userEventPermissions = userEventPermissions,
            composerState = composerState,
            voiceMessageComposerState = voiceMessageComposerState,
            timelineState = timelineState,
            timelineProtectionState = timelineProtectionState,
            identityChangeState = identityChangeState,
            historyVisibleState = historyVisibleState,
            linkState = linkState,
            actionListState = actionListState,
            customReactionState = customReactionState,
            reactionSummaryState = reactionSummaryState,
            readReceiptBottomSheetState = readReceiptBottomSheetState,
            snackbarMessage = snackbarMessage,
            inviteProgress = inviteProgress.value,
            showReinvitePrompt = showReinvitePrompt,
            enableTextFormatting = MessageComposerConfig.ENABLE_RICH_TEXT_EDITING,
            roomCallState = if (isVideoCallEnabled) roomCallState else RoomCallState.Unavailable,
            appName = buildMeta.applicationName,
            pinnedMessagesBannerState = pinnedMessagesBannerState,
            dmUserVerificationState = dmUserVerificationState,
            roomMemberModerationState = roomMemberModerationState,
            successorRoom = roomInfo.successorRoom,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 获取房间头像数据
     *
     * @return AvatarData 头像数据
     */
    private fun RoomInfo.avatarData(): AvatarData {
        return AvatarData(
            id = id.value,
            name = name,
            url = avatarUrl,
            size = AvatarSize.TimelineRoom
        )
    }

    /**
     * 获取房间英雄（重要成员）头像列表
     *
     * @return List<AvatarData> 头像数据列表
     */
    private fun RoomInfo.heroes(): List<AvatarData> {
        return heroes.map { user ->
            user.getAvatarData(size = AvatarSize.TimelineRoom)
        }
    }

    /**
     * 处理时间线操作
     *
     * @param action 操作类型
     * @param targetEvent 目标事件
     * @param composerState 消息编辑器状态
     * @param timelineProtectionState 时间线保护状态
     * @param enableTextFormatting 是否启用文本格式
     * @param timelineState 时间线状态
     */
    private fun CoroutineScope.handleTimelineAction(
        action: TimelineItemAction,
        targetEvent: TimelineItem.Event,
        composerState: MessageComposerState,
        timelineProtectionState: TimelineProtectionState,
        enableTextFormatting: Boolean,
        timelineState: TimelineState,
    ) = launch {
        when (action) {
            TimelineItemAction.CopyText -> handleCopyContents(targetEvent)
            TimelineItemAction.CopyCaption -> handleCopyCaption(targetEvent)
            TimelineItemAction.CopyLink -> handleCopyLink(targetEvent)
            TimelineItemAction.Redact -> handleActionRedact(targetEvent)
            TimelineItemAction.Edit,
            TimelineItemAction.EditPoll -> handleActionEdit(targetEvent, composerState, enableTextFormatting)
            TimelineItemAction.AddCaption -> handleActionAddCaption(targetEvent, composerState)
            TimelineItemAction.EditCaption -> handleActionEditCaption(targetEvent, composerState)
            TimelineItemAction.RemoveCaption -> handleRemoveCaption(targetEvent)
            TimelineItemAction.Reply -> handleActionReply(targetEvent, composerState, timelineProtectionState)
            TimelineItemAction.ReplyInThread -> {
                val displayThreads = featureFlagService.isFeatureEnabled(FeatureFlags.Threads)
                if (displayThreads) {
                    // 获取事件所在的线程 ID，或者如果不在线程中则使用事件 ID 以便启动一个
                    val threadId = when (targetEvent.threadInfo) {
                        is TimelineItemThreadInfo.ThreadResponse -> targetEvent.threadInfo.threadRootId
                        is TimelineItemThreadInfo.ThreadRoot, null -> targetEvent.eventId?.toThreadId()
                    } ?: return@launch
                    navigator.navigateToThread(threadId, null)
                } else {
                    handleActionReply(targetEvent, composerState, timelineProtectionState)
                }
            }
            TimelineItemAction.ViewSource -> handleShowDebugInfoAction(targetEvent)
            TimelineItemAction.Forward -> handleForwardAction(targetEvent)
            TimelineItemAction.ReportContent -> handleReportAction(targetEvent)
            TimelineItemAction.EndPoll -> handleEndPollAction(targetEvent, timelineState)
            TimelineItemAction.Pin -> handlePinAction(targetEvent)
            TimelineItemAction.Unpin -> handleUnpinAction(targetEvent)
            TimelineItemAction.ViewInTimeline -> Unit
        }
    }

    /**
     * 移除标题
     *
     * @param targetEvent 目标事件
     */
    private suspend fun handleRemoveCaption(targetEvent: TimelineItem.Event) {
        timelineController.invokeOnCurrentTimeline {
            editCaption(
                eventOrTransactionId = targetEvent.eventOrTransactionId,
                caption = null,
                formattedCaption = null,
            )
        }
    }

    /**
     * 固定事件
     *
     * @param targetEvent 目标事件
     */
    private suspend fun handlePinAction(targetEvent: TimelineItem.Event) {
        if (targetEvent.eventId == null) return
        analyticsService.capture(
            PinUnpinAction(
                from = PinUnpinAction.From.Timeline,
                kind = PinUnpinAction.Kind.Pin,
            )
        )
        timelineController.invokeOnCurrentTimeline {
            pinEvent(targetEvent.eventId)
                .onFailure {
                    Timber.e(it, "Failed to pin event ${targetEvent.eventId}")
                    snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_error))
                }
        }
    }

    /**
     * 取消固定事件
     *
     * @param targetEvent 目标事件
     */
    private suspend fun handleUnpinAction(targetEvent: TimelineItem.Event) {
        if (targetEvent.eventId == null) return
        analyticsService.capture(
            PinUnpinAction(
                from = PinUnpinAction.From.Timeline,
                kind = PinUnpinAction.Kind.Unpin,
            )
        )
        timelineController.invokeOnCurrentTimeline {
            unpinEvent(targetEvent.eventId)
                .onFailure {
                    Timber.e(it, "Failed to unpin event ${targetEvent.eventId}")
                    snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_error))
                }
        }
    }

    /**
     * 切换反应
     *
     * @param emoji 表情符号
     * @param eventOrTransactionId 事件或事务 ID
     */
    private fun CoroutineScope.toggleReaction(
        emoji: String,
        eventOrTransactionId: EventOrTransactionId,
    ) = launch(dispatchers.io) {
        timelineController.invokeOnCurrentTimeline {
            toggleReaction(emoji, eventOrTransactionId)
                .flatMap { added -> if (added) addRecentEmoji(emoji) else Result.success(Unit) }
                .onFailure { Timber.e(it) }
        }
    }

    /**
     * 重新邀请其他用户
     *
     * @param inviteProgress 邀请进度状态
     */
    private fun CoroutineScope.reinviteOtherUser(inviteProgress: MutableState<AsyncData<Unit>>) = launch(dispatchers.io) {
        inviteProgress.value = AsyncData.Loading()
        runCatchingExceptions {
            val memberList = when (val memberState = room.membersStateFlow.value) {
                is RoomMembersState.Ready -> memberState.roomMembers
                is RoomMembersState.Error -> memberState.prevRoomMembers.orEmpty()
                else -> emptyList()
            }

            val member = memberList.first { it.userId != room.sessionId }
            room.inviteUserById(member.userId).onFailure { t ->
                Timber.e(t, "Failed to reinvite DM partner")
            }.getOrThrow()
        }.fold(
            onSuccess = {
                inviteProgress.value = AsyncData.Success(Unit)
            },
            onFailure = {
                inviteProgress.value = AsyncData.Failure(it)
            }
        )
    }

    /**
     * 处理删除操作
     *
     * @param event 目标事件
     */
    private suspend fun handleActionRedact(event: TimelineItem.Event) {
        timelineController.invokeOnCurrentTimeline {
            redactEvent(eventOrTransactionId = event.eventOrTransactionId, reason = null)
                .onFailure { Timber.e(it) }
        }
    }

    /**
     * 处理编辑操作
     *
     * @param targetEvent 目标事件
     * @param composerState 消息编辑器状态
     * @param enableTextFormatting 是否启用文本格式
     */
    private fun handleActionEdit(
        targetEvent: TimelineItem.Event,
        composerState: MessageComposerState,
        enableTextFormatting: Boolean,
    ) {
        when (targetEvent.content) {
            is TimelineItemPollContent -> {
                if (targetEvent.eventId == null) return
                navigator.navigateToEditPoll(targetEvent.eventId)
            }
            else -> {
                val composerMode = MessageComposerMode.Edit(
                    targetEvent.eventOrTransactionId,
                    (targetEvent.content as? TimelineItemTextBasedContent)?.let {
                        if (enableTextFormatting) {
                            it.htmlBody ?: it.body
                        } else {
                            it.body
                        }
                    }.orEmpty(),
                )
                composerState.eventSink(
                    MessageComposerEvent.SetMode(composerMode)
                )
            }
        }
    }

    /**
     * 处理添加标题操作
     *
     * @param targetEvent 目标事件
     * @param composerState 消息编辑器状态
     */
    private suspend fun handleActionAddCaption(
        targetEvent: TimelineItem.Event,
        composerState: MessageComposerState,
    ) {
        val composerMode = MessageComposerMode.EditCaption(
            eventOrTransactionId = targetEvent.eventOrTransactionId,
            content = "",
        )
        composerState.eventSink(
            MessageComposerEvent.SetMode(composerMode)
        )
    }

    /**
     * 处理编辑标题操作
     *
     * @param targetEvent 目标事件
     * @param composerState 消息编辑器状态
     */
    private suspend fun handleActionEditCaption(
        targetEvent: TimelineItem.Event,
        composerState: MessageComposerState,
    ) {
        val composerMode = MessageComposerMode.EditCaption(
            eventOrTransactionId = targetEvent.eventOrTransactionId,
            content = (targetEvent.content as? TimelineItemEventContentWithAttachment)?.caption.orEmpty(),
        )
        composerState.eventSink(
            MessageComposerEvent.SetMode(composerMode)
        )
    }

    /**
     * 处理回复操作
     *
     * @param targetEvent 目标事件
     * @param composerState 消息编辑器状态
     * @param timelineProtectionState 时间线保护状态
     */
    private suspend fun handleActionReply(
        targetEvent: TimelineItem.Event,
        composerState: MessageComposerState,
        timelineProtectionState: TimelineProtectionState,
    ) {
        if (targetEvent.eventId == null) return
        timelineController.invokeOnCurrentTimeline {
            val replyToDetails = loadReplyDetails(targetEvent.eventId).map(permalinkParser)
            val composerMode = MessageComposerMode.Reply(
                replyToDetails = replyToDetails,
                hideImage = timelineProtectionState.hideMediaContent(targetEvent.eventId),
            )
            composerState.eventSink(
                MessageComposerEvent.SetMode(composerMode)
            )
        }
    }

    /**
     * 处理显示调试信息操作
     *
     * @param event 目标事件
     */
    private fun handleShowDebugInfoAction(event: TimelineItem.Event) {
        navigator.navigateToEventDebugInfo(event.eventId, event.debugInfo)
    }

    /**
     * 处理转发操作
     *
     * @param event 目标事件
     */
    private fun handleForwardAction(event: TimelineItem.Event) {
        if (event.eventId == null) return
        navigator.forwardEvent(event.eventId)
    }

    /**
     * 处理举报操作
     *
     * @param event 目标事件
     */
    private fun handleReportAction(event: TimelineItem.Event) {
        if (event.eventId == null) return
        navigator.navigateToReportMessage(event.eventId, event.senderId)
    }

    /**
     * 处理结束投票操作
     *
     * @param event 目标事件
     * @param timelineState 时间线状态
     */
    private fun handleEndPollAction(
        event: TimelineItem.Event,
        timelineState: TimelineState,
    ) {
        event.eventId?.let { timelineState.eventSink(TimelineEvents.EndPoll(it)) }
    }

    /**
     * 处理复制链接
     *
     * @param event 目标事件
     */
    private suspend fun handleCopyLink(event: TimelineItem.Event) {
        event.eventId ?: return
        room.getPermalinkFor(event.eventId).fold(
            onSuccess = { permalink ->
                clipboardHelper.copyPlainText(permalink)
                snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_link_copied_to_clipboard))
            },
            onFailure = {
                Timber.e(it, "Failed to get permalink for event ${event.eventId}")
                snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_error))
            }
        )
    }

    /**
     * 处理复制内容
     *
     * @param event 目标事件
     */
    private fun handleCopyContents(event: TimelineItem.Event) {
        val content = when (event.content) {
            is TimelineItemTextBasedContent -> event.content.body
            is TimelineItemStateContent -> event.content.body
            else -> return
        }
        clipboardHelper.copyPlainText(content)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            snackbarDispatcher.post(SnackbarMessage(R.string.screen_room_timeline_message_copied))
        }
    }

    /**
     * 处理复制标题
     *
     * @param event 目标事件
     */
    private fun handleCopyCaption(event: TimelineItem.Event) {
        val content = (event.content as? TimelineItemEventContentWithAttachment)?.caption ?: return
        clipboardHelper.copyPlainText(content)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_copied_to_clipboard))
        }
    }
}
