/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 聚合反应发送者数据类
 *
 * 表示一个发送反应的用户信息。
 *
 * @property senderId 发送者用户ID
 * @property timestamp 时间戳（毫秒）
 * @property sentTime 发送时间字符串
 * @property user 发送者用户信息（可选，用于缓存）
 */
data class AggregatedReactionSender(
    val senderId: UserId,
    val timestamp: Long,
    val sentTime: String,
    val user: MatrixUser? = null
)
