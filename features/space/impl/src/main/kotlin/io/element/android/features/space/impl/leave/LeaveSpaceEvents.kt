/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.leave

import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开 Space 页面可能触发的用户事件。
 */
sealed interface LeaveSpaceEvents {
    /** 重新加载当前离开页面数据。 */
    data object Retry : LeaveSpaceEvents

    /** 选中全部可离开的子房间。 */
    data object SelectAllRooms : LeaveSpaceEvents

    /** 取消选中全部子房间。 */
    data object DeselectAllRooms : LeaveSpaceEvents

    /** 切换某个子房间的选中状态。 */
    data class ToggleRoomSelection(val roomId: RoomId) : LeaveSpaceEvents

    /** 确认执行离开 Space。 */
    data object LeaveSpace : LeaveSpaceEvents

    /** 关闭当前错误提示。 */
    data object CloseError : LeaveSpaceEvents
}
