/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history

import io.element.android.features.poll.impl.history.model.PollHistoryFilter
import io.element.android.features.poll.impl.history.model.PollHistoryItem
import io.element.android.features.poll.impl.history.model.PollHistoryItems
import kotlinx.collections.immutable.ImmutableList

/**
 * 投票历史状态数据类
 *
 * 表示投票历史界面的完整状态。
 *
 * @property isLoading 是否正在加载
 * @property hasMoreToLoad 是否还有更多数据可加载
 * @property activeFilter 当前活动的过滤器
 * @property pollHistoryItems 投票历史项集合
 * @property eventSink 事件处理函数
 */
data class PollHistoryState(
    val isLoading: Boolean,
    val hasMoreToLoad: Boolean,
    val activeFilter: PollHistoryFilter,
    val pollHistoryItems: PollHistoryItems,
    val eventSink: (PollHistoryEvents) -> Unit,
) {
    /**
     * 根据过滤器获取对应的投票历史列表
     *
     * @param filter 投票历史过滤器
     * @return 过滤后的投票历史列表
     */
    fun pollHistoryForFilter(filter: PollHistoryFilter): ImmutableList<PollHistoryItem> {
        return when (filter) {
            PollHistoryFilter.ONGOING -> pollHistoryItems.ongoing
            PollHistoryFilter.PAST -> pollHistoryItems.past
        }
    }
}
