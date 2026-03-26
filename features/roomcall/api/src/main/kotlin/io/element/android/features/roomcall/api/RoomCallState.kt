/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomcall.api

import androidx.compose.runtime.Immutable
import io.element.android.features.roomcall.api.RoomCallState.OnGoing
import io.element.android.features.roomcall.api.RoomCallState.StandBy

/**
 * 房间通话状态密封接口
 *
 * 用于表示房间通话的当前状态，可能处于以下几种状态：
 * - 不可用（Unavailable）：通话功能不可用
 * - 待机状态（StandBy）：房间暂无通话，可以发起通话
 * - 进行中（OnGoing）：房间正在进行通话
 */
@Immutable
sealed interface RoomCallState {
    /**
     * 通话功能不可用状态
     * 表示当前会话/房间不支持通话功能
     */
    data object Unavailable : RoomCallState

    /**
     * 待机状态
     * 房间暂无通话，用户可以发起通话
     *
     * @property canStartCall 当前用户是否有权限发起通话
     */
    data class StandBy(
        val canStartCall: Boolean,
    ) : RoomCallState

    /**
     * 通话进行中状态
     * 房间内已有通话正在进行
     *
     * @property canJoinCall 当前用户是否有权限加入通话
     * @property isUserInTheCall 当前用户是否已加入该通话（从服务端同步）
     * @property isUserLocallyInTheCall 当前用户是否在本地已加入该通话
     */
    data class OnGoing(
        val canJoinCall: Boolean,
        val isUserInTheCall: Boolean,
        val isUserLocallyInTheCall: Boolean,
    ) : RoomCallState
}

/**
 * 检查当前房间通话状态是否具有加入/发起通话的权限
 *
 * @return 如果当前状态允许用户加入或发起通话返回 true，否则返回 false
 */
fun RoomCallState.hasPermissionToJoin() = when (this) {
    RoomCallState.Unavailable -> false
    is StandBy -> canStartCall
    is OnGoing -> canJoinCall
}
