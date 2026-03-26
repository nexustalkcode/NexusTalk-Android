/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.messages.impl.MessagesNavigator
import io.element.android.features.messages.impl.UserEventPermissions
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureEvents
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.element.android.features.messages.impl.timeline.components.MessageShieldData
import io.element.android.features.messages.impl.timeline.factories.TimelineItemsFactory
import io.element.android.features.messages.impl.timeline.factories.TimelineItemsFactoryConfig
import io.element.android.features.messages.impl.timeline.model.NewEventState
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemTypingNotificationModel
import io.element.android.features.messages.impl.typing.TypingNotificationState
import io.element.android.features.messages.impl.userEventPermissions
import io.element.android.features.messages.impl.voicemessages.timeline.RedactedVoiceMessageManager
import io.element.android.features.poll.api.actions.EndPollAction
import io.element.android.features.poll.api.actions.SendPollResponseAction
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.core.asEventId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.room.powerlevels.permissionsAsState
import io.element.android.libraries.matrix.api.room.roomMembers
import io.element.android.libraries.matrix.api.timeline.ReceiptType
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.event.TimelineItemEventOrigin
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction.DisplayFirstTimelineItems
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction.NotificationToMessage
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction.OpenRoom
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.finishLongRunningTransaction
import io.element.android.services.analyticsproviders.api.AnalyticsUserData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

const val FOCUS_ON_PINNED_EVENT_DEBOUNCE_DURATION_IN_MILLIS = 200L

/**
 * 时间线 Presenter
 *
 * 负责处理时间线的业务逻辑，管理时间线项目加载、焦点事件、投票操作等功能。
 *
 * @property timelineItemsFactoryCreator 时间线项目工厂创建者
 * @property room 已加入的房间
 * @property dispatchers 协程调度器
 * @property sessionCoroutineScope 会话协程作用域
 * @property navigator 消息导航器
 * @property redactedVoiceMessageManager 已编辑语音消息管理器
 * @property sendPollResponseAction 发送投票响应动作
 * @property endPollAction 结束投票动作
 * @property sessionPreferencesStore 会话偏好设置存储
 * @property timelineController 时间线控制器
 * @property timelineItemIndexer 时间线项目索引器
 * @property resolveVerifiedUserSendFailurePresenter 解决验证用户发送失败 Presenter
 * @property typingNotificationPresenter 打字通知 Presenter
 * @property roomCallStatePresenter 房间通话状态 Presenter
 * @property featureFlagService 功能标志服务
 * @property analyticsService 分析服务
 */
@AssistedInject
class TimelinePresenter(
    timelineItemsFactoryCreator: TimelineItemsFactory.Creator,
    private val room: JoinedRoom,
    private val dispatchers: CoroutineDispatchers,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    @Assisted private val navigator: MessagesNavigator,
    private val redactedVoiceMessageManager: RedactedVoiceMessageManager,
    private val sendPollResponseAction: SendPollResponseAction,
    private val endPollAction: EndPollAction,
    private val sessionPreferencesStore: SessionPreferencesStore,
    @Assisted private val timelineController: TimelineController,
    private val timelineItemIndexer: TimelineItemIndexer = TimelineItemIndexer(),
    private val resolveVerifiedUserSendFailurePresenter: Presenter<ResolveVerifiedUserSendFailureState>,
    private val typingNotificationPresenter: Presenter<TypingNotificationState>,
    private val roomCallStatePresenter: Presenter<RoomCallState>,
    private val featureFlagService: FeatureFlagService,
    private val analyticsService: AnalyticsService,
) : Presenter<TimelineState> {
    private val tag = "TimelinePresenter"

    /**
     * Presenter 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param timelineController 时间线控制器
         * @param navigator 消息导航器
         * @return TimelinePresenter 实例
         */
        fun create(
            timelineController: TimelineController,
            navigator: MessagesNavigator
        ): TimelinePresenter
    }

    private val timelineItemsFactory: TimelineItemsFactory = timelineItemsFactoryCreator.create(
        config = TimelineItemsFactoryConfig(
            computeReadReceipts = true,
            computeReactions = true,
        )
    )
    private var timelineItems by mutableStateOf<ImmutableList<TimelineItem>>(persistentListOf())

    private val focusRequestState: MutableState<FocusRequestState> = mutableStateOf(FocusRequestState.None)

    @Composable
    override fun present(): TimelineState {
        LaunchedEffect(Unit) {
            val parent = analyticsService.getLongRunningTransaction(OpenRoom)
            analyticsService.startLongRunningTransaction(DisplayFirstTimelineItems, parent)
        }

        val localScope = rememberCoroutineScope()

        val timelineMode = remember { timelineController.mainTimelineMode() }

        val lastReadReceiptId = rememberSaveable { mutableStateOf<EventId?>(null) }

        val roomInfo by room.roomInfoFlow.collectAsState()

        val prevMostRecentItemId = rememberSaveable { mutableStateOf<UniqueId?>(null) }

        val newEventState = remember { mutableStateOf(NewEventState.None) }
        val messageShieldDialogData: MutableState<MessageShieldData?> = remember { mutableStateOf(null) }

        val resolveVerifiedUserSendFailureState = resolveVerifiedUserSendFailurePresenter.present()
        val isSendPublicReadReceiptsEnabled by remember {
            sessionPreferencesStore.isSendPublicReadReceiptsEnabled()
        }.collectAsState(initial = true)
        val renderReadReceipts by remember {
            sessionPreferencesStore.isRenderReadReceiptsEnabled()
        }.collectAsState(initial = true)
        val isLive by remember {
            timelineController.isLive()
        }.collectAsState(initial = true)

        val displayThreadSummaries by produceState(false) {
            value = featureFlagService.isFeatureEnabled(FeatureFlags.Threads)
        }

        fun handleEvent(event: TimelineEvents) {
            when (event) {
                is TimelineEvents.LoadMore -> {
                    if (event.direction == Timeline.PaginationDirection.FORWARDS && timelineMode is Timeline.Mode.Thread) {
                        // 不在线程模式下向前分页，因为不支持
                        return
                    }
                    localScope.launch {
                        timelineController.paginate(direction = event.direction)
                    }
                }
                is TimelineEvents.OnScrollFinished -> {
                    if (isLive) {
                        if (event.firstIndex == 0) {
                            newEventState.value = NewEventState.None
                        }
                        Timber.tag(tag).d("## sendReadReceiptIfNeeded firstVisibleIndex: ${event.firstIndex}")
                        sessionCoroutineScope.sendReadReceiptIfNeeded(
                            firstVisibleIndex = event.firstIndex,
                            timelineItems = timelineItems,
                            lastReadReceiptId = lastReadReceiptId,
                            readReceiptType = if (isSendPublicReadReceiptsEnabled) ReceiptType.READ else ReceiptType.READ_PRIVATE,
                        )
                    } else {
                        newEventState.value = NewEventState.None
                    }
                }
                is TimelineEvents.SelectPollAnswer -> sessionCoroutineScope.launch {
                    timelineController.invokeOnCurrentTimeline {
                        sendPollResponseAction.execute(
                            timeline = this,
                            pollStartId = event.pollStartId,
                            answerId = event.answerId
                        )
                    }
                }
                is TimelineEvents.EndPoll -> sessionCoroutineScope.launch {
                    timelineController.invokeOnCurrentTimeline {
                        endPollAction.execute(
                            timeline = this,
                            pollStartId = event.pollStartId,
                        )
                    }
                }
                is TimelineEvents.EditPoll -> {
                    navigator.navigateToEditPoll(event.pollStartId)
                }
                is TimelineEvents.FocusOnEvent -> sessionCoroutineScope.launch {
                    focusRequestState.value = FocusRequestState.Requested(event.eventId, event.debounce)
                    delay(event.debounce)
                    Timber.tag(tag).d("Started focus on ${event.eventId}")
                    focusOnEvent(event.eventId, focusRequestState)
                }.start()
                is TimelineEvents.OnFocusEventRender -> {
                    // 如果有待处理的"通知点击打开时间线"事务，现在聚焦到所需事件后完成它
                    analyticsService.finishLongRunningTransaction(NotificationToMessage)

                    focusRequestState.value = focusRequestState.value.onFocusEventRender()
                }
                is TimelineEvents.ClearFocusRequestState -> {
                    focusRequestState.value = FocusRequestState.None
                }
                is TimelineEvents.JumpToLive -> {
                    timelineController.focusOnLive()
                }
                TimelineEvents.HideShieldDialog -> messageShieldDialogData.value = null
                is TimelineEvents.ShowShieldDialog -> messageShieldDialogData.value = event.messageShieldData
                is TimelineEvents.ComputeVerifiedUserSendFailure -> {
                    resolveVerifiedUserSendFailureState.eventSink(ResolveVerifiedUserSendFailureEvents.ComputeForMessage(event.event))
                }
                is TimelineEvents.NavigateToPredecessorOrSuccessorRoom -> {
                    // 导航到前一个或后继房间
                    val serverNames = calculateServerNamesForRoom(room)
                    navigator.navigateToRoom(event.roomId, null, serverNames)
                }
                is TimelineEvents.OpenThread -> {
                    navigator.navigateToThread(
                        threadRootId = event.threadRootEventId,
                        focusedEventId = event.focusedEvent,
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            timelineItemsFactory.timelineItems
                .onEach { newTimelineItems ->
                    timelineItemIndexer.process(newTimelineItems)
                    timelineItems = newTimelineItems

                    analyticsService.run {
                        finishLongRunningTransaction(DisplayFirstTimelineItems)
                        finishLongRunningTransaction(OpenRoom)
                    }
                }
                .launchIn(this)

            combine(timelineController.timelineItems(), room.membersStateFlow) { items, membersState ->
                val parent = analyticsService.getLongRunningTransaction(DisplayFirstTimelineItems)
                val transaction = parent?.startChild("timelineItemsFactory.replaceWith", "Processing timeline items")
                transaction?.putExtraData(AnalyticsUserData.TIMELINE_ITEM_COUNT, items.count().toString())
                timelineItemsFactory.replaceWith(
                    timelineItems = items,
                    roomMembers = membersState.roomMembers().orEmpty()
                )
                transaction?.finish()
                items
            }
                .onEach(redactedVoiceMessageManager::onEachMatrixTimelineItem)
                .flowOn(dispatchers.computation)
                .launchIn(this)
        }

        LaunchedEffect(timelineItems.size) {
            computeNewItemState(timelineItems, prevMostRecentItemId, newEventState)
        }

        LaunchedEffect(timelineItems.size, focusRequestState.value) {
            val currentFocusRequestState = focusRequestState.value
            if (currentFocusRequestState is FocusRequestState.Success && !currentFocusRequestState.rendered) {
                val eventId = currentFocusRequestState.eventId
                if (timelineItemIndexer.isKnown(eventId)) {
                    val index = timelineItemIndexer.indexOf(eventId)
                    focusRequestState.value = FocusRequestState.Success(eventId = eventId, index = index)
                } else {
                    Timber.w("Unknown timeline item for focused item, can't render focus")
                }
            }
        }

        val typingNotificationState = typingNotificationPresenter.present()
        val roomCallState = roomCallStatePresenter.present()
        val userEventPermissions by room.permissionsAsState(UserEventPermissions.DEFAULT) { perms ->
            perms.userEventPermissions()
        }
        val timelineRoomInfo by remember(typingNotificationState, roomCallState, roomInfo) {
            derivedStateOf {
                TimelineRoomInfo(
                    name = roomInfo.name,
                    isDm = roomInfo.isDm,
                    userHasPermissionToSendMessage = userEventPermissions.canSendMessage,
                    userHasPermissionToSendReaction = userEventPermissions.canSendReaction,
                    roomCallState = roomCallState,
                    pinnedEventIds = roomInfo.pinnedEventIds,
                    typingNotificationState = typingNotificationState,
                    predecessorRoom = room.predecessorRoom(),
                )
            }
        }

        LaunchedEffect(focusRequestState.value) {
            Timber.tag(tag).d("Timeline: $timelineMode | focus state: ${focusRequestState.value}")
        }

        return TimelineState(
            timelineItems = timelineItems,
            timelineMode = timelineMode,
            timelineRoomInfo = timelineRoomInfo,
            renderReadReceipts = renderReadReceipts,
            newEventState = newEventState.value,
            isLive = isLive,
            focusRequestState = focusRequestState.value,
            messageShieldDialogData = messageShieldDialogData.value,
            resolveVerifiedUserSendFailureState = resolveVerifiedUserSendFailureState,
            displayThreadSummaries = displayThreadSummaries,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 聚焦到事件
     *
     * @param eventId 事件 ID
     * @param focusRequestState 焦点请求状态
     */
    private suspend fun focusOnEvent(
        eventId: EventId,
        focusRequestState: MutableState<FocusRequestState>,
    ) {
        if (timelineItemIndexer.isKnown(eventId)) {
            val index = timelineItemIndexer.indexOf(eventId)
            focusRequestState.value = FocusRequestState.Success(eventId = eventId, index = index)
            return
        }

        Timber.tag(tag).d("Event $eventId not found in the loaded timeline, loading a focused timeline")
        focusRequestState.value = FocusRequestState.Loading(eventId = eventId)

        val threadId = room.threadRootIdForEvent(eventId).getOrElse {
            focusRequestState.value = FocusRequestState.Failure(it)
            return
        }

        if (timelineController.mainTimelineMode() is Timeline.Mode.Thread && threadId == null) {
            // 我们在线程时间线中，但事件不属于线程，需要导航回房间
            focusRequestState.value = FocusRequestState.None
            navigator.navigateToRoom(room.roomId, eventId, calculateServerNamesForRoom(room))
        } else {
            Timber.tag(tag).d("Focusing on event $eventId - thread $threadId")
            timelineController.focusOnEvent(eventId, threadId)
                .onSuccess { result ->
                    when (result) {
                        is EventFocusResult.FocusedOnLive -> {
                            focusRequestState.value = FocusRequestState.Success(eventId = eventId)
                        }
                        is EventFocusResult.IsInThread -> {
                            val currentThreadId = (timelineController.mainTimelineMode() as? Timeline.Mode.Thread)?.threadRootId
                            if (currentThreadId == result.threadId) {
                                // 是同一个线程，我们只聚焦到事件
                                focusRequestState.value = FocusRequestState.Success(eventId = eventId)
                            } else {
                                focusRequestState.value = FocusRequestState.Success(eventId = result.threadId.asEventId())
                                // 它属于我们不在的线程，让我们在另一个时间线中打开它
                                navigator.navigateToThread(result.threadId, eventId)
                            }
                        }
                    }
                }
                .onFailure {
                    focusRequestState.value = FocusRequestState.Failure(it)
                }
        }
    }

    /**
     * 计算新项目状态
     *
     * 此方法在时间线项目大小改变时计算作为 [MutableState] 传递的 hasNewItem 状态。
     * 基本上，如果从同步或本地收到新的时间线事件（无论是来自我们还是其他用户），
     * 我们更新状态以告知有新项目。
     * 状态永远不会从此方法重置为 None，但需要从其他地方重置。
     *
     * @param timelineItems 时间线项目列表
     * @param prevMostRecentItemId 上一个最近项目 ID
     * @param newEventState 新事件状态
     */
    private suspend fun computeNewItemState(
        timelineItems: ImmutableList<TimelineItem>,
        prevMostRecentItemId: MutableState<UniqueId?>,
        newEventState: MutableState<NewEventState>
    ) = withContext(dispatchers.computation) {
        // FromMe 优先于 FromOther，所以如果已有 FromMe 则跳过
        if (newEventState.value == NewEventState.FromMe) {
            return@withContext
        }
        val newMostRecentItem = timelineItems.firstOrNull {
            // 忽略打字项目
            (it as? TimelineItem.Virtual)?.model !is TimelineItemTypingNotificationModel
        }
        val prevMostRecentItemIdValue = prevMostRecentItemId.value
        val newMostRecentItemId = newMostRecentItem?.identifier()
        val hasNewEvent = prevMostRecentItemIdValue != null &&
            newMostRecentItem is TimelineItem.Event &&
            newMostRecentItem.origin != TimelineItemEventOrigin.PAGINATION &&
            newMostRecentItemId != prevMostRecentItemIdValue

        if (hasNewEvent) {
            // 如果新事件来自我，即使是来自另一个设备也滚动到底部
            val fromMe = newMostRecentItem.isMine
            newEventState.value = if (fromMe) {
                NewEventState.FromMe
            } else {
                NewEventState.FromOther
            }
        }
        prevMostRecentItemId.value = newMostRecentItemId
    }

    /**
     * 必要时发送已读回执
     *
     * @param firstVisibleIndex 第一个可见索引
     * @param timelineItems 时间线项目列表
     * @param lastReadReceiptId 上一个已读回执 ID
     * @param readReceiptType 已读回执类型
     */
    private fun CoroutineScope.sendReadReceiptIfNeeded(
        firstVisibleIndex: Int,
        timelineItems: ImmutableList<TimelineItem>,
        lastReadReceiptId: MutableState<EventId?>,
        readReceiptType: ReceiptType,
    ) = launch(dispatchers.computation) {
        // 如果我们在时间线底部，将房间标记为已读
        if (firstVisibleIndex == 0) {
            timelineController.invokeOnCurrentTimeline {
                markAsRead(receiptType = readReceiptType)
            }
        } else {
            // 获取用户看到的最后一个有效 EventId，因为第一个索引可能指向 Virtual 项目
            val eventId = getLastEventIdBeforeOrAt(firstVisibleIndex, timelineItems)
            if (eventId != null && eventId != lastReadReceiptId.value) {
                lastReadReceiptId.value = eventId
                timelineController.invokeOnCurrentTimeline {
                    sendReadReceipt(eventId = eventId, receiptType = readReceiptType)
                }
            }
        }
    }

    /**
     * 获取指定索引之前或位置的最后一个事件 ID
     *
     * @param index 索引
     * @param items 项目列表
     * @return 最后一个事件 ID（如果找到）
     */
    private fun getLastEventIdBeforeOrAt(index: Int, items: ImmutableList<TimelineItem>): EventId? {
        for (i in index until items.count()) {
            val item = items[i]
            if (item is TimelineItem.Event) {
                return item.eventId
            }
        }
        return null
    }
}

private fun FocusRequestState.onFocusEventRender(): FocusRequestState {
    return when (this) {
        is FocusRequestState.Success -> copy(rendered = true)
        else -> this
    }
}

// Workaround for not having the server names available, get possible server names from the user ids of the room members
private fun calculateServerNamesForRoom(room: JoinedRoom): List<String> {
    // If we have no room members, return right ahead
    val serverNames = room.membersStateFlow.value.roomMembers() ?: return emptyList()

    // Otherwise get the three most common server names from the user ids of the room members
    return serverNames
        .mapNotNull { it.userId.domainName }
        .groupingBy { it }
        .eachCount()
        .let { map ->
            map.keys.sortedByDescending { map[it] }
        }
        .take(3)
}
