/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

import io.element.android.libraries.permissions.api.PermissionsState

/**
 * 通知权限选择状态数据类
 *
 * 表示首次启动时通知权限请求界面的当前状态。
 *
 * @property notificationsPermissionState 当前通知权限的状态信息，包括是否已授权等
 * @property eventSink 事件处理函数，用于将用户操作事件传递给 Presenter
 */
data class NotificationsOptInState(
    /** 通知权限的当前状态 */
    val notificationsPermissionState: PermissionsState,
    /** 事件处理函数，用于传递用户操作事件 */
    val eventSink: (NotificationsOptInEvents) -> Unit
)
