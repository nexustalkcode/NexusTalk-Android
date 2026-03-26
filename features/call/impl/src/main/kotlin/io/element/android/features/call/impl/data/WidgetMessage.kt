/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Widget 消息数据类
 *
 * 用于在 Element X 应用和 Element Call WebView 之间传递的消息结构。
 * 该消息遵循 Matrix Widget API 规范，用于控制通话的各个方面。
 *
 * @property direction 消息方向，标识消息是从 Widget 发送到应用还是从应用发送到 Widget
 * @property widgetId Widget 的唯一标识符
 * @property requestId 请求的唯一标识符，用于匹配请求和响应
 * @property action 要执行的操作
 * @property data 消息的附加数据（可选）
 *
 * @see Direction 消息方向枚举
 * @see Action 操作类型枚举
 */
@Serializable
data class WidgetMessage(
    /** 消息方向，标识消息是从 Widget 发送到应用还是从应用发送到 Widget */
    @SerialName("api") val direction: Direction,
    /** Widget 的唯一标识符 */
    @SerialName("widgetId") val widgetId: String,
    /** 请求的唯一标识符，用于匹配请求和响应 */
    @SerialName("requestId") val requestId: String,
    /** 要执行的操作 */
    @SerialName("action") val action: Action,
    /** 消息的附加数据（可选） */
    @SerialName("data") val data: JsonElement? = null,
) {
    /**
     * 消息方向枚举
     *
     * 定义消息的传递方向。
     */
    @Serializable
    enum class Direction {
        /** 消息从 Widget（WebView）发送到 Element X 应用 */
        @SerialName("fromWidget")
        FromWidget,

        /** 消息从 Element X 应用发送到 Widget（WebView） */
        @SerialName("toWidget")
        ToWidget
    }

    /**
     * 操作类型枚举
     *
     * 定义 Widget 消息支持的各种操作。
     */
    @Serializable
    enum class Action {
        /** 加入通话 */
        @SerialName("io.element.join")
        Join,

        /** 挂断通话 */
        @SerialName("im.vector.hangup")
        HangUp,

        /** 关闭 Widget */
        @SerialName("io.element.close")
        Close,

        /** 发送事件 */
        @SerialName("send_event")
        SendEvent,

        /** 内容已加载 */
        @SerialName("content_loaded")
        ContentLoaded,
    }
}
