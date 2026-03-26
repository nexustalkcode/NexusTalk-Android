/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.pollcontent

import io.element.android.libraries.matrix.api.poll.PollAnswer

/**
 * UI 模型 - 投票答案项
 *
 * 用于在 UI 中显示单个投票选项的完整信息。
 *
 * @property answer 投票答案对象
 * @property isSelected 用户是否选择了该答案
 * @property isEnabled 该答案是否可投票（投票结束后不可投票）
 * @property isWinner 是否是投票中的获胜答案
 * @property showVotes 是否显示投票数
 * @property votesCount 投票数量
 * @property percentage 投票百分比
 */
data class PollAnswerItem(
    val answer: PollAnswer,
    val isSelected: Boolean,
    val isEnabled: Boolean,
    val isWinner: Boolean,
    val showVotes: Boolean,
    val votesCount: Int,
    val percentage: Float,
)
