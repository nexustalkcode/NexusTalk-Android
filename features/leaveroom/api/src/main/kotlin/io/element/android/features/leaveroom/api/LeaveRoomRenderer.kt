/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 离开房间渲染器函数式接口
 *
 * 定义离开房间功能的渲染接口，使用 Jetpack Compose 实现 UI 渲染。
 * 该接口支持在需要选择新房间所有者的场景下传递回调。
 *
 * @param state 当前离开房间状态
 * @param onSelectNewOwners 选择新所有者时的回调，参数为房间 ID
 * @param modifier 修饰符
 */
fun interface LeaveRoomRenderer {
    /**
     * 渲染离开房间 UI
     *
     * @param state 离开房间状态
     * @param onSelectNewOwners 选择新所有者回调
     * @param modifier 修饰符
     */
    @Composable
    fun Render(
        state: LeaveRoomState,
        onSelectNewOwners: (RoomId) -> Unit,
        modifier: Modifier,
    )
}
