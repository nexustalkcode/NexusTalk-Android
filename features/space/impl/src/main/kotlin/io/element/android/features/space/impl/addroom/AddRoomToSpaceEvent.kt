/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.addroom

import io.element.android.libraries.matrix.ui.model.SelectRoomInfo

/**
 * “向 Space 添加房间”页面可能触发的用户事件。
 */
sealed interface AddRoomToSpaceEvent {
    /** 切换某个房间的选中状态。 */
    data class ToggleRoom(val room: SelectRoomInfo) : AddRoomToSpaceEvent
    /** 切换搜索栏是否处于激活状态。 */
    data class OnSearchActiveChanged(val active: Boolean) : AddRoomToSpaceEvent
    /** 保存当前选中的房间。 */
    data object Save : AddRoomToSpaceEvent
    /** 重置保存动作状态。 */
    data object ResetSaveAction : AddRoomToSpaceEvent
}
