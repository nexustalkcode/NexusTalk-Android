/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.ui.media.AvatarAction

/**
 * 配置房间事件密封接口
 *
 * 定义了配置房间界面中所有用户交互产生的事件类型。
 * 这些事件由 View 层捕获并传递给 Presenter 进行处理。
 *
 * @see ConfigureRoomPresenter 处理这些事件的 presenter
 * @see ConfigureRoomState 事件处理后的状态更新
 */
sealed interface ConfigureRoomEvents {
    /**
     * 房间名称变更事件
     *
     * @property name 新的房间名称
     */
    data class RoomNameChanged(val name: String) : ConfigureRoomEvents

    /**
     * 房间主题变更事件
     *
     * @property topic 新的房间主题/描述
     */
    data class TopicChanged(val topic: String) : ConfigureRoomEvents

    /**
     * 加入规则变更事件
     *
     * @property joinRuleItem 新的加入规则选项
     */
    data class JoinRuleChanged(val joinRuleItem: JoinRuleItem) : ConfigureRoomEvents

    /**
     * 房间地址变更事件
     *
     * @property roomAddress 新的房间地址（仅公开房间需要）
     */
    data class RoomAddressChanged(val roomAddress: String) : ConfigureRoomEvents

    /**
     * 创建房间事件
     *
     * 触发创建房间的操作
     */
    data object CreateRoom : ConfigureRoomEvents

    /**
     * 处理头像操作事件
     *
     * @property action 头像操作，包括拍照、选择图片或移除头像
     */
    data class HandleAvatarAction(val action: AvatarAction) : ConfigureRoomEvents

    /**
     * 设置父空间事件
     *
     * @property space 父空间房间，如果为 null 表示不添加到任何空间
     */
    data class SetParentSpace(val space: SpaceRoom?) : ConfigureRoomEvents

    /**
     * 取消创建房间事件
     *
     * 取消正在进行的创建房间操作，重置创建状态
     */
    data object CancelCreateRoom : ConfigureRoomEvents
}
