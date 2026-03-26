/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import io.element.android.libraries.matrix.ui.media.AvatarAction

/**
 * 房间详情编辑页面事件密封接口
 *
 * 定义了房间详情编辑功能中所有可能发生的用户交互事件
 */
sealed interface RoomDetailsEditEvent {
    /**
     * 处理头像操作事件
     *
     * @property action 要执行的头像操作（拍照、选择照片或删除）
     */
    data class HandleAvatarAction(val action: AvatarAction) : RoomDetailsEditEvent

    /**
     * 更新房间名称事件
     *
     * @property name 新的房间名称
     */
    data class UpdateRoomName(val name: String) : RoomDetailsEditEvent

    /**
     * 更新房间主题事件
     *
     * @property topic 新的房间主题
     */
    data class UpdateRoomTopic(val topic: String) : RoomDetailsEditEvent

    /**
     * 返回按钮 pressed 事件
     * 当用户点击返回按钮时触发，可能显示保存更改确认对话框
     */
    data object OnBackPress : RoomDetailsEditEvent

    /**
     * 保存更改事件
     * 触发将编辑后的房间名称、主题和头像保存到服务器
     */
    data object Save : RoomDetailsEditEvent

    /**
     * 关闭对话框事件
     * 关闭保存确认对话框或错误提示对话框
     */
    data object CloseDialog : RoomDetailsEditEvent
}
