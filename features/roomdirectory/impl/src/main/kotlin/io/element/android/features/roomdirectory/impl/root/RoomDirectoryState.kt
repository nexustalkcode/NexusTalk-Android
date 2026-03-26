/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.impl.root

import io.element.android.features.roomdirectory.api.RoomDescription
import kotlinx.collections.immutable.ImmutableList

/**
 * 房间目录状态数据类
 *
 * @property query 搜索查询
 * @property roomDescriptions 房间描述列表
 * @property displayLoadMoreIndicator 是否显示加载更多指示器
 * @property eventSink 事件处理函数
 * @property displayEmptyState 是否显示空状态
 */
data class RoomDirectoryState(
    val query: String,
    val roomDescriptions: ImmutableList<RoomDescription>,
    val displayLoadMoreIndicator: Boolean,
    val eventSink: (RoomDirectoryEvents) -> Unit
) {
    val displayEmptyState = roomDescriptions.isEmpty() && !displayLoadMoreIndicator
}
