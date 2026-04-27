/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.filters.selection

import io.element.android.features.home.impl.filters.RoomListFilter
import kotlinx.coroutines.flow.StateFlow

/**
 * 房间列表筛选项选择策略接口。
 */
interface FilterSelectionStrategy {
    /** 当前所有筛选项的展示状态。 */
    val filterSelectionStates: StateFlow<Set<FilterSelectionState>>

    /** 选中指定筛选项。 */
    fun select(filter: RoomListFilter)
    /** 取消选中指定筛选项。 */
    fun deselect(filter: RoomListFilter)
    /** 判断指定筛选项是否已被选中。 */
    fun isSelected(filter: RoomListFilter): Boolean
    /** 清空当前已选项。 */
    fun clear()

    /** 切换指定筛选项的选中状态。 */
    fun toggle(filter: RoomListFilter) {
        if (isSelected(filter)) {
            deselect(filter)
        } else {
            select(filter)
        }
    }
}
