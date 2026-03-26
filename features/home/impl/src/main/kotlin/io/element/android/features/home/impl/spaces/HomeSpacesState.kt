/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.spaces

import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 首页空间状态数据类
 *
 * 表示首页空间导航的完整状态，包含当前空间、空间房间列表、邀请状态等信息。
 *
 * @property space 当前空间
 * @property spaceRooms 空间房间列表
 * @property seenSpaceInvites 已查看的空间邀请 ID 集合
 * @property hideInvitesAvatar 是否隐藏邀请头像
 * @property canCreateSpaces 是否可以创建空间
 * @property canExploreSpaces 是否可以探索空间
 * @property eventSink 事件处理函数
 */
data class HomeSpacesState(
    val space: CurrentSpace,
    val spaceRooms: ImmutableList<SpaceRoom>,
    val seenSpaceInvites: ImmutableSet<RoomId>,
    val hideInvitesAvatar: Boolean,
    val canCreateSpaces: Boolean,
    val canExploreSpaces: Boolean,
    val eventSink: (HomeSpacesEvents) -> Unit,
)

/**
 * 当前空间密封接口
 *
 * 表示当前选中的空间类型。
 */
sealed interface CurrentSpace {
    /** 根空间（所有房间） */
    object Root : CurrentSpace
    /**
     * 具体空间
     *
     * @property spaceRoom 空间房间信息
     */
    data class Space(val spaceRoom: SpaceRoom) : CurrentSpace
}
