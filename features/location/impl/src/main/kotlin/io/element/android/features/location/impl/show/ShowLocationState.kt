/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.show

import io.element.android.features.location.api.Location

/**
 * 显示位置状态数据类
 *
 * 表示显示位置界面的完整状态，包含位置信息、权限状态、地图交互状态等。
 *
 * @property permissionDialog 权限对话框状态
 * @property location 位置信息
 * @property description 位置描述
 * @property hasLocationPermission 是否已获取位置权限
 * @property isTrackMyLocation 是否正在跟踪我的位置
 * @property appName 应用名称
 * @property eventSink 事件处理函数
 */
data class ShowLocationState(
    /** 权限对话框状态 */
    val permissionDialog: Dialog,
    /** 位置信息 */
    val location: Location,
    /** 位置描述 */
    val description: String?,
    /** 是否已获取位置权限 */
    val hasLocationPermission: Boolean,
    /** 是否正在跟踪我的位置 */
    val isTrackMyLocation: Boolean,
    /** 应用名称 */
    val appName: String,
    /** 事件处理函数 */
    val eventSink: (ShowLocationEvents) -> Unit,
) {
    /**
     * 对话框状态密封接口
     */
    sealed interface Dialog {
        /** 无对话框 */
        data object None : Dialog
        /** 权限说明对话框 */
        data object PermissionRationale : Dialog
        /** 权限被拒绝对话框 */
        data object PermissionDenied : Dialog
    }
}
