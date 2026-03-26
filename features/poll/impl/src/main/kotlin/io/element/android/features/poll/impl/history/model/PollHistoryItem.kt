/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history.model

import io.element.android.features.poll.api.pollcontent.PollContentState

/**
 * 投票历史项数据类
 *
 * 表示单个投票历史记录的 UI 状态。
 *
 * @property formattedDate 格式化后的日期字符串
 * @property state 投票内容状态
 */
data class PollHistoryItem(
    val formattedDate: String,
    val state: PollContentState,
)
