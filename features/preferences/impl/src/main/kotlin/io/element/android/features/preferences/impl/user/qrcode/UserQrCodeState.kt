/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.qrcode

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 用户二维码页面状态数据类
 *
 * @property matrixUser Matrix 用户信息
 * @property eventSink 事件处理函数
 */
data class UserQrCodeState(
    val matrixUser: MatrixUser,
    val eventSink: (UserQrCodeEvent) -> Unit
)

/**
 * 用户二维码页面事件密封接口
 */
sealed interface UserQrCodeEvent {
    /** 返回点击 */
    data object BackClick : UserQrCodeEvent
    /** 分享二维码 */
    data object ShareQrCode : UserQrCodeEvent
}
