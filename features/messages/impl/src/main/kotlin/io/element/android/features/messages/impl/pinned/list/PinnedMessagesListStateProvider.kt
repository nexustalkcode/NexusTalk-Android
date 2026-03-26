/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.messages.impl.UserEventPermissions
import io.element.android.features.messages.impl.actionlist.ActionListState
import io.element.android.features.messages.impl.actionlist.anActionListState
import io.element.android.features.messages.impl.link.LinkState
import io.element.android.features.messages.impl.link.aLinkState
import io.element.android.features.messages.impl.timeline.TimelineRoomInfo
import io.element.android.features.messages.impl.timeline.aTimelineItemDaySeparator
import io.element.android.features.messages.impl.timeline.aTimelineItemEvent
import io.element.android.features.messages.impl.timeline.aTimelineItemReactions
import io.element.android.features.messages.impl.timeline.aTimelineRoomInfo
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.messages.impl.timeline.protection.aTimelineProtectionState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 固定消息列表状态提供器
 *
 * 用于预览（Preview）功能的参数提供器。
 * 生成多种固定消息列表状态的示例数据，用于UI预览和测试。
 *
 * @see PreviewParameterProvider Compose预览参数提供器接口
 * @see PinnedMessagesListState 固定消息列表状态
 */
open class PinnedMessagesListStateProvider : PreviewParameterProvider<PinnedMessagesListState> {
    /**
     * 生成状态序列
     *
     * 提供多种不同状态的示例，用于预览和测试：
     * - 失败状态
     * - 加载中状态
     * - 空状态
     * - 已加载状态（包含各种消息类型：文本、音频、投票等）
     *
     * @return Sequence<PinnedMessagesListState> 状态序列
     */
    override val values: Sequence<PinnedMessagesListState>
        get() = sequenceOf(
            aFailedPinnedMessagesListState(),
            aLoadingPinnedMessagesListState(),
            anEmptyPinnedMessagesListState(),
            aLoadedPinnedMessagesListState(
                timelineItems = persistentListOf(
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemTextContent("A pinned message"),
                        groupPosition = TimelineItemGroupPosition.Last,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemAudioContent("A pinned file"),
                        groupPosition = TimelineItemGroupPosition.Middle,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemPollContent("A pinned poll?"),
                        groupPosition = TimelineItemGroupPosition.First,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemDaySeparator(),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemTextContent("A pinned message"),
                        groupPosition = TimelineItemGroupPosition.Last,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemFileContent("A pinned file?"),
                        groupPosition = TimelineItemGroupPosition.Middle,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemPollContent("A pinned poll?"),
                        groupPosition = TimelineItemGroupPosition.First,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                )
            )
        )
}

/**
 * 创建失败状态的固定消息列表
 *
 * @return PinnedMessagesListState.Failed 失败状态
 */
fun aFailedPinnedMessagesListState() = PinnedMessagesListState.Failed

/**
 * 创建加载状态的固定消息列表
 *
 * @return PinnedMessagesListState.Loading 加载状态
 */
fun aLoadingPinnedMessagesListState() = PinnedMessagesListState.Loading

/**
 * 创建空状态的固定消息列表
 *
 * @return PinnedMessagesListState.Empty 空状态
 */
fun anEmptyPinnedMessagesListState() = PinnedMessagesListState.Empty

/**
 * 创建已加载状态的固定消息列表
 *
 * @param timelineRoomInfo 时间线房间信息
 * @param timelineProtectionState 时间线保护状态
 * @param linkState 链接状态
 * @param timelineItems 时间线项目列表
 * @param actionListState 操作列表状态
 * @param aUserEventPermissions 用户事件权限
 * @param displayThreadSummaries 是否显示线程摘要
 * @param eventSink 事件处理函数
 * @return PinnedMessagesListState.Filled 已加载状态
 */
fun aLoadedPinnedMessagesListState(
    timelineRoomInfo: TimelineRoomInfo = aTimelineRoomInfo(),
    timelineProtectionState: TimelineProtectionState = aTimelineProtectionState(),
    linkState: LinkState = aLinkState(),
    timelineItems: List<TimelineItem> = emptyList(),
    actionListState: ActionListState = anActionListState(),
    aUserEventPermissions: UserEventPermissions = UserEventPermissions.DEFAULT,
    displayThreadSummaries: Boolean = false,
    eventSink: (PinnedMessagesListEvents) -> Unit = {}
) = PinnedMessagesListState.Filled(
    timelineRoomInfo = timelineRoomInfo,
    timelineProtectionState = timelineProtectionState,
    linkState = linkState,
    timelineItems = timelineItems.toImmutableList(),
    actionListState = actionListState,
    userEventPermissions = aUserEventPermissions,
    displayThreadSummaries = displayThreadSummaries,
    eventSink = eventSink,
)
