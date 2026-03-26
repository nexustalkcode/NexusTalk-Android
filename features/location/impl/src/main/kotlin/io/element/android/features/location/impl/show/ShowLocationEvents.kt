/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.show

/**
 * 显示位置事件密封接口
 *
 * 定义了显示位置功能的所有用户交互事件。
 */
sealed interface ShowLocationEvents {
    /** 分享位置事件 */
    data object Share : ShowLocationEvents
    /**
     * 跟踪我的位置事件
     *
     * @property enabled 是否启用位置跟踪
     */
    data class TrackMyLocation(val enabled: Boolean) : ShowLocationEvents
    /** 关闭对话框事件 */
    data object DismissDialog : ShowLocationEvents
    /** 请求权限事件 */
    data object RequestPermissions : ShowLocationEvents
    /** 打开应用设置事件 */
    data object OpenAppSettings : ShowLocationEvents
}
