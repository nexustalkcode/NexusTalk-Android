/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.addroom

import androidx.compose.foundation.text.input.TextFieldState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.matrix.ui.model.SelectRoomInfo
import kotlinx.collections.immutable.ImmutableList

/**
 * 添加房间到空间状态数据类
 *
 * @property searchQuery 搜索查询的文本字段状态
 * @property isSearchActive 搜索是否处于活动状态
 * @property searchResults 搜索结果状态
 * @property selectedRooms 已选中的房间列表
 * @property suggestions 建议房间列表
 * @property saveAction 保存操作的异步状态
 * @property eventSink 事件处理函数
 * @property canSave 是否可以保存
 */
data class AddRoomToSpaceState(
    val searchQuery: TextFieldState,
    val isSearchActive: Boolean,
    val searchResults: SearchBarResultState<ImmutableList<SelectRoomInfo>>,
    val selectedRooms: ImmutableList<SelectRoomInfo>,
    val suggestions: ImmutableList<SelectRoomInfo>,
    val saveAction: AsyncAction<Unit>,
    val eventSink: (AddRoomToSpaceEvent) -> Unit,
) {
    val canSave: Boolean = selectedRooms.isNotEmpty() && !saveAction.isLoading()
}
