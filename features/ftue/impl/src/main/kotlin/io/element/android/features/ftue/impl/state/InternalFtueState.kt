/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.state

/**
 * 首次用户体验内部状态密封接口
 *
 * 定义 FTUE 流程的内部状态，用于跟踪用户引导流程的当前阶段。
 * 与公开的 FtueState 不同，此状态包含更详细的流程信息。
 */
sealed interface InternalFtueState {
    /** 未知状态 */
    data object Unknown : InternalFtueState

    /**
     * 未完成状态
     * @property nextStep 引导流程的下一步骤
     */
    data class Incomplete(
        val nextStep: FtueStep,
    ) : InternalFtueState

    /** 已完成状态 */
    data object Complete : InternalFtueState
}
