/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomcall.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.features.call.api.CurrentCall
import io.element.android.features.call.api.CurrentCallService
import io.element.android.features.enterprise.api.SessionEnterpriseService
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.powerlevels.canCall
import io.element.android.libraries.matrix.api.room.powerlevels.permissionsAsState

/**
 * 房间通话状态 Presenter
 *
 * 负责管理与房间通话相关的状态逻辑，
 * 根据当前房间信息、用户权限和企业服务配置，
 * 生成合适的 RoomCallState 状态供 UI 使用
 *
 * @property room 已加入的房间实例，用于获取房间信息和权限
 * @property currentCallService 当前通话服务，用于检查本地通话状态
 * @property sessionEnterpriseService 企业会话服务，用于检查通话功能是否可用
 */
@Inject
class RoomCallStatePresenter(
    private val room: JoinedRoom,
    private val currentCallService: CurrentCallService,
    private val sessionEnterpriseService: SessionEnterpriseService,
) : Presenter<RoomCallState> {
    @Composable
    override fun present(): RoomCallState {
        // 通话功能是否可用（通过企业服务检查）
        val isAvailable by produceState(false) {
            value = sessionEnterpriseService.isElementCallAvailable()
        }
        // 房间信息流，包含房间通话状态等信息
        val roomInfo by room.roomInfoFlow.collectAsState()
        // 当前用户是否有权限发起/加入通话
        val canJoinCall by room.permissionsAsState(false) { perms -> perms.canCall() }
        // 当前用户是否已在房间通话中（从服务端同步）
        val isUserInTheCall by remember {
            derivedStateOf {
                room.sessionId in roomInfo.activeRoomCallParticipants
            }
        }
        // 当前通话服务中的通话状态
        val currentCall by currentCallService.currentCall.collectAsState()
        // 当前用户是否在本地已加入该房间通话
        val isUserLocallyInTheCall by remember {
            derivedStateOf {
                (currentCall as? CurrentCall.RoomCall)?.roomId == room.roomId
            }
        }
        // 根据各项状态计算最终的通话状态
        val callState by remember {
            derivedStateOf {
                when {
                    // 通话功能不可用
                    isAvailable.not() -> RoomCallState.Unavailable
                    // 房间已有通话正在进行
                    roomInfo.hasRoomCall -> RoomCallState.OnGoing(
                        canJoinCall = canJoinCall,
                        isUserInTheCall = isUserInTheCall,
                        isUserLocallyInTheCall = isUserLocallyInTheCall,
                    )
                    // 房间无通话，用户可以发起
                    else -> RoomCallState.StandBy(canStartCall = canJoinCall)
                }
            }
        }
        return callState
    }
}
