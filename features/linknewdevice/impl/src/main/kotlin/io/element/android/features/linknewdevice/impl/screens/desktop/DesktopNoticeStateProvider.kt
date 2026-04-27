/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.desktop

import android.Manifest
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.permissions.api.PermissionsState
import io.element.android.libraries.permissions.api.aPermissionsState

/**
 * 为桌面端说明页预览提供样例状态。
 */
open class DesktopNoticeStateProvider : PreviewParameterProvider<DesktopNoticeState> {
    override val values: Sequence<DesktopNoticeState>
        get() = sequenceOf(
            // 默认状态
            aDesktopNoticeState(),
            // 模拟弹出相机权限对话框的状态
            aDesktopNoticeState(cameraPermissionState = aPermissionsState(showDialog = true, permission = Manifest.permission.CAMERA)),
        )
}

/**
 * 构造一份桌面端说明页样例状态。
 */
fun aDesktopNoticeState(
    cameraPermissionState: PermissionsState = aPermissionsState(
        showDialog = false,
        permission = Manifest.permission.CAMERA,
    ),
    canContinue: Boolean = false,
    eventSink: (DesktopNoticeEvent) -> Unit = {},
) = DesktopNoticeState(
    cameraPermissionState = cameraPermissionState,
    canContinue = canContinue,
    eventSink = eventSink
)
