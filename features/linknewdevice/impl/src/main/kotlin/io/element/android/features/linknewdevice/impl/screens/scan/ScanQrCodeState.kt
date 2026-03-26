/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.scan

import io.element.android.libraries.architecture.AsyncAction

/**
 * 扫描二维码状态数据类
 *
 * @property scanAction 扫描操作的异步状态
 * @property eventSink 事件处理函数
 */
data class ScanQrCodeState(
    val scanAction: AsyncAction<Unit>,
    val eventSink: (ScanQrCodeEvent) -> Unit,
)
