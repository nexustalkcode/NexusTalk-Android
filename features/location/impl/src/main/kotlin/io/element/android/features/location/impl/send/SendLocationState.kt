/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.send

/**
 * 发送位置状态数据类
 *
 * 表示发送位置界面的当前状态，包含权限对话框和发送模式。
 *
 * @property permissionDialog 权限对话框状态
 * @property mode 发送位置的模式
 * @property hasLocationPermission 是否已获得位置权限
 * @property appName 应用名称
 * @property eventSink 事件处理函数
 */
data class SendLocationState(
    /** 权限对话框状态 */
    val permissionDialog: Dialog,
    /** 当前发送模式 */
    val mode: Mode,
    /** 是否已获得位置权限 */
    val hasLocationPermission: Boolean,
    /** 应用名称 */
    val appName: String,
    /** 事件处理函数 */
    val eventSink: (SendLocationEvents) -> Unit,
) {
    /**
     * 发送模式密封接口
     *
     * 定义发送位置的两种模式。
     */
    sealed interface Mode {
        /** 发送当前位置 */
        data object SenderLocation : Mode
        /** 发送标记位置 */
        data object PinLocation : Mode
    }

    /**
     * 对话框状态密封接口
     *
     * 定义权限相关的对话框状态。
     */
    sealed interface Dialog {
        /** 无对话框 */
        data object None : Dialog
        /** 权限理由说明对话框 */
        data object PermissionRationale : Dialog
        /** 权限被拒绝对话框 */
        data object PermissionDenied : Dialog
    }
}
