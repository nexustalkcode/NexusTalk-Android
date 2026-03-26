/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

/**
 * 退出登录事件密封接口
 *
 * 定义了退出登录流程中可能发生的各种用户交互事件。
 */
sealed interface LogoutEvents {
    /**
     * 触发退出登录事件
     * @param ignoreSdkError 是否忽略 SDK 错误，强制退出登录
     */
    data class Logout(val ignoreSdkError: Boolean) : LogoutEvents

    /**
     * 关闭对话框事件
     * 用户取消当前对话框时触发
     */
    data object CloseDialogs : LogoutEvents
}
