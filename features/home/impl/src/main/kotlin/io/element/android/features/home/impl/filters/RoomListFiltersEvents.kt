/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.filters

/**
 * 房间列表筛选器可能触发的用户事件。
 */
sealed interface RoomListFiltersEvents {
    /** 切换指定筛选项的选中状态。 */
    data class ToggleFilter(val filter: RoomListFilter) : RoomListFiltersEvents
    /** 清空所有已选筛选项。 */
    data object ClearSelectedFilters : RoomListFiltersEvents
}
