/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.di

import dev.zacsweers.metro.ContributesTo
import io.element.android.features.login.impl.qrcode.QrCodeLoginManager

/**
 * 二维码登录绑定接口
 *
 * 定义二维码登录功能的依赖绑定。
 * 用于在 QrCodeLoginScope 作用域内提供 QrCodeLoginManager 实例。
 *
 * @see QrCodeLoginScope 二维码登录作用域
 * @see QrCodeLoginManager 二维码登录管理器
 */
@ContributesTo(QrCodeLoginScope::class)
interface QrCodeLoginBindings {
    /**
     * 获取二维码登录管理器
     *
     * @return QrCodeLoginManager 实例
     */
    fun qrCodeLoginManager(): QrCodeLoginManager
}
