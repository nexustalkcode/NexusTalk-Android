/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.send

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/** 预览用的应用名称常量 */
private const val APP_NAME = "ApplicationName"

/**
 * 发送位置状态预览参数提供者
 *
 * 用于在预览中生成各种 SendLocationState 示例。
 */
class SendLocationStateProvider : PreviewParameterProvider<SendLocationState> {
    override val values: Sequence<SendLocationState>
        get() = sequenceOf(
            aSendLocationState(
                permissionDialog = SendLocationState.Dialog.None,
                mode = SendLocationState.Mode.PinLocation,
                hasLocationPermission = false,
            ),
            aSendLocationState(
                permissionDialog = SendLocationState.Dialog.PermissionDenied,
                mode = SendLocationState.Mode.PinLocation,
                hasLocationPermission = false,
            ),
            aSendLocationState(
                permissionDialog = SendLocationState.Dialog.PermissionRationale,
                mode = SendLocationState.Mode.PinLocation,
                hasLocationPermission = false,
            ),
            aSendLocationState(
                permissionDialog = SendLocationState.Dialog.None,
                mode = SendLocationState.Mode.PinLocation,
                hasLocationPermission = true,
            ),
            aSendLocationState(
                permissionDialog = SendLocationState.Dialog.None,
                mode = SendLocationState.Mode.SenderLocation,
                hasLocationPermission = true,
            ),
        )
}

/**
 * 创建测试用的 SendLocationState 实例
 *
 * 用于预览和测试目的。
 *
 * @param permissionDialog 权限对话框状态
 * @param mode 发送模式
 * @param hasLocationPermission 是否有位置权限
 * @return SendLocationState 状态实例
 */
private fun aSendLocationState(
    permissionDialog: SendLocationState.Dialog,
    mode: SendLocationState.Mode,
    hasLocationPermission: Boolean,
): SendLocationState {
    return SendLocationState(
        permissionDialog = permissionDialog,
        mode = mode,
        hasLocationPermission = hasLocationPermission,
        appName = APP_NAME,
        eventSink = {}
    )
}
