/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.root

import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom

/**
 * Space 主页可能触发的用户事件。
 */
sealed interface SpaceEvents {
    /** 继续分页加载更多子房间。 */
    data object LoadMore : SpaceEvents

    /** 加入指定的子房间或子空间。 */
    data class Join(val spaceRoom: SpaceRoom) : SpaceEvents

    /** 清理当前加入失败状态。 */
    data object ClearFailures : SpaceEvents

    /** 接受指定的 space 邀请。 */
    data class AcceptInvite(val spaceRoom: SpaceRoom) : SpaceEvents

    /** 拒绝指定的 space 邀请。 */
    data class DeclineInvite(val spaceRoom: SpaceRoom) : SpaceEvents

    /** 显示 topic 查看器。 */
    data class ShowTopicViewer(val topic: String) : SpaceEvents

    /** 隐藏 topic 查看器。 */
    data object HideTopicViewer : SpaceEvents

    /** 进入管理模式。 */
    data object EnterManageMode : SpaceEvents

    /** 退出管理模式。 */
    data object ExitManageMode : SpaceEvents

    /** 切换某个子房间的选中状态。 */
    data class ToggleRoomSelection(val roomId: RoomId) : SpaceEvents

    /** 确认执行移除选中房间。 */
    data object ConfirmRoomRemoval : SpaceEvents

    /** 请求移除当前已选房间。 */
    data object RemoveSelectedRooms : SpaceEvents

    /** 清理移除动作状态。 */
    data object ClearRemoveAction : SpaceEvents
}
