/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history

import io.element.android.features.poll.impl.history.model.PollHistoryFilter
import io.element.android.libraries.matrix.api.core.EventId

/**
 * 投票历史事件密封接口
 *
 * 定义了投票历史界面中所有可能发生的用户事件。
 */
sealed interface PollHistoryEvents {
    /** 加载更多事件 - 触发加载更多历史投票 */
    data object LoadMore : PollHistoryEvents

    /**
     * 选择投票答案事件 - 用户选择了某个投票的答案
     *
     * @property pollStartId 投票开始事件 ID
     * @property answerId 选择的答案 ID
     */
    data class SelectPollAnswer(val pollStartId: EventId, val answerId: String) : PollHistoryEvents

    /**
     * 结束投票事件 - 用户结束指定的投票
     *
     * @property pollStartId 投票开始事件 ID
     */
    data class EndPoll(val pollStartId: EventId) : PollHistoryEvents

    /**
     * 选择过滤器事件 - 用户切换投票历史过滤器
     *
     * @property filter 投票历史过滤器（进行中/已结束）
     */
    data class SelectFilter(val filter: PollHistoryFilter) : PollHistoryEvents
}
