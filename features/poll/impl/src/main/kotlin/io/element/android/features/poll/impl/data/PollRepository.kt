/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.data

import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.core.extensions.flatMap
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.poll.PollKind
import io.element.android.libraries.matrix.api.room.CreateTimelineParams
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.TimelineProvider
import io.element.android.libraries.matrix.api.timeline.getActiveTimeline
import io.element.android.libraries.matrix.api.timeline.item.event.PollContent
import io.element.android.libraries.matrix.api.timeline.item.event.toEventOrTransactionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

/**
 * 投票数据仓库
 *
 * 负责处理投票相关的数据操作，包括获取、创建、编辑和删除投票。
 *
 * @property room 已加入的房间
 * @property defaultTimelineProvider 默认时间线提供者
 * @property timelineMode 时间线模式
 */
@AssistedInject
class PollRepository(
    private val room: JoinedRoom,
    private val defaultTimelineProvider: TimelineProvider,
    @Assisted private val timelineMode: Timeline.Mode,
) {
    /**
     * 仓库工厂接口
     *
     * 用于创建 PollRepository 实例。
     */
    @AssistedFactory
    fun interface Factory {
        fun create(
            timelineMode: Timeline.Mode,
        ): PollRepository
    }

    /**
     * 获取投票
     *
     * 根据事件 ID 获取对应的投票内容。
     *
     * @param eventId 投票事件 ID
     * @return Result<PollContent> 投票内容结果
     */
    suspend fun getPoll(eventId: EventId): Result<PollContent> = runCatchingExceptions {
        getTimelineProvider()
            .getOrThrow()
            .getActiveTimeline()
            .timelineItems
            .first()
            .asSequence()
            .filterIsInstance<MatrixTimelineItem.Event>()
            .first { it.eventId == eventId }
            .event
            .content as PollContent
    }

    /**
     * 保存投票
     *
     * 创建新投票或编辑现有投票。
     *
     * @param existingPollId 现有投票 ID（如果为 null 表示创建新投票）
     * @param question 投票问题
     * @param answers 投票答案列表
     * @param pollKind 投票类型
     * @param maxSelections 最大可选数量
     * @return Result<Unit> 保存结果
     */
    suspend fun savePoll(
        existingPollId: EventId?,
        question: String,
        answers: List<String>,
        pollKind: PollKind,
        maxSelections: Int,
    ): Result<Unit> = when (existingPollId) {
        null -> getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider
                .getActiveTimeline()
                .createPoll(
                    question = question,
                    answers = answers,
                    maxSelections = maxSelections,
                    pollKind = pollKind,
                )
        }
        else -> getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider.getActiveTimeline()
                .editPoll(
                    pollStartId = existingPollId,
                    question = question,
                    answers = answers,
                    maxSelections = maxSelections,
                    pollKind = pollKind,
                )
        }
    }

    /**
     * 删除投票
     *
     * 根据投票开始事件 ID 删除指定的投票。
     *
     * @param pollStartId 投票开始事件 ID
     * @return Result<Unit> 删除结果
     */
    suspend fun deletePoll(
        pollStartId: EventId,
    ): Result<Unit> =
        getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider.getActiveTimeline()
                .redactEvent(
                    eventOrTransactionId = pollStartId.toEventOrTransactionId(),
                    reason = null,
                )
        }

    private suspend fun getTimelineProvider(): Result<TimelineProvider> {
        return when (timelineMode) {
            is Timeline.Mode.Thread -> {
                val threadedTimelineResult = room.createTimeline(CreateTimelineParams.Threaded(timelineMode.threadRootId))
                threadedTimelineResult.map { threadedTimeline ->
                    object : TimelineProvider {
                        private val flow = MutableStateFlow<Timeline?>(threadedTimeline)
                        override fun activeTimelineFlow(): StateFlow<Timeline?> = flow
                    }
                }
            }
            else -> Result.success(defaultTimelineProvider)
        }
    }
}
