/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.password

/**
 * 重置身份密码事件密封接口
 *
 * 定义了重置身份密码页面的用户交互事件。
 */
sealed interface ResetIdentityPasswordEvent {
    /** 重置事件，包含密码 */
    data class Reset(val password: String) : ResetIdentityPasswordEvent

    /** 关闭错误提示事件 */
    data object DismissError : ResetIdentityPasswordEvent
}
