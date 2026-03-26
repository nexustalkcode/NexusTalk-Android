/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.desktop

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter

/**
 * 桌面设备通知 Presenter
 *
 * 负责处理链接桌面设备时显示通知权限说明的业务逻辑和状态管理。
 * 管理相机权限的请求和用户确认流程。
 *
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 */
@Inject
class DesktopNoticePresenter(
    permissionsPresenterFactory: PermissionsPresenter.Factory,
) : Presenter<DesktopNoticeState> {
    // 相机权限 Presenter，用于查询/请求权限
    private val cameraPermissionPresenter: PermissionsPresenter = permissionsPresenterFactory.create(Manifest.permission.CAMERA)
    // 是否有待处理的权限请求（避免重复触发）
    private var pendingPermissionRequest by mutableStateOf(false)

    /**
     * 生成界面状态
     *
     * @return DesktopNoticeState 桌面设备通知状态
     */
    @Composable
    override fun present(): DesktopNoticeState {
        // 权限组件的当前状态（是否已授权、是否展示系统对话框等）
        val cameraPermissionState = cameraPermissionPresenter.present()
        // UI 是否可以继续进入扫码页
        var canContinue by remember { mutableStateOf(false) }
        LaunchedEffect(cameraPermissionState.permissionGranted) {
            // 如果用户刚授予权限且之前触发过请求，则放行继续
            if (cameraPermissionState.permissionGranted && pendingPermissionRequest) {
                pendingPermissionRequest = false
                canContinue = true
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 桌面设备通知事件
         */
        fun handleEvent(event: DesktopNoticeEvent) {
            when (event) {
                DesktopNoticeEvent.Continue -> if (cameraPermissionState.permissionGranted) {
                    // 已有权限，直接放行
                    canContinue = true
                } else {
                    // 标记为等待权限，触发系统权限弹窗
                    pendingPermissionRequest = true
                    cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                }
            }
        }

        return DesktopNoticeState(
            cameraPermissionState = cameraPermissionState,
            canContinue = canContinue,
            eventSink = ::handleEvent,
        )
    }
}
