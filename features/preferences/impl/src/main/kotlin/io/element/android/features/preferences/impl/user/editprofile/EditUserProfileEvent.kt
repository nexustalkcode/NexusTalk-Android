/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.editprofile

import io.element.android.libraries.matrix.ui.media.AvatarAction

/**
 * 编辑用户资料事件密封接口
 *
 * 定义编辑用户资料页面中可能发生的各种用户交互事件。
 */
sealed interface EditUserProfileEvent {
    /** 处理头像操作（拍照、选择照片、删除） */
    data class HandleAvatarAction(val action: AvatarAction) : EditUserProfileEvent
    /** 更新显示名称 */
    data class UpdateDisplayName(val name: String) : EditUserProfileEvent
    /** 退出编辑 */
    data object Exit : EditUserProfileEvent
    /** 保存更改 */
    data object Save : EditUserProfileEvent
    /** 关闭对话框 */
    data object CloseDialog : EditUserProfileEvent
}
