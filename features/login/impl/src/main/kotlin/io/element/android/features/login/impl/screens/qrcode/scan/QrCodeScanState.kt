/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.qrcode.scan

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.auth.qrlogin.MatrixQrCodeLoginData

/**
 * 二维码扫码页展示状态。
 *
 * @property isScanning 当前是否允许继续扫描。
 * @property authenticationAction 当前二维码解析/校验异步状态。
 * @property eventSink 页面事件分发函数。
 */
data class QrCodeScanState(
    val isScanning: Boolean,
    val authenticationAction: AsyncAction<MatrixQrCodeLoginData>,
    val eventSink: (QrCodeScanEvents) -> Unit
)
