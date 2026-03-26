/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.permissions.api.PermissionsState
import io.element.android.libraries.permissions.api.aPermissionsState
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间详情编辑状态的预览参数提供者
 *
 * 用于在Compose预览中提供多种状态的 [RoomDetailsEditState] 实例
 */
open class RoomDetailsEditStateProvider : PreviewParameterProvider<RoomDetailsEditState> {
    override val values: Sequence<RoomDetailsEditState>
        get() = sequenceOf(
            aRoomDetailsEditState(),
            aRoomDetailsEditState(roomTopic = ""),
            aRoomDetailsEditState(roomRawName = ""),
            aRoomDetailsEditState(roomAvatarUrl = "example://uri"),
            aRoomDetailsEditState(roomAvatarUrl = "example://uri", isSpace = true, roomTopic = ""),
            aRoomDetailsEditState(canChangeName = true, canChangeTopic = false, canChangeAvatar = true, saveButtonEnabled = false),
            aRoomDetailsEditState(canChangeName = false, canChangeTopic = true, canChangeAvatar = false, saveButtonEnabled = false),
            aRoomDetailsEditState(saveAction = AsyncAction.Loading),
            aRoomDetailsEditState(saveAction = AsyncAction.Failure(RuntimeException("Whelp"))),
            aRoomDetailsEditState(saveAction = AsyncAction.ConfirmingCancellation),
        )
}

/**
 * 创建用于测试和预览的 [RoomDetailsEditState] 实例的便捷函数
 *
 * @param roomId 房间ID，默认值为测试用房间ID
 * @param roomRawName 房间原始名称
 * @param canChangeName 是否可以修改名称
 * @param roomTopic 房间主题
 * @param canChangeTopic 是否可以修改主题
 * @param roomAvatarUrl 房间头像URL
 * @param canChangeAvatar 是否可以修改头像
 * @param avatarActions 头像操作列表
 * @param saveButtonEnabled 保存按钮是否可用
 * @param saveAction 保存操作状态
 * @param cameraPermissionState 相机权限状态
 * @param isSpace 是否为空间
 * @param eventSink 事件处理函数
 * @return 配置好的 [RoomDetailsEditState] 实例
 */
fun aRoomDetailsEditState(
    roomId: RoomId = RoomId("!aRoomId:aDomain"),
    roomRawName: String = "Marketing",
    canChangeName: Boolean = true,
    roomTopic: String = "a room topic that is quite long so should wrap onto multiple lines",
    canChangeTopic: Boolean = true,
    roomAvatarUrl: String? = null,
    canChangeAvatar: Boolean = true,
    avatarActions: List<AvatarAction> = emptyList(),
    saveButtonEnabled: Boolean = true,
    saveAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    cameraPermissionState: PermissionsState = aPermissionsState(showDialog = false),
    isSpace: Boolean = false,
    eventSink: (RoomDetailsEditEvent) -> Unit = {},
) = RoomDetailsEditState(
    roomId = roomId,
    roomRawName = roomRawName,
    canChangeName = canChangeName,
    roomTopic = roomTopic,
    canChangeTopic = canChangeTopic,
    roomAvatarUrl = roomAvatarUrl,
    canChangeAvatar = canChangeAvatar,
    avatarActions = avatarActions.toImmutableList(),
    saveButtonEnabled = saveButtonEnabled,
    saveAction = saveAction,
    cameraPermissionState = cameraPermissionState,
    isSpace = isSpace,
    eventSink = eventSink,
)
