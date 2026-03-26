/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 创建房间配置数据类
 *
 * 表示创建房间时用户配置的所有信息，包括房间名称、主题、头像、可见性设置等。
 * 该配置会在整个创建房间流程中传递和使用。
 *
 * @property isSpace 是否创建为空间（Space），默认为 false 表示创建普通房间
 * @property roomName 房间名称，用户输入的房间名称
 * @property topic 房间主题/描述，房间的可选描述信息
 * @property avatarUri 头像 URI，房间头像的图片 URI
 * @property invites 被邀请用户列表，准备邀请加入房间的用户列表
 * @property visibilityState 可见性状态，房间的可见性设置（私有/公开）
 * @property parentSpace 父空间，将房间添加到的父空间（可选）
 */
data class CreateRoomConfig(
    val isSpace: Boolean = false,
    val roomName: String? = null,
    val topic: String? = null,
    val avatarUri: String? = null,
    val invites: ImmutableList<MatrixUser> = persistentListOf(),
    val visibilityState: RoomVisibilityState = RoomVisibilityState.Private(),
    val parentSpace: SpaceRoom? = null,
)
