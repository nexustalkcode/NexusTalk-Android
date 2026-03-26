/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.declineandblock

/**
 * 拒绝并封禁事件接口
 *
 * 定义了拒绝邀请并封禁用户功能的事件。
 * 用于在 UI 层和业务逻辑层之间传递用户交互事件。
 */
sealed interface DeclineAndBlockEvents {
    /**
     * 更新举报原因事件
     *
     * @property reason 举报原因
     */
    data class UpdateReportReason(val reason: String) : DeclineAndBlockEvents

    /** 切换举报房间开关事件 */
    data object ToggleReportRoom : DeclineAndBlockEvents

    /** 切换封禁用户开关事件 */
    data object ToggleBlockUser : DeclineAndBlockEvents

    /** 执行拒绝操作事件 */
    data object Decline : DeclineAndBlockEvents

    /** 清除拒绝操作状态事件 */
    data object ClearDeclineAction : DeclineAndBlockEvents
}
