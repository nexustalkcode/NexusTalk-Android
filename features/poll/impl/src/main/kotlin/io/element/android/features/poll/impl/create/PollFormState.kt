/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.create

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import io.element.android.features.poll.impl.PollConstants
import io.element.android.features.poll.impl.PollConstants.MIN_ANSWERS
import io.element.android.libraries.matrix.api.poll.PollKind
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 投票表单状态数据类
 *
 * 表示投票创建/编辑表单的状态，包含问题、答案列表和投票类型。
 * 可通过 pollFormStateSaver 保存和恢复状态。
 *
 * @property question 投票问题
 * @property answers 答案列表
 * @property isDisclosed 是否公开（true 为公开，false 为匿名）
 */
data class PollFormState(
    val question: String,
    val answers: ImmutableList<String>,
    val isDisclosed: Boolean,
) {
    companion object {
        /** 空表单状态 - 用于初始化新建投票表单 */
        val Empty = PollFormState(
            question = "",
            answers = MutableList(MIN_ANSWERS) { "" }.toImmutableList(),
            isDisclosed = true,
        )
    }

    /**
     * 投票类型属性
     *
     * 根据 isDisclosed 属性返回对应的投票类型。
     *
     * @return PollKind 投票类型（Disclosed 或 Undisclosed）
     */
    val pollKind
        get() = when (isDisclosed) {
            true -> PollKind.Disclosed
            false -> PollKind.Undisclosed
        }

    /**
     * Create a copy of the [PollFormState] with a new blank answer added.
     *
     * If the maximum number of answers has already been reached an answer is not added.
     */
    fun withNewAnswer(): PollFormState {
        if (!canAddAnswer) {
            return this
        }

        return copy(answers = (answers + "").toImmutableList())
    }

    /**
     * Create a copy of the [PollFormState] with the answer at [index] removed.
     *
     * If the answer doesn't exist or can't be removed, the state is unchanged.
     *
     * @param index the index of the answer to remove.
     *
     * @return a new [PollFormState] with the answer at [index] removed.
     */
    fun withAnswerRemoved(index: Int): PollFormState {
        if (!canDeleteAnswer) {
            return this
        }

        return copy(answers = answers.filterIndexed { i, _ -> i != index }.toImmutableList())
    }

    /**
     * Create a copy of the [PollFormState] with the answer at [index] changed.
     *
     * If the new answer is longer than [PollConstants.MAX_ANSWER_LENGTH], it will be truncated.
     *
     * @param index the index of the answer to change.
     * @param rawAnswer the new answer as the user typed it.
     *
     * @return a new [PollFormState] with the answer at [index] changed.
     */
    fun withAnswerChanged(index: Int, rawAnswer: String): PollFormState =
        copy(answers = answers.toMutableList().apply {
            this[index] = rawAnswer.take(PollConstants.MAX_ANSWER_LENGTH)
        }.toImmutableList())

    /**
     * 是否可以添加答案
     *
     * 当答案数量未达到最大限制时可以添加新答案。
     *
     * @return Boolean 是否可以添加
     */
    val canAddAnswer get() = answers.size < PollConstants.MAX_ANSWERS

    /**
     * 是否可以删除答案
     *
     * 当答案数量大于最小限制时可以删除答案。
     *
     * @return Boolean 是否可以删除
     */
    val canDeleteAnswer get() = answers.size > MIN_ANSWERS

    /**
     * 表单是否有效
     *
     * 有效条件：问题不为空、答案数量不少于最小限制、所有答案都不为空。
     *
     * @return Boolean 表单是否有效
     */
    val isValid get() = question.isNotBlank() && answers.size >= MIN_ANSWERS && answers.all { it.isNotBlank() }
}

/**
 * 投票表单状态保存器
 *
 * 用于保存和恢复 PollFormState 的 Compose Saver。
 * 将表单状态转换为可序列化的格式以便保存。
 */
internal val pollFormStateSaver = mapSaver(
    save = {
        mutableMapOf(
            "question" to it.question,
            "answers" to it.answers.toTypedArray(),
            "isDisclosed" to it.isDisclosed,
        )
    },
    restore = { saved ->
        PollFormState(
            question = saved["question"] as String,
            answers = (saved["answers"] as Array<*>).map { it as String }.toImmutableList(),
            isDisclosed = saved["isDisclosed"] as Boolean,
        )
    }
)
