/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav.loggedin

/**
 * 已登录根页面可能触发的用户事件。
 */
sealed interface LoggedInEvents {
    /** 关闭当前错误对话框。 */
    data class CloseErrorDialog(val doNotShowAgain: Boolean) : LoggedInEvents
    /** 检查 Sliding Sync 代理可用性。 */
    data object CheckSlidingSyncProxyAvailability : LoggedInEvents
    /** 登出并迁移到原生 Sliding Sync。 */
    data object LogoutAndMigrateToNativeSlidingSync : LoggedInEvents
}
