/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 推送配置 (Push Configuration)
 *
 * 此对象包含消息推送功能相关的配置项。
 * 用于配置与Matrix推送网关的连接参数。
 */
object PushConfig {
    /**
     * 推送应用的唯一标识符。用于向Matrix推送网关注册设备时标识应用。
     * 注意：pusher_app_id不能超过64个字符。
     * 此ID对应Matrix服务器上配置的推送应用，用于接收新消息通知。
     */
    const val PUSHER_APP_ID: String = "chat.haddpp.android.z"
}
