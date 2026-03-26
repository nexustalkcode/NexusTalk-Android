/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.show

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.location.api.Location
import io.element.android.features.location.impl.common.MapDefaults
import io.element.android.features.location.impl.common.actions.LocationActions
import io.element.android.features.location.impl.common.permissions.PermissionsEvents
import io.element.android.features.location.impl.common.permissions.PermissionsPresenter
import io.element.android.features.location.impl.common.permissions.PermissionsState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta

/**
 * 显示位置 Presenter
 *
 * 负责处理显示位置页面的业务逻辑，包括：
 * - 管理位置权限状态
 * - 处理位置分享
 * - 处理位置跟踪
 * - 处理权限对话框显示
 *
 * @property location 要显示的位置
 * @property description 位置描述
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property locationActions 位置操作
 * @property buildMeta 构建元信息
 * @see ShowLocationState 显示位置状态
 */
/**
 * 显示位置 Presenter
 *
 * 负责处理显示位置页面的业务逻辑，包括：
 * - 管理位置权限状态
 * - 处理位置分享
 * - 处理位置跟踪
 * - 处理权限对话框显示
 *
 * @property location 要显示的位置
 * @property description 位置描述
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property locationActions 位置操作
 * @property buildMeta 构建元信息
 * @see ShowLocationState 显示位置状态
 */
@AssistedInject
class ShowLocationPresenter(
    /** 要显示的位置 */
    @Assisted private val location: Location,
    /** 位置描述 */
    @Assisted private val description: String?,
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    private val locationActions: LocationActions,
    private val buildMeta: BuildMeta,
) : Presenter<ShowLocationState> {
    /**
     * 工厂接口，用于创建 ShowLocationPresenter 实例
     */
    @AssistedFactory
    fun interface Factory {
        /**
         * 创建 ShowLocationPresenter 实例
         *
         * @param location 要显示的位置
         * @param description 位置描述
         * @return ShowLocationPresenter 显示位置Presenter实例
         */
        fun create(location: Location, description: String?): ShowLocationPresenter
    }

    /** 权限Presenter实例 */
    private val permissionsPresenter = permissionsPresenterFactory.create(MapDefaults.permissions)

    @Composable
    override fun present(): ShowLocationState {
        val permissionsState: PermissionsState = permissionsPresenter.present()
        var isTrackMyLocation by remember { mutableStateOf(false) }
        val appName by remember { derivedStateOf { buildMeta.applicationName } }
        var permissionDialog: ShowLocationState.Dialog by remember {
            mutableStateOf(ShowLocationState.Dialog.None)
        }

        LaunchedEffect(permissionsState.permissions) {
            if (permissionsState.isAnyGranted) {
                permissionDialog = ShowLocationState.Dialog.None
            }
        }

        fun handleEvent(event: ShowLocationEvents) {
            when (event) {
                ShowLocationEvents.Share -> locationActions.share(location, description)
                is ShowLocationEvents.TrackMyLocation -> {
                    if (event.enabled) {
                        when {
                            permissionsState.isAnyGranted -> isTrackMyLocation = true
                            permissionsState.shouldShowRationale -> permissionDialog = ShowLocationState.Dialog.PermissionRationale
                            else -> permissionDialog = ShowLocationState.Dialog.PermissionDenied
                        }
                    } else {
                        isTrackMyLocation = false
                    }
                }
                ShowLocationEvents.DismissDialog -> permissionDialog = ShowLocationState.Dialog.None
                ShowLocationEvents.OpenAppSettings -> {
                    locationActions.openSettings()
                    permissionDialog = ShowLocationState.Dialog.None
                }
                ShowLocationEvents.RequestPermissions -> permissionsState.eventSink(PermissionsEvents.RequestPermissions)
            }
        }

        return ShowLocationState(
            permissionDialog = permissionDialog,
            location = location,
            description = description,
            hasLocationPermission = permissionsState.isAnyGranted,
            isTrackMyLocation = isTrackMyLocation,
            appName = appName,
            eventSink = ::handleEvent,
        )
    }
}
