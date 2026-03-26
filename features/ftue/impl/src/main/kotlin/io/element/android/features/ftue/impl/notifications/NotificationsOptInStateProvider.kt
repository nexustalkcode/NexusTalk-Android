/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.permissions.api.aPermissionsState

/**
 * 通知权限选择状态预览参数提供者
 *
 * 该类继承自 PreviewParameterProvider，用于在 Jetpack Compose 预览中提供
 * 不同状态的 NotificationsOptInState 示例数据。
 *
 * @see NotificationsOptInState
 */
open class NotificationsOptInStateProvider : PreviewParameterProvider<NotificationsOptInState> {
    override val values: Sequence<NotificationsOptInState>
        get() = sequenceOf(
            aNotificationsOptInState(),
            // Add other states here
        )
}

/**
 * 创建测试用的通知权限选择状态
 *
 * @param showDialog 是否显示权限对话框，默认为 false
 * @return 包含默认值的 NotificationsOptInState 实例
 */
fun aNotificationsOptInState() = NotificationsOptInState(
    notificationsPermissionState = aPermissionsState(showDialog = false),
    eventSink = {}
)
