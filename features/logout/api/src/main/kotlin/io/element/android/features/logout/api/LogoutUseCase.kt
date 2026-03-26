/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.api

/**
 * 用于从应用的任何位置触发当前用户退出登录的用例接口
 *
 * 此接口定义了退出登录的核心业务逻辑，允许应用的不同模块统一触发退出登录操作。
 */
interface LogoutUseCase {
    /**
     * 退出当前所有用户登录，并执行所需的清理任务
     * @param ignoreSdkError 如果为 true，则忽略 SDK 错误，用户仍将被强制退出登录
     */
    suspend fun logoutAll(ignoreSdkError: Boolean)
}
