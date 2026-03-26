/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications.edit

import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList

/**
 * 编辑通知设置房间信息数据类
 *
 * @property roomId 房间 ID
 * @property name 房间名称
 * @property heroesAvatar 房间头像列表（用于群聊头像）
 * @property avatarData 头像数据
 * @property notificationMode 通知模式
 */
data class EditNotificationSettingRoomInfo(
    val roomId: RoomId,
    val name: String?,
    val heroesAvatar: ImmutableList<AvatarData>,
    val avatarData: AvatarData,
    val notificationMode: RoomNotificationMode?
)
