/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.messages.impl.timeline.TimelineEvents
import io.element.android.features.messages.impl.timeline.TimelineRoomInfo
import io.element.android.features.messages.impl.timeline.components.event.TimelineItemEventContentView
import io.element.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionEvent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.libraries.designsystem.colors.gradientSubtleColors
import io.element.android.libraries.designsystem.modifiers.onKeyboardContextMenuAction
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.toPx
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.time.isTalkbackActive
import io.element.android.wysiwyg.link.Link
import kotlin.time.DurationUnit

/**
 * 时间线项目行组件
 *
 * 根据时间线项目的类型（虚拟项目、事件、分组事件）渲染相应的UI组件。
 * 处理焦点事件高亮、辅助功能和无障碍访问。
 *
 * @param timelineItem 时间线项目
 * @param timelineMode 时间线模式
 * @param timelineRoomInfo 时间线房间信息
 * @param isLatestCallNotify 是否为最新通话通知
 * @param renderReadReceipts 是否渲染已读回执
 * @param isLastOutgoingMessage 是否为最后一条发送的消息
 * @param timelineProtectionState 时间线保护状态
 * @param focusedEventId 聚焦事件ID
 * @param displayThreadSummaries 是否显示线程摘要
 * @param onUserDataClick 用户数据点击回调
 * @param onLinkClick 链接点击回调
 * @param onLinkLongClick 链接长按回调
 * @param onContentClick 内容点击回调
 * @param onLongClick 长按回调
 * @param inReplyToClick 回复点击回调
 * @param onReactionClick 反应点击回调
 * @param onReactionLongClick 反应长按回调
 * @param onMoreReactionsClick 更多反应点击回调
 * @param onReadReceiptClick 已读回执点击回调
 * @param onSwipeToReply 滑动回复回调
 * @param onJoinCallClick 加入通话点击回调
 * @param eventSink 事件处理函数
 * @param modifier 修饰符
 * @param eventContentView 自定义事件内容视图
 */
@Composable
internal fun TimelineItemRow(
    timelineItem: TimelineItem,
    timelineMode: Timeline.Mode,
    timelineRoomInfo: TimelineRoomInfo,
    isLatestCallNotify: Boolean = false,
    renderReadReceipts: Boolean,
    isLastOutgoingMessage: Boolean,
    timelineProtectionState: TimelineProtectionState,
    focusedEventId: EventId?,
    displayThreadSummaries: Boolean,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    onContentClick: (TimelineItem.Event) -> Unit,
    onLongClick: (TimelineItem.Event) -> Unit,
    inReplyToClick: (EventId) -> Unit,
    onReactionClick: (key: String, TimelineItem.Event) -> Unit,
    onReactionLongClick: (key: String, TimelineItem.Event) -> Unit,
    onMoreReactionsClick: (TimelineItem.Event) -> Unit,
    onReadReceiptClick: (TimelineItem.Event) -> Unit,
    onSwipeToReply: (TimelineItem.Event) -> Unit,
    onJoinCallClick: () -> Unit,
    eventSink: (TimelineEvents.EventFromTimelineItem) -> Unit,
    modifier: Modifier = Modifier,
    eventContentView: @Composable (TimelineItem.Event, Modifier, (ContentAvoidingLayoutData) -> Unit) -> Unit =
        { event, contentModifier, onContentLayoutChange ->
            TimelineItemEventContentView(
                content = event.content,
                hideMediaContent = timelineProtectionState.hideMediaContent(event.eventId),
                isMine = event.isMine,
                onShowContentClick = { timelineProtectionState.eventSink(TimelineProtectionEvent.ShowContent(event.eventId)) },
                onContentClick = { onContentClick(event) },
                onLongClick = { onLongClick(event) },
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
                eventSink = eventSink,
                modifier = contentModifier,
                onContentLayoutChange = onContentLayoutChange
            )
        },
) {
    val backgroundModifier = if (timelineItem.isEvent(focusedEventId)) {
        val focusedEventOffset = if ((timelineItem as? TimelineItem.Event)?.showSenderAvatar == true) {
            14.dp
        } else {
            2.dp
        }
        Modifier.focusedEvent(focusedEventOffset)
    } else {
        Modifier
    }
    Box(modifier = modifier.then(backgroundModifier)) {
        when (timelineItem) {
            is TimelineItem.Virtual -> {
                TimelineItemVirtualRow(
                    virtual = timelineItem,
                    timelineRoomInfo = timelineRoomInfo,
                    eventSink = eventSink,
                )
            }
            is TimelineItem.Event -> {
                when (timelineItem.content) {
                    is TimelineItemStateContent, is TimelineItemLegacyCallInviteContent -> {
                        TimelineItemStateEventRow(
                            event = timelineItem,
                            renderReadReceipts = renderReadReceipts,
                            isLastOutgoingMessage = isLastOutgoingMessage,
                            onClick = { onContentClick(timelineItem) },
                            onReadReceiptsClick = onReadReceiptClick,
                            onLongClick = { onLongClick(timelineItem) },
                            eventSink = eventSink,
                        )
                    }
                    is TimelineItemRtcNotificationContent -> {
                        TimelineItemCallNotifyView(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                            event = timelineItem,
                            isLatestCallNotify = isLatestCallNotify,
                            roomCallState = timelineRoomInfo.roomCallState,
                            onLongClick = onLongClick,
                            onJoinCallClick = onJoinCallClick,
                        )
                    }
                    else -> {
                        val a11yVoiceMessage = stringResource(CommonStrings.a11y_voice_message)
                        TimelineItemEventRow(
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {
                                    contentDescription = if (timelineItem.content is TimelineItemVoiceContent) {
                                        val voiceMessageText = String.format(a11yVoiceMessage, timelineItem.content.duration.toString(DurationUnit.MINUTES))
                                        "${timelineItem.safeSenderName}, $voiceMessageText"
                                    } else {
                                        timelineItem.safeSenderName
                                    }
                                    // For Polls, allow the answers to be traversed by Talkback
                                    isTraversalGroup = timelineItem.content is TimelineItemPollContent ||
                                        timelineItem.failedToSend ||
                                        timelineItem.messageShield != null
                                    // TODO Also set to true when the event has link(s)
                                }
                                // Custom clickable that applies over the whole item for accessibility
                                .then(
                                    if (isTalkbackActive()) {
                                        Modifier
                                            .combinedClickable(
                                                onClick = { onContentClick(timelineItem) },
                                                onLongClick = { onLongClick(timelineItem) },
                                                onLongClickLabel = stringResource(CommonStrings.action_open_context_menu),
                                            )
                                            .onKeyboardContextMenuAction { onLongClick(timelineItem) }
                                    } else {
                                        Modifier
                                    }
                                ),
                            event = timelineItem,
                            timelineMode = timelineMode,
                            timelineRoomInfo = timelineRoomInfo,
                            renderReadReceipts = renderReadReceipts,
                            timelineProtectionState = timelineProtectionState,
                            isLastOutgoingMessage = isLastOutgoingMessage,
                            displayThreadSummaries = displayThreadSummaries,
                            onEventClick = { onContentClick(timelineItem) },
                            onLongClick = { onLongClick(timelineItem) },
                            onLinkClick = onLinkClick,
                            onLinkLongClick = onLinkLongClick,
                            onUserDataClick = onUserDataClick,
                            inReplyToClick = inReplyToClick,
                            onReactionClick = onReactionClick,
                            onReactionLongClick = onReactionLongClick,
                            onMoreReactionsClick = onMoreReactionsClick,
                            onReadReceiptClick = onReadReceiptClick,
                            onSwipeToReply = { onSwipeToReply(timelineItem) },
                            eventSink = eventSink,
                            eventContentView = { contentModifier, onContentLayoutChange ->
                                eventContentView(timelineItem, contentModifier, onContentLayoutChange)
                            },
                        )
                    }
                }
            }
            is TimelineItem.GroupedEvents -> {
                TimelineItemGroupedEventsRow(
                    timelineItem = timelineItem,
                    timelineMode = timelineMode,
                    timelineRoomInfo = timelineRoomInfo,
                    timelineProtectionState = timelineProtectionState,
                    renderReadReceipts = renderReadReceipts,
                    isLastOutgoingMessage = isLastOutgoingMessage,
                    focusedEventId = focusedEventId,
                    displayThreadSummaries = displayThreadSummaries,
                    onClick = onContentClick,
                    onLongClick = onLongClick,
                    inReplyToClick = inReplyToClick,
                    onUserDataClick = onUserDataClick,
                    onLinkClick = onLinkClick,
                    onLinkLongClick = onLinkLongClick,
                    onReactionClick = onReactionClick,
                    onReactionLongClick = onReactionLongClick,
                    onMoreReactionsClick = onMoreReactionsClick,
                    onReadReceiptClick = onReadReceiptClick,
                    eventSink = eventSink,
                )
            }
        }
    }
}

@Suppress("ModifierComposable")
@Composable
private fun Modifier.focusedEvent(
    focusedEventOffset: Dp,
): Modifier {
    val highlightedLineColor = ElementTheme.colors.borderAccentSubtle
    val gradientColors = gradientSubtleColors()
    val verticalOffset = focusedEventOffset.toPx()
    val verticalRatio = 0.7f
    return drawWithCache {
        val brush = Brush.verticalGradient(
            colors = gradientColors,
            endY = size.height * verticalRatio,
        )
        onDrawBehind {
            drawRect(
                brush,
                topLeft = Offset(0f, verticalOffset),
                size = Size(size.width, size.height * verticalRatio)
            )
            drawLine(
                highlightedLineColor,
                start = Offset(0f, verticalOffset),
                end = Offset(size.width, verticalOffset)
            )
        }
    }.padding(top = 4.dp)
}

@PreviewsDayNight
@Composable
internal fun FocusedEventPreview() = ElementPreview {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(160.dp)
            .focusedEvent(0.dp),
    )
}
