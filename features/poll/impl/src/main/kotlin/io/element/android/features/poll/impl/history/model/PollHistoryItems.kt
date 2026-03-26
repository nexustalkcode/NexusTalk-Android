/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 投票历史项集合数据类
 *
 * 包含所有投票历史项，按状态分类为进行中和已结束。
 *
 * @property ongoing 进行中的投票列表
 * @property past 已结束的投票列表
 */
data class PollHistoryItems(
    val ongoing: ImmutableList<PollHistoryItem> = persistentListOf(),
    val past: ImmutableList<PollHistoryItem> = persistentListOf(),
) {
    /** 总投票数量 */
    val size = ongoing.size + past.size
}
