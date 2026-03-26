/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.filters.selection

import io.element.android.features.home.impl.filters.RoomListFilter

/**
 * 过滤器选择状态数据类
 *
 * @property filter 房间列表过滤器
 * @property isSelected 是否被选中
 */
data class FilterSelectionState(
    val filter: RoomListFilter,
    val isSelected: Boolean,
)
