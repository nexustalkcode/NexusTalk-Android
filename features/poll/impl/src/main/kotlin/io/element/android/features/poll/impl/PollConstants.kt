/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl

/**
 * 投票常量对象
 *
 * 定义投票相关的各种常量限制。
 */
internal object PollConstants {
    /** 最小答案数量 - 投票至少需要 2 个答案 */
    const val MIN_ANSWERS = 2

    /** 最大答案数量 - 投票最多可以有 20 个答案 */
    const val MAX_ANSWERS = 20

    /** 单个答案的最大长度（字符数） */
    const val MAX_ANSWER_LENGTH = 240

    /** 最大可选数量 - 每个投票最多选择 1 个答案 */
    const val MAX_SELECTIONS = 1
}
