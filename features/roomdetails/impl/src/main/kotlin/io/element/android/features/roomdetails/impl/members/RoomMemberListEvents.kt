/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members

import io.element.android.libraries.matrix.api.room.RoomMember

/**
 * 房间成员列表事件密封接口
 *
 * 定义房间成员列表页面可能发生的事件。
 * 使用密封接口确保类型安全，只能创建预定义的子类型。
 *
 * @see RoomMember 房间成员数据类
 * @see SelectedSection 已选区域枚举
 */
sealed interface RoomMemberListEvents {
    /**
     * 切换选中的成员区域
     *
     * 用户切换成员列表的显示区域（成员/已封禁成员）。
     *
     * @property section 新的选中区域
     */
    data class ChangeSelectedSection(val section: SelectedSection) : RoomMemberListEvents

    /**
     * 选中房间成员
     *
     * 用户点击选择某个房间成员，通常用于显示成员操作菜单。
     *
     * @property roomMember 被选中的房间成员
     */
    data class RoomMemberSelected(val roomMember: RoomMember) : RoomMemberListEvents
}
