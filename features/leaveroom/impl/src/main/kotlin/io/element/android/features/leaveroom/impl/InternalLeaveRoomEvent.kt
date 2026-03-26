/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl

import io.element.android.features.leaveroom.api.LeaveRoomEvent

/**
 * 离开房间内部事件接口
 *
 * 定义离开房间功能的内部事件，继承自 LeaveRoomEvent 接口。
 * 用于处理内部状态管理和事件流转。
 *
 * @see InternalLeaveRoomEvent.ResetState 重置状态事件
 */
sealed interface InternalLeaveRoomEvent : LeaveRoomEvent {
    /**
     * 重置状态事件
     *
     * 用于将离开房间操作状态重置为初始状态
     */
    data object ResetState : InternalLeaveRoomEvent
}
