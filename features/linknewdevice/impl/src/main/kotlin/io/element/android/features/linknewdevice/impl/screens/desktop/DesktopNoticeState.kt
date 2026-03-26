/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.desktop

import io.element.android.libraries.permissions.api.PermissionsState

/**
 * 桌面通知状态数据类
 *
 * @property cameraPermissionState 相机权限状态
 * @property canContinue 是否可以继续
 * @property eventSink 事件处理函数
 */
data class DesktopNoticeState(
    val cameraPermissionState: PermissionsState,
    val canContinue: Boolean,
    val eventSink: (DesktopNoticeEvent) -> Unit,
)
