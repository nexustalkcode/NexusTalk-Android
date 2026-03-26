/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.permissions

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding

/**
 * 默认权限Presenter实现
 *
 * 使用 Accompanist Permissions 库实现权限状态管理。
 */
@Suppress("unused")
@AssistedInject
class DefaultPermissionsPresenter(
    @Assisted private val permissions: List<String>
) : PermissionsPresenter {
    /**
     * 工厂接口，用于创建 DefaultPermissionsPresenter 实例
     */
    @AssistedFactory
    @ContributesBinding(AppScope::class)
    interface Factory : PermissionsPresenter.Factory {
        override fun create(permissions: List<String>): DefaultPermissionsPresenter
    }

    /**
     * 生成权限状态
     *
     * 使用 Accompanist 权限库管理权限状态，并处理权限请求事件。
     *
     * @return PermissionsState 当前权限状态
     */
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun present(): PermissionsState {
        val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

        fun handleEvent(event: PermissionsEvents) {
            when (event) {
                PermissionsEvents.RequestPermissions -> multiplePermissionsState.launchMultiplePermissionRequest()
            }
        }

        return PermissionsState(
            permissions = when {
                multiplePermissionsState.allPermissionsGranted -> PermissionsState.Permissions.AllGranted
                multiplePermissionsState.permissions.any { it.status.isGranted } -> PermissionsState.Permissions.SomeGranted
                else -> PermissionsState.Permissions.NoneGranted
            },
            shouldShowRationale = multiplePermissionsState.shouldShowRationale,
            eventSink = ::handleEvent,
        )
    }
}
