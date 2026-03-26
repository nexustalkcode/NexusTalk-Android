/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.api

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 用户资料状态数据类
 *
 * @property userId 用户 ID
 * @property userName 用户名称
 * @property avatarUrl 头像 URL
 * @property verificationState 用户验证状态
 * @property isBlocked 是否被屏蔽的异步数据
 * @property startDmActionState 开启私信操作的异步状态
 * @property displayConfirmationDialog 显示的确认对话框类型
 * @property isCurrentUser 是否为当前用户
 * @property dmRoomId 私信房间 ID
 * @property canCall 是否可以通话
 * @property snackbarMessage Snackbar 消息
 * @property eventSink 事件处理函数
 */
data class UserProfileState(
    val userId: UserId,
    val userName: String?,
    val avatarUrl: String?,
    val verificationState: UserProfileVerificationState,
    val isBlocked: AsyncData<Boolean>,
    val startDmActionState: AsyncAction<RoomId>,
    val displayConfirmationDialog: ConfirmationDialog?,
    val isCurrentUser: Boolean,
    val dmRoomId: RoomId?,
    val canCall: Boolean,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (UserProfileEvents) -> Unit
) {
    enum class ConfirmationDialog {
        Block,
        Unblock
    }
}

enum class UserProfileVerificationState {
    UNKNOWN,
    VERIFIED,
    UNVERIFIED,
    VERIFICATION_VIOLATION,
}
