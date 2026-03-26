/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.pollcontent

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.poll.PollAnswer
import io.element.android.libraries.matrix.api.poll.PollKind
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 创建测试用的投票问题
 *
 * @return String 测试用问题文本
 */
fun aPollQuestion() = "What type of food should we have at the party?"

/**
 * 创建测试用的投票答案列表
 *
 * @param hasVotes 是否有投票
 * @param isEnded 投票是否已结束
 * @param showVotes 是否显示投票数
 * @return ImmutableList<PollAnswerItem> 答案列表
 */
fun aPollAnswerItemList(
    hasVotes: Boolean = true,
    isEnded: Boolean = false,
    showVotes: Boolean = true,
) = persistentListOf(
    aPollAnswerItem(
        answer = PollAnswer("option_1", "Italian \uD83C\uDDEE\uD83C\uDDF9"),
        showVotes = showVotes,
        isEnabled = !isEnded,
        isWinner = isEnded,
        votesCount = if (hasVotes) 5 else 0,
        percentage = if (hasVotes) 0.5f else 0f
    ),
    aPollAnswerItem(
        answer = PollAnswer("option_2", "Chinese \uD83C\uDDE8\uD83C\uDDF3"),
        showVotes = showVotes,
        isEnabled = !isEnded,
        isWinner = false,
        votesCount = 0,
        percentage = 0f
    ),
    aPollAnswerItem(
        answer = PollAnswer("option_3", "Brazilian \uD83C\uDDE7\uD83C\uDDF7"),
        showVotes = showVotes,
        isEnabled = !isEnded,
        isWinner = false,
        isSelected = true,
        votesCount = if (hasVotes) 1 else 0,
        percentage = if (hasVotes) 0.1f else 0f
    ),
    aPollAnswerItem(
        showVotes = showVotes,
        isEnabled = !isEnded,
        votesCount = if (hasVotes) 4 else 0,
        percentage = if (hasVotes) 0.4f else 0f,
    ),
)

/**
 * 创建测试用的单个投票答案项
 *
 * @param answer 投票答案对象
 * @param isSelected 是否被选中
 * @param isEnabled 是否可用
 * @param isWinner 是否获胜
 * @param showVotes 是否显示投票数
 * @param votesCount 投票数量
 * @param percentage 投票百分比
 * @return PollAnswerItem 答案项
 */
fun aPollAnswerItem(
    answer: PollAnswer = PollAnswer(
        "option_4",
        "French \uD83C\uDDEB\uD83C\uDDF7 But make it a very very very long option then this should just keep expanding"
    ),
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    isWinner: Boolean = false,
    showVotes: Boolean = true,
    votesCount: Int = 4,
    percentage: Float = 0.4f,
) = PollAnswerItem(
    answer = answer,
    isSelected = isSelected,
    isEnabled = isEnabled,
    isWinner = isWinner,
    showVotes = showVotes,
    votesCount = votesCount,
    percentage = percentage
)

/**
 * 创建测试用的投票内容状态
 *
 * @param eventId 事件 ID
 * @param isMine 是否由当前用户创建
 * @param isEnded 投票是否已结束
 * @param showVotes 是否显示投票数
 * @param isPollEditable 投票是否可编辑
 * @param hasVotes 是否有投票
 * @param question 投票问题
 * @param pollKind 投票类型
 * @param answerItems 答案列表
 * @return PollContentState 投票内容状态
 */
fun aPollContentState(
    eventId: EventId? = null,
    isMine: Boolean = false,
    isEnded: Boolean = false,
    showVotes: Boolean = true,
    isPollEditable: Boolean = true,
    hasVotes: Boolean = true,
    question: String = aPollQuestion(),
    pollKind: PollKind = PollKind.Disclosed,
    answerItems: ImmutableList<PollAnswerItem> = aPollAnswerItemList(
        isEnded = isEnded,
        showVotes = showVotes,
        hasVotes = hasVotes
    ),
) = PollContentState(
    eventId = eventId,
    question = question,
    answerItems = answerItems,
    pollKind = pollKind,
    isPollEditable = isMine && !isEnded && isPollEditable,
    isPollEnded = isEnded,
    isMine = isMine,
)
