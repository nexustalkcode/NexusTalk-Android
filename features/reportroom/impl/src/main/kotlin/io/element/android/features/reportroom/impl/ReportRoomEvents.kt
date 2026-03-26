/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

/**
 * 举报房间功能的事件 sealed 接口
 *
 * 定义了用户在举报房间界面可以触发的所有事件类型
 */
sealed interface ReportRoomEvents {
    /**
     * 更新举报原因的事件
     *
     * @property reason 用户输入的举报原因文本
     */
    data class UpdateReason(val reason: String) : ReportRoomEvents

    /**
     * 切换是否离开房间的事件
     *
     * 点击"离开房间"开关时触发，用于切换离开房间的状态
     */
    data object ToggleLeaveRoom : ReportRoomEvents

    /**
     * 提交举报事件
     *
     * 用户点击"举报"按钮时触发，执行举报房间的操作
     */
    data object Report : ReportRoomEvents

    /**
     * 清除举报操作状态的事件
     *
     * 用于重置举报操作的状态，例如在错误提示后清除错误状态
     */
    data object ClearReportAction : ReportRoomEvents
}
