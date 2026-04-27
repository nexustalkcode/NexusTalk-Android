/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.qrcode.intro

import io.element.android.libraries.permissions.api.PermissionsState

/**
 * 二维码登录引导页展示状态。
 *
 * @property appName 当前应用名。
 * @property desktopAppName 桌面端应用名。
 * @property cameraPermissionState 相机权限状态。
 * @property canContinue 当前是否可以继续到扫码页。
 * @property eventSink 页面事件分发函数。
 */
data class QrCodeIntroState(
    val appName: String,
    val desktopAppName: String,
    val cameraPermissionState: PermissionsState,
    val canContinue: Boolean,
    val eventSink: (QrCodeIntroEvents) -> Unit
)
