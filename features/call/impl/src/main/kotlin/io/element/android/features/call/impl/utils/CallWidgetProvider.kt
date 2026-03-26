/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.widget.MatrixWidgetDriver

/**
 * 通话小组件提供者接口
 *
 * 提供获取 Matrix 房间通话小组件的功能。
 * 用于获取通话所需的 Widget Driver 和 URL。
 *
 * @see DefaultCallWidgetProvider 默认实现
 */
interface CallWidgetProvider {
    /**
     * 获取通话小组件
     *
     * 根据房间信息获取 Element Call 小组件，包括 Widget Driver 和呼叫 URL。
     *
     * @param sessionId 会话 ID
     * @param roomId 房间 ID
     * @param clientId 客户端 ID
     * @param languageTag 语言标签（可选）
     * @param theme 主题（可选）
     * @return Result<GetWidgetResult> 包含 Widget Driver 和 URL 的结果
     */
    suspend fun getWidget(
        sessionId: SessionId,
        roomId: RoomId,
        clientId: String,
        languageTag: String?,
        theme: String?,
    ): Result<GetWidgetResult>

    /**
     * 获取小组件结果数据类
     *
     * @property driver Matrix Widget Driver，用于与小组件通信
     * @property url Element Call URL
     */
    data class GetWidgetResult(
        /** Matrix Widget Driver，用于与小组件通信 */
        val driver: MatrixWidgetDriver,
        /** Element Call URL */
        val url: String,
    )
}
