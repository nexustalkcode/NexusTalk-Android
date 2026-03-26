/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.show

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.location.api.Location

/** 预览用的应用名称常量 */
private const val APP_NAME = "ApplicationName"

/**
 * 显示位置状态预览参数提供者
 *
 * 用于在预览中生成各种 ShowLocationState 示例。
 */
class ShowLocationStateProvider : PreviewParameterProvider<ShowLocationState> {
    override val values: Sequence<ShowLocationState>
        get() = sequenceOf(
            aShowLocationState(),
            aShowLocationState(
                permissionDialog = ShowLocationState.Dialog.PermissionDenied,
            ),
            aShowLocationState(
                permissionDialog = ShowLocationState.Dialog.PermissionRationale,
            ),
            aShowLocationState(
                hasLocationPermission = true,
            ),
            aShowLocationState(
                hasLocationPermission = true,
                isTrackMyLocation = true,
            ),
            aShowLocationState(
                description = "My favourite place!",
            ),
            aShowLocationState(
                description = "For some reason I decided to to write a small essay that wraps at just two lines!",
            ),
            aShowLocationState(
                description = "For some reason I decided to write a small essay in the location description. " +
                    "It is so long that it will wrap onto more than two lines!",
            ),
        )
}

/**
 * 创建测试用的 ShowLocationState 实例
 *
 * 用于预览和测试目的。
 *
 * @param permissionDialog 权限对话框状态
 * @param location 位置信息
 * @param description 位置描述
 * @param hasLocationPermission 是否有位置权限
 * @param isTrackMyLocation 是否跟踪我的位置
 * @param appName 应用名称
 * @param eventSink 事件处理函数
 * @return ShowLocationState 状态实例
 */
fun aShowLocationState(
    permissionDialog: ShowLocationState.Dialog = ShowLocationState.Dialog.None,
    location: Location = Location(1.23, 2.34, 4f),
    description: String? = null,
    hasLocationPermission: Boolean = false,
    isTrackMyLocation: Boolean = false,
    appName: String = APP_NAME,
    eventSink: (ShowLocationEvents) -> Unit = {},
) = ShowLocationState(
    permissionDialog = permissionDialog,
    location = location,
    description = description,
    hasLocationPermission = hasLocationPermission,
    isTrackMyLocation = isTrackMyLocation,
    appName = appName,
    eventSink = eventSink,
)
