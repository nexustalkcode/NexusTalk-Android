/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.impl.root

/**
 * 房间目录事件密封接口
 *
 * 定义了房间目录界面中所有用户交互事件的类型。
 */
sealed interface RoomDirectoryEvents {
    /**
     * 搜索房间事件
     *
     * @property query 搜索关键词
     */
    data class Search(val query: String) : RoomDirectoryEvents

    /**
     * 加载更多房间事件
     *
     * 当用户滚动到列表底部时触发，加载更多房间数据。
     */
    data object LoadMore : RoomDirectoryEvents
}
