/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.networkmonitor.api

/**
 * 网络连接状态枚举
 *
 * 表示设备的网络连接状态。
 *
 * **注意：** 这是网络连接状态，不是互联网连接状态。
 *
 * @see NetworkStatus.Connected 已连接
 * @see NetworkStatus.Disconnected 断开连接
 */
enum class NetworkStatus {
    /**
     * 设备已连接到网络
     */
    Connected,

    /**
     * 设备未连接到任何网络
     */
    Disconnected
}
