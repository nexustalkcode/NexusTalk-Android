/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.api

import androidx.compose.runtime.Immutable

/**
 * 离开房间状态接口
 *
 * 定义离开房间功能的公共状态接口，用于在模块间传递状态信息。
 * 该接口是 Compose 不可变数据，确保 UI 的可预测性和性能优化。
 *
 * @property eventSink 事件处理函数，用于发送离开房间相关事件
 * @see InternalLeaveRoomState 内部状态实现
 */
@Immutable
interface LeaveRoomState {
    /** 事件处理函数 */
    val eventSink: (LeaveRoomEvent) -> Unit
}
