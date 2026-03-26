/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.search

import androidx.compose.foundation.text.input.TextFieldState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import kotlinx.collections.immutable.ImmutableList

/**
 * 房间列表搜索状态数据类
 *
 * 表示房间列表搜索的完整状态。
 *
 * @property isSearchActive 是否正在搜索
 * @property query 搜索查询文本字段状态
 * @property results 搜索结果列表
 * @property eventSink 事件处理函数
 */
data class RoomListSearchState(
    /** 是否正在搜索 */
    val isSearchActive: Boolean,
    /** 搜索查询文本字段状态 */
    val query: TextFieldState,
    /** 搜索结果列表 */
    val results: ImmutableList<RoomListRoomSummary>,
    /** 事件处理函数 */
    val eventSink: (RoomListSearchEvents) -> Unit
)
