/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.permissions

/**
 * 权限状态数据类
 *
 * 表示权限请求界面的当前状态。
 *
 * @property permissions 权限授予状态
 * @property shouldShowRationale 是否显示权限说明理由
 * @property eventSink 事件处理函数
 */
data class PermissionsState(
    val permissions: Permissions,
    val shouldShowRationale: Boolean,
    val eventSink: (PermissionsEvents) -> Unit,
) {
    /**
     * 权限授予状态密封接口
     */
    sealed interface Permissions {
        /** 所有权限都已授予 */
        data object AllGranted : Permissions
        /** 部分权限已授予 */
        data object SomeGranted : Permissions
        /** 所有权限都未授予 */
        data object NoneGranted : Permissions
    }

    /** 是否有任何权限已授予 */
    val isAnyGranted: Boolean
        get() = permissions is Permissions.SomeGranted || permissions is Permissions.AllGranted
}
