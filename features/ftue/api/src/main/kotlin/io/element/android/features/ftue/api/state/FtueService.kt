/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.api.state

import kotlinx.coroutines.flow.StateFlow

/**
 * 首次用户体验服务接口
 *
 * 用于管理首次用户体验（又称新手引导）状态的服务接口。
 */
interface FtueService {
    /** FTUE 状态的 StateFlow */
    val state: StateFlow<FtueState>
}

/**
 * FTUE 状态密封接口
 *
 * 定义首次用户体验的不同状态。
 */
sealed interface FtueState {
    /** 未知状态，暂无操作 */
    data object Unknown : FtueState

    /** 未完成状态，应显示 FTUE 流程 */
    data object Incomplete : FtueState

    /** 已完成状态，不应再显示 FTUE 流程 */
    data object Complete : FtueState
}
