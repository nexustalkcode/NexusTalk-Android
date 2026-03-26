/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import dev.zacsweers.metro.Inject
import io.element.android.features.call.impl.data.WidgetMessage
import io.element.android.libraries.androidutils.json.JsonProvider
import io.element.android.libraries.core.extensions.runCatchingExceptions

/**
 * 小组件消息序列化器
 *
 * 负责将 WidgetMessage 对象序列化为 JSON 字符串，以及将 JSON 字符串反序列化为 WidgetMessage 对象。
 *
 * @param json JSON 提供者
 *
 * @see WidgetMessage 小组件消息数据类
 */
@Inject
class WidgetMessageSerializer(
    private val json: JsonProvider,
) {
    /**
     * 反序列化消息
     *
     * 将 JSON 字符串转换为 WidgetMessage 对象。
     *
     * @param message JSON 字符串
     * @return Result<WidgetMessage> 转换结果
     */
    fun deserialize(message: String): Result<WidgetMessage> {
        return runCatchingExceptions { json().decodeFromString(WidgetMessage.serializer(), message) }
    }

    /**
     * 序列化消息
     *
     * 将 WidgetMessage 对象转换为 JSON 字符串。
     *
     * @param message WidgetMessage 对象
     * @return JSON 字符串
     */
    fun serialize(message: WidgetMessage): String {
        return json().encodeToString(WidgetMessage.serializer(), message)
    }
}
