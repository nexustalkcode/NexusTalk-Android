/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.root

import io.element.android.libraries.architecture.AsyncData

/**
 * 链接新设备根页面状态数据类
 *
 * @property isSupported 是否支持链接新设备
 * @property qrCodeData 二维码数据的异步状态
 * @property eventSink 事件处理函数
 */
data class LinkNewDeviceRootState(
    val isSupported: AsyncData<Boolean>,
    val qrCodeData: AsyncData<Unit>,
    val eventSink: (LinkNewDeviceRootEvent) -> Unit,
)
