/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.editprofile

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.permissions.api.PermissionsState
import kotlinx.collections.immutable.ImmutableList

/**
 * 编辑用户资料状态数据类
 *
 * @property userId 用户 ID
 * @property displayName 显示名称
 * @property userAvatarUrl 用户头像 URL
 * @property avatarActions 可用的头像操作列表
 * @property saveButtonEnabled 保存按钮是否启用
 * @property saveAction 保存操作的状态
 * @property cameraPermissionState 相机权限状态
 * @property eventSink 事件处理函数
 */
data class EditUserProfileState(
    val userId: UserId,
    val displayName: String,
    val userAvatarUrl: String?,
    val avatarActions: ImmutableList<AvatarAction>,
    val saveButtonEnabled: Boolean,
    val saveAction: AsyncAction<Unit>,
    val cameraPermissionState: PermissionsState,
    val eventSink: (EditUserProfileEvent) -> Unit
)
