/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 转发消息状态数据类
 *
 * 表示转发消息界面的状态，包含转发操作的异步状态。
 *
 * @property forwardAction 转发操作的异步状态
 * @property eventSink 事件处理函数
 */
data class ForwardMessagesState(
    val forwardAction: AsyncAction<List<RoomId>>,
    val eventSink: (ForwardMessagesEvents) -> Unit
)
