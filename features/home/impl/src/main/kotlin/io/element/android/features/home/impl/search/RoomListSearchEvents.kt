/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.search

/**
 * 房间列表搜索事件密封接口
 *
 * 定义房间列表搜索可能发生的用户交互事件。
 */
sealed interface RoomListSearchEvents {
    /** 切换搜索结果可见性 */
    data object ToggleSearchVisibility : RoomListSearchEvents
    /** 清除搜索查询 */
    data object ClearQuery : RoomListSearchEvents
}
