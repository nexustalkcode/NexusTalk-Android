/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.api

import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开房间事件接口
 *
 * 定义离开房间功能的事件类型，使用密封接口实现类型安全的事件处理。
 *
 * @see LeaveRoomEvent.LeaveRoom 离开房间事件
 */
interface LeaveRoomEvent {
    /**
     * 离开房间事件
     *
     * @property roomId 要离开的房间 ID
     * @property needsConfirmation 是否需要显示确认对话框
     */
    data class LeaveRoom(val roomId: RoomId, val needsConfirmation: Boolean) : LeaveRoomEvent
}
