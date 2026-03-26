/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.permissions.api.PermissionsState
import kotlinx.collections.immutable.ImmutableList

/**
 * 房间详情编辑页面状态数据类
 *
 * 封装了房间详情编辑页面的所有状态信息，包括房间信息、编辑权限和UI状态
 *
 * @property roomId 房间的唯一标识符
 * @property roomRawName 房间原始名称（来自m.room.name状态事件，非显示名称）
 * @property canChangeName 当前用户是否有权限修改房间名称
 * @property roomTopic 房间主题/描述
 * @property canChangeTopic 当前用户是否有权限修改房间主题
 * @property roomAvatarUrl 房间头像URL，如果有的话
 * @property canChangeAvatar 当前用户是否有权限修改房间头像
 * @property avatarActions 可用的头像操作列表（拍照、从相册选择、删除等）
 * @property saveButtonEnabled 保存按钮是否可用
 * @property saveAction 保存操作的状态（未初始化、加载中、成功、失败、确认取消等）
 * @property cameraPermissionState 相机权限状态
 * @property isSpace 是否为空间（Space）
 * @property eventSink 事件处理函数，用于将用户交互事件发送到Presenter
 */
data class RoomDetailsEditState(
    /** 房间的唯一标识符 */
    val roomId: RoomId,
    /** 房间原始名称（来自m.room.name状态事件，非显示名称） */
    val roomRawName: String,
    /** 当前用户是否有权限修改房间名称 */
    val canChangeName: Boolean,
    /** 房间主题/描述 */
    val roomTopic: String,
    /** 当前用户是否有权限修改房间主题 */
    val canChangeTopic: Boolean,
    /** 房间头像URL */
    val roomAvatarUrl: String?,
    /** 当前用户是否有权限修改房间头像 */
    val canChangeAvatar: Boolean,
    /** 可用的头像操作列表 */
    val avatarActions: ImmutableList<AvatarAction>,
    /** 保存按钮是否可用 */
    val saveButtonEnabled: Boolean,
    /** 保存操作的状态 */
    val saveAction: AsyncAction<Unit>,
    /** 相机权限状态 */
    val cameraPermissionState: PermissionsState,
    /** 是否为空间（Space） */
    val isSpace: Boolean,
    /** 事件处理函数，用于将用户交互事件发送到Presenter */
    val eventSink: (RoomDetailsEditEvent) -> Unit
)
