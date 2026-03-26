/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.permissions.api

data class PermissionsState(
    // 权限字符串，例如 Manifest.permission.POST_NOTIFICATIONS
    val permission: String,
    // 当前权限是否已授予
    val permissionGranted: Boolean,
    // 是否应向用户展示权限申请说明（rationale）
    val shouldShowRationale: Boolean,
    // 是否展示应用内权限引导对话框
    val showDialog: Boolean,
    // 该权限是否已经向用户发起过请求
    val permissionAlreadyAsked: Boolean,
    // 为 true 时表示无需再次请求，系统权限弹窗将不再显示
    val permissionAlreadyDenied: Boolean,
    // 事件分发入口，用于处理权限相关交互事件
    val eventSink: (PermissionsEvent) -> Unit
)
