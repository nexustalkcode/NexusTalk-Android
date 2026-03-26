/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.aResolveVerifiedUserSendFailureState
import io.element.android.features.messages.impl.timeline.components.MessageShieldData
import io.element.android.features.messages.impl.timeline.components.receipt.aReadReceiptData
import io.element.android.features.messages.impl.timeline.model.NewEventState
import io.element.android.features.messages.impl.timeline.model.ReadReceiptData
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.element.android.features.messages.impl.timeline.model.TimelineItemReactions
import io.element.android.features.messages.impl.timeline.model.TimelineItemReadReceipts
import io.element.android.features.messages.impl.timeline.model.TimelineItemThreadInfo
import io.element.android.features.messages.impl.timeline.model.anAggregatedReaction
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemStateEventContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.element.android.features.messages.impl.timeline.model.virtual.aTimelineItemDaySeparatorModel
import io.element.android.features.messages.impl.typing.TypingNotificationState
import io.element.android.features.messages.impl.typing.aTypingNotificationState
import io.element.android.features.roomcall.api.aStandByCallState
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.TransactionId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.tombstone.PredecessorRoom
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import io.element.android.libraries.matrix.api.timeline.item.event.MessageShield
import io.element.android.libraries.matrix.ui.messages.reply.InReplyToDetails
import io.element.android.libraries.matrix.ui.messages.reply.aProfileTimelineDetailsReady
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID
import kotlin.random.Random

/**
 * 创建测试用 TimelineState 对象
 *
 * 用于预览和测试目的，构建一个完整的时间线状态对象。
 *
 * @param timelineItems 时间线项目列表
 * @param timelineMode 时间线模式
 * @param renderReadReceipts 是否渲染已读回执
 * @param timelineRoomInfo 时间线房间信息
 * @param focusedEventIndex 聚焦事件索引
 * @param isLive 是否为实时模式
 * @param messageShield 消息盾牌
 * @param resolveVerifiedUserSendFailureState 解决验证用户发送失败状态
 * @param displayThreadSummaries 是否显示线程摘要
 * @param eventSink 事件处理函数
 * @return TimelineState 实例
 */
fun aTimelineState(
    timelineItems: ImmutableList<TimelineItem> = persistentListOf(),
    timelineMode: Timeline.Mode = Timeline.Mode.Live,
    renderReadReceipts: Boolean = false,
    timelineRoomInfo: TimelineRoomInfo = aTimelineRoomInfo(),
    focusedEventIndex: Int = -1,
    isLive: Boolean = true,
    messageShield: MessageShield? = null,
    resolveVerifiedUserSendFailureState: ResolveVerifiedUserSendFailureState = aResolveVerifiedUserSendFailureState(),
    displayThreadSummaries: Boolean = false,
    eventSink: (TimelineEvents) -> Unit = {},
): TimelineState {
    val focusedEventId = timelineItems.filterIsInstance<TimelineItem.Event>().getOrNull(focusedEventIndex)?.eventId
    val focusRequestState = if (focusedEventId != null) {
        FocusRequestState.Success(focusedEventId, focusedEventIndex)
    } else {
        FocusRequestState.None
    }
    return TimelineState(
        timelineItems = timelineItems,
        timelineMode = timelineMode,
        timelineRoomInfo = timelineRoomInfo,
        renderReadReceipts = renderReadReceipts,
        newEventState = NewEventState.None,
        isLive = isLive,
        focusRequestState = focusRequestState,
        messageShieldDialogData = messageShield?.let { MessageShieldData(it) },
        resolveVerifiedUserSendFailureState = resolveVerifiedUserSendFailureState,
        displayThreadSummaries = displayThreadSummaries,
        eventSink = eventSink,
    )
}

/**
 * 创建测试用时间线项目列表
 *
 * 用于预览和测试，生成包含多种类型事件的时间线列表。
 * 列表包含：他人消息（First/Middle/Last位置）、状态事件、分组事件、日期分隔符等。
 *
 * @param content 事件内容
 * @return 包含多个 TimelineItem 的不可变列表
 */
internal fun aTimelineItemList(content: TimelineItemEventContent): ImmutableList<TimelineItem> {
    return persistentListOf(
        // 3 items (First Middle Last) with isMine = false
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.Last
        ),
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.Middle,
            sendState = LocalEventSendState.Failed.Unknown("Message failed to send"),
        ),
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.First
        ),
        // A state event on top of it
        aTimelineItemEvent(
            isMine = false,
            content = aTimelineItemStateEventContent(),
            groupPosition = TimelineItemGroupPosition.None
        ),
        // 3 items (First Middle Last) with isMine = true
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.Last
        ),
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.Middle,
            sendState = LocalEventSendState.Failed.Unknown("Message failed to send"),
        ),
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.First
        ),
        // A grouped event on top of it
        aGroupedEvents(),
        // A day separator
        aTimelineItemDaySeparator(),
    )
}

/**
 * 创建测试用日期分隔符虚拟项目
 *
 * @return 日期分隔符虚拟项目
 */
fun aTimelineItemDaySeparator(): TimelineItem.Virtual {
    return TimelineItem.Virtual(
        id = UniqueId(UUID.randomUUID().toString()),
        model = aTimelineItemDaySeparatorModel("Today"),
    )
}

/**
 * 创建测试用时间线事件
 *
 * 用于预览和测试目的，构建一个模拟的时间线事件。
 *
 * @param eventId 事件ID
 * @param transactionId 事务ID
 * @param isMine 是否为自己的消息
 * @param isEditable 是否可编辑
 * @param canBeRepliedTo 是否可回复
 * @param senderDisplayName 发送者显示名称
 * @param displayNameAmbiguous 显示名称是否可能有歧义
 * @param content 事件内容
 * @param groupPosition 分组位置
 * @param sendState 发送状态
 * @param inReplyTo 回复详情
 * @param threadInfo 线程信息
 * @param debugInfo 调试信息
 * @param timelineItemReactions 反应状态
 * @param readReceiptState 已读回执状态
 * @param messageShield 消息盾牌
 * @return TimelineItem.Event 实例
 */
internal fun aTimelineItemEvent(
    eventId: EventId = EventId("\$" + Random.nextInt().toString()),
    transactionId: TransactionId? = null,
    isMine: Boolean = false,
    isEditable: Boolean = false,
    canBeRepliedTo: Boolean = false,
    senderDisplayName: String = "Sender",
    displayNameAmbiguous: Boolean = false,
    content: TimelineItemEventContent = aTimelineItemTextContent(),
    groupPosition: TimelineItemGroupPosition = TimelineItemGroupPosition.None,
    sendState: LocalEventSendState? = null,
    inReplyTo: InReplyToDetails? = null,
    threadInfo: TimelineItemThreadInfo? = null,
    debugInfo: TimelineItemDebugInfo = aTimelineItemDebugInfo(),
    timelineItemReactions: TimelineItemReactions = aTimelineItemReactions(),
    readReceiptState: TimelineItemReadReceipts = aTimelineItemReadReceipts(),
    messageShield: MessageShield? = null,
): TimelineItem.Event {
    return TimelineItem.Event(
        id = UniqueId(UUID.randomUUID().toString()),
        eventId = eventId,
        transactionId = transactionId,
        senderId = UserId("@senderId:domain"),
        senderAvatar = AvatarData("@senderId:domain", "sender", size = AvatarSize.TimelineSender),
        content = content,
        reactionsState = timelineItemReactions,
        readReceiptState = readReceiptState,
        sentTime = "12:34",
        isMine = isMine,
        isEditable = isEditable,
        canBeRepliedTo = canBeRepliedTo,
        senderProfile = aProfileTimelineDetailsReady(
            displayName = senderDisplayName,
            displayNameAmbiguous = displayNameAmbiguous,
        ),
        groupPosition = groupPosition,
        localSendState = sendState,
        inReplyTo = inReplyTo,
        threadInfo = threadInfo,
        origin = null,
        timelineItemDebugInfoProvider = { debugInfo },
        messageShieldProvider = { messageShield },
        sendHandleProvider = { null },
        forwarder = null,
        forwarderProfile = null,
    )
}

/**
 * 创建测试用反应状态
 *
 * @param count 反应数量
 * @param isHighlighted 是否高亮
 * @return 包含多个聚合反应的状态
 */
fun aTimelineItemReactions(
    count: Int = 1,
    isHighlighted: Boolean = false,
): TimelineItemReactions {
    val emojis = arrayOf("👍️", "😀️", "😁️", "😆️", "😅️", "🤣️", "🥰️", "😇️", "😊️", "😉️", "🙃️", "🙂️", "😍️", "🤗️", "🤭️")
    return TimelineItemReactions(
        reactions = buildList {
            repeat(count) { index ->
                val key = emojis[index % emojis.size]
                add(
                    anAggregatedReaction(
                        key = key,
                        count = index + 1,
                        isHighlighted = isHighlighted
                    )
                )
            }
        }.toImmutableList()
    )
}

/**
 * 创建测试用调试信息
 *
 * @param model 模型字符串
 * @param originalJson 原始JSON
 * @param latestEditedJson 最新编辑JSON
 * @return TimelineItemDebugInfo 实例
 */
internal fun aTimelineItemDebugInfo(
    model: String = "Rust(Model())",
    originalJson: String? = null,
    latestEditedJson: String? = null,
) = TimelineItemDebugInfo(
    model,
    originalJson,
    latestEditedJson
)

/**
 * 创建测试用已读回执状态
 *
 * @param receipts 已读回执数据列表
 * @return TimelineItemReadReceipts 实例
 */
internal fun aTimelineItemReadReceipts(
    receipts: List<ReadReceiptData> = emptyList(),
): TimelineItemReadReceipts {
    return TimelineItemReadReceipts(
        receipts = receipts.toImmutableList(),
    )
}

/**
 * 创建测试用分组事件
 *
 * 用于预览和测试目的，生成一个包含多个事件的分组事件。
 *
 * @param id 唯一ID
 * @param withReadReceipts 是否包含已读回执
 * @return TimelineItem.GroupedEvents 实例
 */
internal fun aGroupedEvents(
    id: UniqueId = UniqueId("0"),
    withReadReceipts: Boolean = false,
): TimelineItem.GroupedEvents {
    val event1 = aTimelineItemEvent(
        isMine = true,
        content = aTimelineItemStateEventContent(),
        groupPosition = TimelineItemGroupPosition.None,
        readReceiptState = TimelineItemReadReceipts(
            receipts = (if (withReadReceipts) listOf(aReadReceiptData(0)) else emptyList()).toImmutableList()
        ),
    )
    val event2 = aTimelineItemEvent(
        isMine = true,
        content = aTimelineItemStateEventContent(body = "Another state event"),
        groupPosition = TimelineItemGroupPosition.None,
        readReceiptState = TimelineItemReadReceipts(
            receipts = (if (withReadReceipts) listOf(aReadReceiptData(1)) else emptyList()).toImmutableList()
        ),
    )
    val events = listOf(event1, event2)
    return TimelineItem.GroupedEvents(
        id = id,
        events = events.toImmutableList(),
        aggregatedReadReceipts = events.flatMap { it.readReceiptState.receipts }.toImmutableList(),
    )
}

/**
 * 创建测试用时间线房间信息
 *
 * 用于预览和测试目的。
 *
 * @param name 房间名称
 * @param isDm 是否为直接消息
 * @param userHasPermissionToSendMessage 用户是否有发送消息权限
 * @param pinnedEventIds 固定事件ID列表
 * @param typingNotificationState 打字通知状态
 * @param predecessorRoom 前一个房间
 * @return TimelineRoomInfo 实例
 */
internal fun aTimelineRoomInfo(
    name: String = "Room name",
    isDm: Boolean = false,
    userHasPermissionToSendMessage: Boolean = true,
    pinnedEventIds: List<EventId> = emptyList(),
    typingNotificationState: TypingNotificationState = aTypingNotificationState(),
    predecessorRoom: PredecessorRoom? = null,
) = TimelineRoomInfo(
    isDm = isDm,
    name = name,
    userHasPermissionToSendMessage = userHasPermissionToSendMessage,
    userHasPermissionToSendReaction = true,
    roomCallState = aStandByCallState(),
    pinnedEventIds = pinnedEventIds.toImmutableList(),
    typingNotificationState = typingNotificationState,
    predecessorRoom = predecessorRoom,
)
