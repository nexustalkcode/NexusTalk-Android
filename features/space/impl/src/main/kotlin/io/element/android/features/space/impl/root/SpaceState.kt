/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.root

import androidx.compose.runtime.Immutable
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

/**
 * 空间状态数据类
 *
 * @property spaceInfo 空间信息
 * @property children 子空间列表
 * @property seenSpaceInvites 已查看的空间邀请 ID 集合
 * @property hideInvitesAvatar 是否隐藏邀请头像
 * @property hasMoreToLoad 是否还有更多内容可以加载
 * @property joinActions 加入操作的异步状态映射
 * @property acceptDeclineInviteState 接受/拒绝邀请状态
 * @property topicViewerState 话题查看器状态
 * @property canAccessSpaceSettings 是否可以访问空间设置
 * @property isManageMode 是否为管理模式
 * @property selectedRoomIds 已选中的房间 ID 集合
 * @property canEditSpaceGraph 是否可以编辑空间图
 * @property removeRoomsAction 移除房间操作的异步状态
 * @property eventSink 事件处理函数
 */
data class SpaceState(
    val spaceInfo: RoomInfo,
    val children: ImmutableList<SpaceRoom>,
    val seenSpaceInvites: ImmutableSet<RoomId>,
    val hideInvitesAvatar: Boolean,
    val hasMoreToLoad: Boolean,
    val joinActions: ImmutableMap<RoomId, AsyncAction<Unit>>,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val topicViewerState: TopicViewerState,
    val canAccessSpaceSettings: Boolean,
    val isManageMode: Boolean,
    val selectedRoomIds: ImmutableSet<RoomId>,
    val canEditSpaceGraph: Boolean,
    val removeRoomsAction: AsyncAction<Unit>,
    val eventSink: (SpaceEvents) -> Unit
) {
    fun isJoining(spaceId: RoomId): Boolean = joinActions[spaceId] == AsyncAction.Loading
    fun isSelected(spaceId: RoomId): Boolean = selectedRoomIds.contains(spaceId)
    val hasAnyJoinFailures: Boolean = joinActions.values.any {
        it is AsyncAction.Failure
    }

    val showManageRoomsAction: Boolean = canEditSpaceGraph && children.any { spaceRoom -> !spaceRoom.isSpace }
    val selectedCount: Int = selectedRoomIds.size
    val isRemoveButtonEnabled: Boolean = selectedRoomIds.isNotEmpty()
}

@Immutable
sealed interface TopicViewerState {
    data object Hidden : TopicViewerState
    data class Shown(val topic: String) : TopicViewerState
}
