/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.send

import io.element.android.features.location.api.Location

/**
 * 发送位置事件密封接口
 *
 * 定义了发送位置功能的所有用户交互事件。
 */
sealed interface SendLocationEvents {
    /**
     * 发送位置事件
     *
     * 触发位置消息的发送。
     *
     * @property cameraPosition 相机位置（用于固定位置模式）
     * @property location 用户当前位置（用于分享位置模式）
     */
    data class SendLocation(
        val cameraPosition: CameraPosition,
        val location: Location?,
    ) : SendLocationEvents {
        /**
         * 相机位置数据类
         *
         * @property lat 纬度
         * @property lon 经度
         * @property zoom 缩放级别
         */
        data class CameraPosition(
            val lat: Double,
            val lon: Double,
            val zoom: Double,
        )
    }

    /** 切换到"我的位置"模式 */
    data object SwitchToMyLocationMode : SendLocationEvents
    /** 切换到"标记位置"模式 */
    data object SwitchToPinLocationMode : SendLocationEvents
    /** 关闭对话框 */
    data object DismissDialog : SendLocationEvents
    /** 请求位置权限 */
    data object RequestPermissions : SendLocationEvents
    /** 打开应用设置 */
    data object OpenAppSettings : SendLocationEvents
}
