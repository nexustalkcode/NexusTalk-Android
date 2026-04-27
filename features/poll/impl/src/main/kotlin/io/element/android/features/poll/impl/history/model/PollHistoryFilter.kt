/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.history.model

import io.element.android.features.poll.api.R

/**
 * 投票历史过滤器枚举
 *
 * 用于过滤投票历史列表的选项。
 */
enum class PollHistoryFilter(val stringResource: Int) {
    /** 进行中投票 - 显示尚未结束的投票 */
    ONGOING(R.string.screen_polls_history_filter_ongoing),

    /** 已结束投票 - 显示已结束的投票 */
    PAST(R.string.screen_polls_history_filter_past),
}
