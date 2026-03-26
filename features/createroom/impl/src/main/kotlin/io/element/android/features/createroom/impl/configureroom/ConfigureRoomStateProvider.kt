/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.ui.components.aMatrixUserList
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidity
import io.element.android.libraries.permissions.api.PermissionsState
import io.element.android.libraries.permissions.api.aPermissionsState
import io.element.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.toImmutableList

/**
 * 配置房间状态提供者
 *
 * 用于预览和测试的配置房间状态数据提供器。
 * 继承自 PreviewParameterProvider，提供多种场景下的配置房间状态用于 UI 预览。
 *
 * @see ConfigureRoomState 配置房间状态的数据类
 */
open class ConfigureRoomStateProvider : PreviewParameterProvider<ConfigureRoomState> {
    override val values: Sequence<ConfigureRoomState>
        get() = sequenceOf(
            aConfigureRoomState(),
            aConfigureRoomState(
                isKnockFeatureEnabled = false,
                config = CreateRoomConfig(
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    invites = aMatrixUserList().toImmutableList(),
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Room-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.AskToJoin,
                    ),
                ),
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    invites = aMatrixUserList().toImmutableList(),
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Room-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.AskToJoin,
                    ),
                ),
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Room-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.AskToJoin,
                    ),
                ),
                roomAddressValidity = RoomAddressValidity.NotAvailable,
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Room-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.AskToJoin,
                    ),
                ),
                roomAddressValidity = RoomAddressValidity.InvalidSymbols,
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Room-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.AskToJoin,
                    ),
                ),
                roomAddressValidity = RoomAddressValidity.Valid,
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    isSpace = true,
                    roomName = "Space 101",
                    topic = "Space topic for this space when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Space-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.Public,
                    ),
                ),
                roomAddressValidity = RoomAddressValidity.Valid,
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    isSpace = false,
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    parentSpace = null,
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Space-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.Restricted(aSpaceRoom().roomId),
                    ),
                ),
                spaces = listOf(aSpaceRoom()),
                roomAddressValidity = RoomAddressValidity.Valid,
            ),
            aConfigureRoomState(
                config = CreateRoomConfig(
                    isSpace = false,
                    roomName = "Room 101",
                    topic = "Room topic for this room when the text goes onto multiple lines and is really long, there shouldn’t be more than 3 lines",
                    parentSpace = aSpaceRoom(canonicalAlias = RoomAlias("#a-space-room:example.org")),
                    visibilityState = RoomVisibilityState.Public(
                        roomAddress = RoomAddress.AutoFilled("Space-101"),
                        joinRuleItem = JoinRuleItem.PublicVisibility.Restricted(aSpaceRoom().roomId),
                    ),
                ),
                spaces = listOf(aSpaceRoom()),
                roomAddressValidity = RoomAddressValidity.Valid,
            ),
        )
}

/**
 * 创建配置房间状态的辅助函数
 *
 * 用于测试和预览中快速创建 ConfigureRoomState 实例的便捷函数。
 * 提供默认参数，可根据需要覆盖特定属性。
 *
 * @param config 房间配置信息，默认创建空配置
 * @param isKnockFeatureEnabled 是否启用敲门功能，默认启用
 * @param avatarActions 头像操作列表，默认空列表
 * @param createRoomAction 创建房间的异步操作状态，默认未初始化
 * @param cameraPermissionState 相机权限状态，默认不显示对话框
 * @param homeserverName 服务器名称，默认 "matrix.org"
 * @param roomAddressValidity 房间地址有效性，默认有效
 * @param availableVisibilityOptions 可用的可见性选项，根据是否有父空间动态生成
 * @param spaces 可用的空间列表，默认空列表
 * @param eventSink 事件处理函数，默认空函数
 * @return 配置房间状态实例
 */
fun aConfigureRoomState(
    config: CreateRoomConfig = CreateRoomConfig(),
    isKnockFeatureEnabled: Boolean = true,
    avatarActions: List<AvatarAction> = emptyList(),
    createRoomAction: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    cameraPermissionState: PermissionsState = aPermissionsState(showDialog = false),
    homeserverName: String = "matrix.org",
    roomAddressValidity: RoomAddressValidity = RoomAddressValidity.Valid,
    availableVisibilityOptions: List<JoinRuleItem> = if (config.parentSpace != null) {
        listOfNotNull(
            JoinRuleItem.PublicVisibility.Restricted(config.parentSpace.roomId),
            JoinRuleItem.PublicVisibility.AskToJoinRestricted(config.parentSpace.roomId).takeIf { isKnockFeatureEnabled },
            JoinRuleItem.Private,
        )
    } else {
        listOfNotNull(
            JoinRuleItem.PublicVisibility.Public,
            JoinRuleItem.PublicVisibility.AskToJoin.takeIf { isKnockFeatureEnabled },
            JoinRuleItem.Private,
        )
    },
    spaces: List<SpaceRoom> = emptyList(),
    eventSink: (ConfigureRoomEvents) -> Unit = { },
) = ConfigureRoomState(
    config = config,
    avatarActions = avatarActions.toImmutableList(),
    createRoomAction = createRoomAction,
    cameraPermissionState = cameraPermissionState,
    homeserverName = homeserverName,
    roomAddressValidity = roomAddressValidity,
    availableJoinRules = availableVisibilityOptions.toImmutableList(),
    spaces = spaces.toImmutableList(),
    eventSink = eventSink,
)
