/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.filters

import io.element.android.features.home.impl.filters.selection.FilterSelectionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间列表筛选器展示状态。
 *
 * @property filterSelectionStates 每个筛选项当前的选择状态。
 * @property eventSink 筛选事件分发函数。
 */
data class RoomListFiltersState(
    val filterSelectionStates: ImmutableList<FilterSelectionState>,
    val eventSink: (RoomListFiltersEvents) -> Unit,
) {
    /** 当前是否至少有一个筛选项被选中。 */
    val hasAnyFilterSelected = filterSelectionStates.any { it.isSelected }

    /** 取出当前所有已选筛选项。 */
    fun selectedFilters(): ImmutableList<RoomListFilter> {
        return filterSelectionStates
            .filter { it.isSelected }
            .map { it.filter }
            .toImmutableList()
    }
}
