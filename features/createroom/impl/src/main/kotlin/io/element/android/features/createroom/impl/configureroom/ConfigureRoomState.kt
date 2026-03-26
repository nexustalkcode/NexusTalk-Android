/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidity
import io.element.android.libraries.permissions.api.PermissionsState
import kotlinx.collections.immutable.ImmutableList

/**
 * 配置房间状态数据类
 *
 * 表示配置房间界面的完整状态，包含房间配置、头像操作、创建动作、权限状态等信息。
 *
 * @property config 房间配置信息
 * @property avatarActions 头像操作列表
 * @property createRoomAction 创建房间的异步操作状态
 * @property cameraPermissionState 相机权限状态
 * @property roomAddressValidity 房间地址有效性
 * @property homeserverName 服务器名称
 * @property availableJoinRules 可用的加入规则列表
 * @property spaces 可用的空间列表
 * @property eventSink 事件处理函数
 */
data class ConfigureRoomState(
    val config: CreateRoomConfig,
    val avatarActions: ImmutableList<AvatarAction>,
    val createRoomAction: AsyncAction<RoomId>,
    val cameraPermissionState: PermissionsState,
    val roomAddressValidity: RoomAddressValidity,
    val homeserverName: String,
    val availableJoinRules: ImmutableList<JoinRuleItem>,
    val spaces: ImmutableList<SpaceRoom>,
    val eventSink: (ConfigureRoomEvents) -> Unit
) {
    /**
     * 配置是否有效
     *
     * 房间名称必须非空，且在私有房间情况下房间地址必须有效
     */
    val isValid: Boolean = config.roomName?.isNotEmpty() == true &&
        (config.visibilityState is RoomVisibilityState.Private || roomAddressValidity == RoomAddressValidity.Valid)
}
