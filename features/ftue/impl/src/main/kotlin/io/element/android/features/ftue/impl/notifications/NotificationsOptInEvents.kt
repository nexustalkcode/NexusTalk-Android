/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

/**
 * 通知权限选择事件密封接口
 *
 * 定义用户在通知权限选择界面可能触发的事件。
 */
sealed interface NotificationsOptInEvents {
    /**
     * 点击启用通知按钮事件
     *
     * 当用户点击"启用通知"按钮时触发。
     * 如果权限未授予，系统将请求权限。
     */
    data object ContinueClicked : NotificationsOptInEvents

    /**
     * 点击暂时不按钮事件
     *
     * 当用户点击"暂时不"按钮时触发。
     * 用户选择暂不开启通知，FTUE 流程继续进行。
     */
    data object NotNowClicked : NotificationsOptInEvents
}
