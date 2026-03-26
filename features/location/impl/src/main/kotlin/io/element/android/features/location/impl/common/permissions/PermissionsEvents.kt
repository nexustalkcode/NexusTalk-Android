/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.permissions

/**
 * 权限事件密封接口
 *
 * 定义了权限相关的用户交互事件。
 */
sealed interface PermissionsEvents {
    /**
     * 请求权限事件
     *
     * 触发系统权限请求对话框。
     */
    data object RequestPermissions : PermissionsEvents
}
