/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

/**
 * 扫描用户二维码事件
 *
 * 定义扫描用户二维码功能的事件类型。
 */
sealed interface ScanUserQrCodeEvents {
    /**
     * 请求相机权限事件
     */
    data object RequestCameraPermission : ScanUserQrCodeEvents

    /**
     * 二维码扫描成功事件
     *
     * @param data 扫描到的数据
     */
    data class QrCodeScanned(val data: String) : ScanUserQrCodeEvents

    /**
     * 重试扫描事件
     */
    data object TryAgain : ScanUserQrCodeEvents
}
