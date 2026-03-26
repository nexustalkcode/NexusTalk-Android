/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.permissions.api.PermissionStateProvider
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import io.element.android.libraries.permissions.noop.NoopPermissionsPresenter
import io.element.android.services.toolbox.api.sdk.BuildVersionSdkIntProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 通知权限选择 Presenter
 *
 * 负责处理首次用户体验中通知权限请求的业务逻辑和状态管理。
 * 管理通知权限的请求和用户选择。
 *
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property callback 通知选择完成回调
 * @property appCoroutineScope 应用级别的协程作用域
 * @property permissionStateProvider 权限状态提供者
 * @property buildVersionSdkIntProvider 构建版本 SDK 提供者
 */
@AssistedInject
class NotificationsOptInPresenter(
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    @Assisted private val callback: NotificationsOptInNode.Callback,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope,
    private val permissionStateProvider: PermissionStateProvider,
    private val buildVersionSdkIntProvider: BuildVersionSdkIntProvider,
) : Presenter<NotificationsOptInState> {
    /**
     * 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param callback 通知选择完成回调
         * @return NotificationsOptInPresenter 实例
         */
        fun create(callback: NotificationsOptInNode.Callback): NotificationsOptInPresenter
    }

    private val postNotificationPermissionsPresenter: PermissionsPresenter =
        // 在 Android 13+ 上请求 POST_NOTIFICATION 权限
        if (buildVersionSdkIntProvider.isAtLeast(Build.VERSION_CODES.TIRAMISU)) {
            permissionsPresenterFactory.create(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NoopPermissionsPresenter()
        }

    /**
     * 生成界面状态
     *
     * @return NotificationsOptInState 通知权限选择状态
     */
    @Composable
    override fun present(): NotificationsOptInState {
        val notificationsPermissionsState = postNotificationPermissionsPresenter.present()

        /**
         * 处理用户事件
         *
         * @param event 通知权限选择事件
         */
        fun handleEvent(event: NotificationsOptInEvents) {
            when (event) {
                NotificationsOptInEvents.ContinueClicked -> {
                    if (notificationsPermissionsState.permissionGranted) {
                        callback.onNotificationsOptInFinished()
                    } else {
                        notificationsPermissionsState.eventSink(PermissionsEvent.RequestPermissions)
                    }
                }
                NotificationsOptInEvents.NotNowClicked -> {
                    if (buildVersionSdkIntProvider.isAtLeast(Build.VERSION_CODES.TIRAMISU)) {
                        appCoroutineScope.setPermissionDenied()
                    }
                    callback.onNotificationsOptInFinished()
                }
            }
        }

        LaunchedEffect(notificationsPermissionsState) {
            if (notificationsPermissionsState.permissionGranted ||
                notificationsPermissionsState.permissionAlreadyDenied) {
                callback.onNotificationsOptInFinished()
            }
        }

        return NotificationsOptInState(
            notificationsPermissionState = notificationsPermissionsState,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 设置权限被拒绝状态
     *
     * @receiver CoroutineScope
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun CoroutineScope.setPermissionDenied() = launch {
        permissionStateProvider.setPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, true)
    }
}
