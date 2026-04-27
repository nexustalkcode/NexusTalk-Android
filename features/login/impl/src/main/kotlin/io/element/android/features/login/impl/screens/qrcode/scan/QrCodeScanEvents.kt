/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.qrcode.scan

/**
 * 二维码扫码页可能触发的用户事件。
 */
sealed interface QrCodeScanEvents {
    /** 成功扫描到二维码字节内容。 */
    data class QrCodeScanned(val code: ByteArray) : QrCodeScanEvents
    /** 用户点击重试。 */
    data object TryAgain : QrCodeScanEvents
}
