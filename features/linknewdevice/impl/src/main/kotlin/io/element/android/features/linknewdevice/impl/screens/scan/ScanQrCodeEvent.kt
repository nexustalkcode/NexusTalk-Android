/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.scan

/**
 * 扫描二维码页面可能触发的用户事件。
 */
sealed interface ScanQrCodeEvent {
    /** 成功扫描到二维码内容。 */
    data class QrCodeScanned(val data: ByteArray) : ScanQrCodeEvent

    /** 用户点击重试。 */
    data object TryAgain : ScanQrCodeEvent
}
