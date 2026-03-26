/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.report

/**
 * 举报消息事件密封接口
 *
 * 定义举报消息流程中的各种事件类型。
 */
sealed interface ReportMessageEvents {
    /**
     * 更新举报原因
     *
     * @property reason 举报原因
     */
    data class UpdateReason(val reason: String) : ReportMessageEvents

    /** 切换是否屏蔽用户 */
    data object ToggleBlockUser : ReportMessageEvents

    /** 提交举报 */
    data object Report : ReportMessageEvents

    /** 清除错误 */
    data object ClearError : ReportMessageEvents
}
