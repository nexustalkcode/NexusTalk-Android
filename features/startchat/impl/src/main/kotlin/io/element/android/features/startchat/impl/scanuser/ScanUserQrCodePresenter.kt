/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter

/**
 * 扫描用户二维码 Presenter
 *
 * 负责处理扫描用户二维码的业务逻辑。
 * 解析二维码数据，提取符合格式 @用户名:nexustalk.space 的用户ID。
 */
@Inject
class ScanUserQrCodePresenter(
    permissionsPresenterFactory: PermissionsPresenter.Factory,
) : Presenter<ScanUserQrCodeState> {
    private val cameraPermissionPresenter: PermissionsPresenter = permissionsPresenterFactory.create(Manifest.permission.CAMERA)

    /**
     * 正则表达式匹配 nexustalk.space 域名的用户ID
     */
    private val userIdPattern = Regex("@(.+):nexustalk\\.space")

    /**
     * 生成界面状态
     *
     * @return ScanUserQrCodeState 扫描用户二维码状态
     */
    @Composable
    override fun present(): ScanUserQrCodeState {
        val cameraPermissionState = cameraPermissionPresenter.present()
        var scanAction by remember { mutableStateOf<AsyncAction<String>>(AsyncAction.Loading) }

        LaunchedEffect(Unit) {
            if (!cameraPermissionState.permissionGranted) {
                cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 扫描用户二维码事件
         */
        fun handleEvent(event: ScanUserQrCodeEvents) {
            when (event) {
                ScanUserQrCodeEvents.RequestCameraPermission -> {
                    cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                }
                is ScanUserQrCodeEvents.QrCodeScanned -> {
                    val result = parseUserId(event.data)
                    if (result != null) {
                        scanAction = AsyncAction.Success(result)
                    } else {
                        scanAction = AsyncAction.Failure(
                            Exception("Invalid QR code format. Expected format: @username:nexustalk.space")
                        )
                    }
                }
                ScanUserQrCodeEvents.TryAgain -> {
                    scanAction = AsyncAction.Loading
                }
            }
        }

        return ScanUserQrCodeState(
            cameraPermissionState = cameraPermissionState,
            scanAction = scanAction,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 解析用户ID
     *
     * 从扫描数据中提取符合格式的用户ID。
     *
     * @param data 扫描到的数据
     * @return 提取的用户ID，如果格式不匹配则返回null
     */
    private fun parseUserId(data: String): String? {
        val match = userIdPattern.find(data)
        return match?.value
    }
}
