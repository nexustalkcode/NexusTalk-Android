/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.leaveroom.api.LeaveRoomRenderer
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开房间内部渲染器
 *
 * 提供 LeaveRoomRenderer 接口的内部实现，用于在会话范围内渲染离开房间 UI。
 * 该渲染器将 InternalLeaveRoomState 转换为可视化的 UI 组件。
 *
 * @see LeaveRoomRenderer 离开房间渲染器接口
 * @see InternalLeaveRoomState 离开房间内部状态
 */
@ContributesBinding(SessionScope::class)
class InternalLeaveRoomRenderer : LeaveRoomRenderer {
    /**
     * 渲染离开房间 UI
     *
     * @param state 离开房间状态
     * @param onSelectNewOwners 选择新所有者回调
     * @param modifier 修饰符
     */
    @Composable
    override fun Render(state: LeaveRoomState, onSelectNewOwners: (RoomId) -> Unit, modifier: Modifier) {
        if (state is InternalLeaveRoomState) {
            LeaveRoomView(state, onSelectNewOwners)
        } else {
            error("Unsupported state type ${state.javaClass}")
        }
    }
}
