/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.permissions.api.PermissionsState
import io.element.android.libraries.permissions.api.aPermissionsState

/**
 * 扫描用户二维码状态
 *
 * 表示扫描用户二维码功能的状态，包括扫描结果和处理状态。
 */
data class ScanUserQrCodeState(
    val cameraPermissionState: PermissionsState = aPermissionsState(showDialog = false),
    val scanAction: AsyncAction<String> = AsyncAction.Uninitialized,
    val eventSink: (ScanUserQrCodeEvents) -> Unit = {},
)
