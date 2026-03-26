/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.networkmonitor.api

import kotlinx.coroutines.flow.StateFlow

/**
 * 网络监控器接口
 *
 * 监控设备的网络连接状态，提供当前网络连接状态的 Flow。
 *
 * **注意：** 网络连接状态不等于互联网连接状态。
 * 设备可能已连接到网络，但无法访问 homeserver。
 *
 * @see DefaultNetworkMonitor 默认实现
 * @see NetworkStatus 网络状态枚举
 */
interface NetworkMonitor {
    /**
     * 当前网络连接状态的 Flow
     *
     * @property connectivity 网络连接状态流
     */
    val connectivity: StateFlow<NetworkStatus>
}
