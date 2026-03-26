/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 分享状态数据类
 *
 * 表示分享功能的当前状态，包含分享操作的状态信息。
 *
 * @property shareAction 分享操作的异步状态
 * @property eventSink 事件处理函数
 */
/**
 * Data class representing the state of the share feature.
 *
 * Contains the current state of the share operation, including async action status.
 *
 * @property shareAction The async action state of the share operation
 * @property eventSink The event handler function
 */
data class ShareState(
    val shareAction: AsyncAction<List<RoomId>>,
    val eventSink: (ShareEvents) -> Unit
)
