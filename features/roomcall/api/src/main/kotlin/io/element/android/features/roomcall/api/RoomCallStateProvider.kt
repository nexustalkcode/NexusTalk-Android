/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomcall.api

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 房间通话状态的预览参数提供者
 *
 * 用于在 Compose 预览中提供不同的房间通话状态样本，
 * 以便开发者查看不同状态下的 UI 效果
 */
open class RoomCallStateProvider : PreviewParameterProvider<RoomCallState> {
    override val values: Sequence<RoomCallState> = sequenceOf(
        aStandByCallState(),
        aStandByCallState(canStartCall = false),
        anOngoingCallState(),
        anOngoingCallState(canJoinCall = false),
        anOngoingCallState(canJoinCall = true, isUserInTheCall = true),
        RoomCallState.Unavailable,
    )
}

/**
 * 创建一个通话进行中的房间通话状态（测试用辅助函数）
 *
 * @param canJoinCall 当前用户是否有权限加入通话，默认为 true
 * @param isUserInTheCall 当前用户是否已加入该通话，默认为 false
 * @param isUserLocallyInTheCall 当前用户是否在本地已加入该通话，默认为与 isUserInTheCall 相同
 * @return RoomCallState.OnGoing 实例
 */
fun anOngoingCallState(
    canJoinCall: Boolean = true,
    isUserInTheCall: Boolean = false,
    isUserLocallyInTheCall: Boolean = isUserInTheCall,
) = RoomCallState.OnGoing(
    canJoinCall = canJoinCall,
    isUserInTheCall = isUserInTheCall,
    isUserLocallyInTheCall = isUserLocallyInTheCall,
)

/**
 * 创建一个待机状态的房间通话状态（测试用辅助函数）
 *
 * @param canStartCall 当前用户是否有权限发起通话，默认为 true
 * @return RoomCallState.StandBy 实例
 */
fun aStandByCallState(
    canStartCall: Boolean = true,
) = RoomCallState.StandBy(
    canStartCall = canStartCall,
)
