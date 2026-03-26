/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.messages.impl.UserEventPermissions
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.actionlist.model.TimelineItemActionComparator
import io.element.android.features.messages.impl.actionlist.model.TimelineItemActionPostProcessor
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailureFactory
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.TimelineItemThreadInfo
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContentWithAttachment
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.canBeCopied
import io.element.android.features.messages.impl.timeline.model.event.canBeForwarded
import io.element.android.features.messages.impl.timeline.model.event.canReact
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.dateformatter.api.DateFormatter
import io.element.android.libraries.dateformatter.api.DateFormatterMode
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.libraries.recentemojis.api.GetRecentEmojis
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 动作列表Presenter接口
 *
 * 负责管理消息动作列表的业务逻辑，如显示消息的可用操作（回复、转发、编辑等）。
 * 继承自 Presenter 接口，用于构建动作列表的UI状态。
 * 处理用户权限判断、功能开关检查、消息内容分析等逻辑，
 * 动态生成符合当前上下文的任务动作列表。
 *
 * @see Presenter Presenter基类
 * @see ActionListState 动作列表状态
 * @see TimelineItemActionPostProcessor 动作后处理器
 * @see Timeline.Mode 时间线模式
 */
interface ActionListPresenter : Presenter<ActionListState> {
    /**
     * 动作列表Presenter工厂接口
     *
     * 用于创建不同配置的动作列表Presenter实例。
     * 支持注入不同的后处理器和时间线模式，
     * 以满足不同场景下的功能需求。
     */
    interface Factory {
        /**
         * 创建动作列表Presenter实例
         *
         * @param postProcessor 动作后处理器，用于动态修改动作列表
         * @param timelineMode 时间线模式，影响可用动作（如线程模式下的回复行为）
         * @return 动作列表Presenter实例
         */
        fun create(
            postProcessor: TimelineItemActionPostProcessor,
            timelineMode: Timeline.Mode,
        ): ActionListPresenter
    }
}

/**
 * 默认动作列表Presenter实现类
 *
 * 实现消息动作列表的业务逻辑，包括：
 * - 根据用户权限计算可用动作
 * - 处理消息的回复、转发、编辑、删除、置顶等操作
 * - 管理表情回复和发送失败验证
 * - 根据消息内容类型过滤可用动作
 *
 * 使用 @AssistedInject 注解实现依赖注入，
 * @AssistedFactory 和 @ContributesBinding 注解用于生成工厂接口并绑定到 RoomScope。
 *
 * 主要功能：
 * 1. 计算可用动作 - 根据消息内容、用户权限、功能开关等条件
 * 2. 处理动作后处理 - 支持通过后处理器动态修改动作列表
 * 3. 生成状态数据 - 构建用于UI显示的状态对象
 *
 * @property postProcessor 动作后处理器，用于在显示前修改动作列表
 * @property timelineMode 时间线模式，影响某些动作的可用性（如线程回复）
 * @property appPreferencesStore 应用偏好设置存储，用于检查开发者模式等设置
 * @property room 基础房间，用于获取房间信息（如置顶消息ID）
 * @property userSendFailureFactory 用户发送失败工厂，用于生成发送失败状态
 * @property dateFormatter 日期格式化器，用于格式化消息发送时间
 * @property featureFlagService 特性开关服务，用于检查功能是否启用
 * @property getRecentEmojis 获取最近表情符号，用于显示快速反应列表
 *
 * @see ActionListPresenter 动作列表Presenter接口
 * @see TimelineItemAction 时间线项目动作
 * @see VerifiedUserSendFailure 已验证用户发送失败
 */
@AssistedInject
class DefaultActionListPresenter(
    @Assisted
    private val postProcessor: TimelineItemActionPostProcessor,
    @Assisted
    private val timelineMode: Timeline.Mode,
    private val appPreferencesStore: AppPreferencesStore,
    private val room: BaseRoom,
    private val userSendFailureFactory: VerifiedUserSendFailureFactory,
    private val dateFormatter: DateFormatter,
    private val featureFlagService: FeatureFlagService,
    private val getRecentEmojis: GetRecentEmojis,
) : ActionListPresenter {
    /**
     * 默认动作列表Presenter工厂接口
     *
     * 使用 @AssistedFactory 注解自动生成工厂实现，
     * @ContributesBinding 注解将工厂绑定到 RoomScope。
     * 用于依赖注入框架创建Presenter实例。
     */
    @AssistedFactory
    @ContributesBinding(RoomScope::class)
    interface Factory : ActionListPresenter.Factory {
        override fun create(
            postProcessor: TimelineItemActionPostProcessor,
            timelineMode: Timeline.Mode,
        ): DefaultActionListPresenter
    }

    /** 动作比较器，用于对动作列表进行排序 */
    private val comparator = TimelineItemActionComparator()

    /** 建议使用的表情符号列表，显示在快速反应区域 */
    private val suggestedEmojis = persistentListOf("👍️", "👎️", "🔥", "❤️", "👏")

    /**
     * 生成动作列表状态
     *
     * 使用Composable方式构建状态，管理：
     * - 开发者模式开关状态
     * - 房间置顶消息ID列表
     * - 线程功能开关状态
     * - 目标消息状态
     *
     * @return 包含当前状态和事件处理函数的ActionListState
     */
    @Composable
    override fun present(): ActionListState {
        val localCoroutineScope = rememberCoroutineScope()

        val target: MutableState<ActionListState.Target> = remember {
            mutableStateOf(ActionListState.Target.None)
        }

        val isDeveloperModeEnabled by remember {
            appPreferencesStore.isDeveloperModeEnabledFlow()
        }.collectAsState(initial = false)
        val pinnedEventIds by remember {
            room.roomInfoFlow.map { it.pinnedEventIds }
        }.collectAsState(initial = persistentListOf())

        val isThreadsEnabled = featureFlagService.isFeatureEnabledFlow(FeatureFlags.Threads).collectAsState(false)

        fun handleEvent(event: ActionListEvents) {
            when (event) {
                ActionListEvents.Clear -> target.value = ActionListState.Target.None
                is ActionListEvents.ComputeForMessage -> localCoroutineScope.computeForMessage(
                    timelineItem = event.event,
                    usersEventPermissions = event.userEventPermissions,
                    isDeveloperModeEnabled = isDeveloperModeEnabled,
                    pinnedEventIds = pinnedEventIds,
                    target = target,
                    isThreadsEnabled = isThreadsEnabled.value,
                )
            }
        }

        return ActionListState(
            target = target.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 为指定消息计算可用动作
     *
     * 异步计算给定消息的可用动作列表。
     * 包含以下步骤：
     * 1. 设置加载状态
     * 2. 构建可用动作列表
     * 3. 创建发送失败状态
     * 4. 确定是否显示表情反应
     * 5. 合并建议和最近的表情符号
     * 6. 设置成功状态或清除状态
     *
     * @param timelineItem 目标消息事件
     * @param usersEventPermissions 用户权限
     * @param isDeveloperModeEnabled 是否启用开发者模式
     * @param pinnedEventIds 房间已置顶的消息ID列表
     * @param target 状态目标变量
     * @param isThreadsEnabled 是否启用线程功能
     */
    private fun CoroutineScope.computeForMessage(
        timelineItem: TimelineItem.Event,
        usersEventPermissions: UserEventPermissions,
        isDeveloperModeEnabled: Boolean,
        pinnedEventIds: ImmutableList<EventId>,
        target: MutableState<ActionListState.Target>,
        isThreadsEnabled: Boolean,
    ) = launch {
        target.value = ActionListState.Target.Loading(timelineItem)

        val actions = buildActions(
            timelineItem = timelineItem,
            usersEventPermissions = usersEventPermissions,
            isDeveloperModeEnabled = isDeveloperModeEnabled,
            isEventPinned = pinnedEventIds.contains(timelineItem.eventId),
            isThreadsEnabled = isThreadsEnabled,
        )

        val verifiedUserSendFailure = userSendFailureFactory.create(timelineItem.localSendState)
        val displayEmojiReactions = usersEventPermissions.canSendReaction && timelineItem.content.canReact()

        if (actions.isNotEmpty() || displayEmojiReactions || verifiedUserSendFailure != VerifiedUserSendFailure.None) {
            val recentEmojis = getRecentEmojis().getOrNull()?.toImmutableList() ?: persistentListOf()
            target.value = ActionListState.Target.Success(
                event = timelineItem,
                sentTimeFull = dateFormatter.format(
                    timelineItem.sentTimeMillis,
                    DateFormatterMode.Full,
                    useRelative = true,
                ),
                displayEmojiReactions = displayEmojiReactions,
                verifiedUserSendFailure = verifiedUserSendFailure,
                actions = actions.toImmutableList(),
                // Merge suggested and recent emojis, removing duplicates and returning at most 100
                recentEmojis = (suggestedEmojis + recentEmojis).distinct()
                    .take(100)
                    .toImmutableList()
            )
        } else {
            target.value = ActionListState.Target.None
        }
    }

    /**
     * 构建可用动作列表
     *
     * 根据消息内容、用户权限和功能设置构建可用动作列表。
     * 考虑因素包括：
     * - 用户权限（发送消息、删除、置顶等）
     * - 消息内容类型（文本、图片、投票、文件等）
     * - 消息状态（可编辑、可转发、可回复等）
     * - 功能开关（线程、开发者模式等）
     * - 消息位置（线程中、房间中等）
     *
     * @param timelineItem 目标消息事件
     * @param usersEventPermissions 用户权限
     * @param isDeveloperModeEnabled 是否启用开发者模式
     * @param isEventPinned 消息是否已置顶
     * @param isThreadsEnabled 是否启用线程功能
     * @return 可用的动作列表
     */
    private fun buildActions(
        timelineItem: TimelineItem.Event,
        usersEventPermissions: UserEventPermissions,
        isDeveloperModeEnabled: Boolean,
        isEventPinned: Boolean,
        isThreadsEnabled: Boolean,
    ): List<TimelineItemAction> {
        val canRedact = timelineItem.isMine && usersEventPermissions.canRedactOwn || !timelineItem.isMine && usersEventPermissions.canRedactOther
        return buildSet {
            if (timelineItem.canBeRepliedTo && usersEventPermissions.canSendMessage) {
                if (isThreadsEnabled && timelineMode !is Timeline.Mode.Thread && timelineItem.isRemote) {
                    // If threads are enabled, we can reply in thread if the item is remote
                    add(TimelineItemAction.ReplyInThread)
                    add(TimelineItemAction.Reply)
                } else {
                    if (!isThreadsEnabled && timelineItem.threadInfo is TimelineItemThreadInfo.ThreadResponse) {
                        // If threads are not enabled, we can reply in a thread if the item is already in the thread
                        add(TimelineItemAction.ReplyInThread)
                    } else {
                        // Otherwise, we can only reply in the room
                        add(TimelineItemAction.Reply)
                    }
                }
            }
            if (timelineItem.isRemote && timelineItem.content.canBeForwarded()) {
                add(TimelineItemAction.Forward)
            }
            if (timelineItem.isEditable && usersEventPermissions.canSendMessage) {
                if (timelineItem.content is TimelineItemEventContentWithAttachment) {
                    // Caption
                    if (timelineItem.content.caption == null) {
                        add(TimelineItemAction.AddCaption)
                    } else {
                        add(TimelineItemAction.EditCaption)
                        add(TimelineItemAction.RemoveCaption)
                    }
                } else if (timelineItem.content is TimelineItemPollContent) {
                    add(TimelineItemAction.EditPoll)
                } else {
                    add(TimelineItemAction.Edit)
                }
            }
            if (canRedact && timelineItem.content is TimelineItemPollContent && !timelineItem.content.isEnded) {
                add(TimelineItemAction.EndPoll)
            }
            val canPinUnpin = usersEventPermissions.canPinUnpin && timelineItem.isRemote
            if (canPinUnpin) {
                if (isEventPinned) {
                    add(TimelineItemAction.Unpin)
                } else {
                    add(TimelineItemAction.Pin)
                }
            }
            if (timelineItem.content.canBeCopied()) {
                add(TimelineItemAction.CopyText)
            } else if ((timelineItem.content as? TimelineItemEventContentWithAttachment)?.caption.isNullOrBlank().not()) {
                add(TimelineItemAction.CopyCaption)
            }
            if (timelineItem.isRemote) {
                add(TimelineItemAction.CopyLink)
            }
            if (isDeveloperModeEnabled) {
                add(TimelineItemAction.ViewSource)
            }
            if (!timelineItem.isMine) {
                add(TimelineItemAction.ReportContent)
            }
            if (canRedact) {
                add(TimelineItemAction.Redact)
            }
        }
            .postFilter(timelineItem.content)
            .sortedWith(comparator)
            .let(postProcessor::process)
    }
}

/**
 * 根据事件内容过滤动作列表
 *
 * 根据事件内容的类型，过滤出适合该类型事件的可用动作。
 * 某些消息类型只支持有限的操作：
 * - RTC通知和通话邀请：只显示"查看源代码"动作
 * - 状态消息：只显示"查看源代码"动作
 * - 已删除消息（涂黑）：只显示"查看源代码"和"取消置顶"动作
 * - 其他消息类型：显示所有可用动作
 *
 * @param content 事件内容，用于确定消息类型
 * @return 过滤后的动作迭代器，只保留适合该内容类型的动作
 */
private fun Iterable<TimelineItemAction>.postFilter(content: TimelineItemEventContent): Iterable<TimelineItemAction> {
    return filter { action ->
        when (content) {
            is TimelineItemRtcNotificationContent,
            is TimelineItemLegacyCallInviteContent,
            is TimelineItemStateContent -> action == TimelineItemAction.ViewSource
            is TimelineItemRedactedContent -> {
                action == TimelineItemAction.ViewSource || action == TimelineItemAction.Unpin
            }
            else -> true
        }
    }
}
