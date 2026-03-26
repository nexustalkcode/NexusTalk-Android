/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.actionlist.model.TimelineItemActionComparator
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.anUnsignedDeviceSendFailure
import io.element.android.features.messages.impl.timeline.aTimelineItemEvent
import io.element.android.features.messages.impl.timeline.aTimelineItemReactions
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemLocationContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemVoiceContent
import io.element.android.libraries.matrix.api.timeline.item.event.MessageShield
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 动作列表状态预览参数提供器
 *
 * 用于在预览环境中生成测试用的动作列表状态数据。
 * 继承自 PreviewParameterProvider，支持 Compose 预览功能。
 * 提供多种不同类型的动作列表状态用于UI测试。
 *
 * 预览状态包括：
 * - 初始空状态
 * - 加载中状态
 * - 成功状态（文本消息）
 * - 成功状态（图片消息，带标题）
 * - 成功状态（视频消息）
 * - 成功状态（文件消息）
 * - 成功状态（音频消息）
 * - 成功状态（语音消息）
 * - 成功状态（位置消息）
 * - 成功状态（位置消息，无表情反应）
 * - 成功状态（投票消息）
 * - 成功状态（带消息盾牌）
 * - 成功状态（带发送失败）
 *
 * @see PreviewParameterProvider 预览参数提供器基类
 * @see ActionListState 动作列表状态
 */
open class ActionListStateProvider : PreviewParameterProvider<ActionListState> {
    /** 建议使用的表情符号列表，用于快速反应预览 */
    private val suggestedEmojis = persistentListOf("👍️", "👎️", "🔥", "❤️", "👏")

    /**
     * 预览状态序列
     *
     * 生成一系列不同状态的ActionListState用于预览测试。
     * 每个状态代表一种可能的UI展示场景。
     */
    override val values: Sequence<ActionListState>
        get() {
            val reactionsState = aTimelineItemReactions(1, isHighlighted = true)
            return sequenceOf(
                anActionListState(),
                anActionListState().copy(target = ActionListState.Target.Loading(aTimelineItemEvent())),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemImageContent(),
                            displayNameAmbiguous = true,
                            timelineItemReactions = reactionsState,
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(
                            copyAction = TimelineItemAction.CopyCaption,
                        ),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemVideoContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(
                            copyAction = TimelineItemAction.CopyCaption,
                        ),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemFileContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(
                            copyAction = null,
                        ),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemAudioContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(
                            copyAction = TimelineItemAction.CopyCaption,
                        ),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemVoiceContent(caption = null),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(
                            copyAction = null,
                        ),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemLocationContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemLocationContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = false,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(),
                        recentEmojis = suggestedEmojis,
                    ),
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            content = aTimelineItemPollContent(),
                            timelineItemReactions = reactionsState
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = false,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemPollActionList(),
                        recentEmojis = suggestedEmojis,
                    ),
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(
                            timelineItemReactions = reactionsState,
                            messageShield = MessageShield.UnknownDevice(isCritical = true)
                        ),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = VerifiedUserSendFailure.None,
                        actions = aTimelineItemActionList(),
                        recentEmojis = suggestedEmojis,
                    )
                ),
                anActionListState(
                    target = ActionListState.Target.Success(
                        event = aTimelineItemEvent(),
                        sentTimeFull = "January 1, 1970 at 12:00 AM",
                        displayEmojiReactions = true,
                        verifiedUserSendFailure = anUnsignedDeviceSendFailure(),
                        actions = aTimelineItemActionList(),
                        recentEmojis = suggestedEmojis,
                    )
                ),
            )
        }
}

/**
 * 创建测试用动作列表状态的辅助函数
 *
 * 用于测试环境中快速创建ActionListState实例。
 * 提供默认的空目标状态和空的事件处理函数。
 *
 * @param target 动作列表目标状态，默认为None（无目标）
 * @param eventSink 事件处理函数，默认为空函数
 * @return 配置好的 ActionListState 实例
 */
fun anActionListState(
    target: ActionListState.Target = ActionListState.Target.None,
    eventSink: (ActionListEvents) -> Unit = {},
) = ActionListState(
    target = target,
    eventSink = eventSink
)

/**
 * 创建测试用时间线项目动作列表的辅助函数
 *
 * 生成一个包含常见消息动作的测试列表。
 * 动作包括回复、转发、复制、编辑、删除、举报等。
 * 列表会经过排序和转换为不可变列表。
 *
 * @param copyAction 复制动作类型，可以是CopyText、CopyCaption或null
 * @return 动作列表的不可变列表，按优先级排序
 */
fun aTimelineItemActionList(
    copyAction: TimelineItemAction? = TimelineItemAction.CopyText
): ImmutableList<TimelineItemAction> {
    return setOfNotNull(
        TimelineItemAction.Reply,
        TimelineItemAction.Forward,
        copyAction,
        TimelineItemAction.CopyLink,
        TimelineItemAction.Edit,
        TimelineItemAction.Redact,
        TimelineItemAction.ReportContent,
        TimelineItemAction.ViewSource,
    )
        .sortedWith(TimelineItemActionComparator())
        .toImmutableList()
}

/**
 * 创建测试用投票动作列表的辅助函数
 *
 * 生成一个专门针对投票消息的测试动作列表。
 * 包含投票特有的操作：结束投票、编辑投票、回复、置顶、复制链接、删除。
 *
 * @return 投票相关动作列表的不可变列表，按优先级排序
 */
fun aTimelineItemPollActionList(): ImmutableList<TimelineItemAction> {
    return setOf(
        TimelineItemAction.EndPoll,
        TimelineItemAction.EditPoll,
        TimelineItemAction.Reply,
        TimelineItemAction.Pin,
        TimelineItemAction.CopyLink,
        TimelineItemAction.Redact,
    )
        .sortedWith(TimelineItemActionComparator())
        .toImmutableList()
}
