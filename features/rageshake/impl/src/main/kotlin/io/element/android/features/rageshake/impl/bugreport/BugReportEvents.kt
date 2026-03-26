/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.bugreport

/**
 * 问题报告事件密封接口
 *
 * 定义了问题报告功能可能产生的各种事件，用于状态管理和事件处理。
 */
sealed interface BugReportEvents {
    /**
     * 发送问题报告
     *
     * 触发问题报告的发送流程。
     */
    data object SendBugReport : BugReportEvents

    /**
     * 重置所有数据
     *
     * 重置问题报告表单的所有数据和状态。
     */
    data object ResetAll : BugReportEvents

    /**
     * 清除错误
     *
     * 清除当前显示的错误状态。
     */
    data object ClearError : BugReportEvents

    /**
     * 设置问题描述
     *
     * 更新问题报告中的问题描述内容。
     *
     * @param description 问题描述文本
     */
    data class SetDescription(val description: String) : BugReportEvents

    /**
     * 设置是否发送日志
     *
     * 控制在问题报告中是否包含日志文件。
     *
     * @param sendLog 是否发送日志
     */
    data class SetSendLog(val sendLog: Boolean) : BugReportEvents

    /**
     * 设置是否允许联系
     *
     * 设置用户是否同意被直接联系以获取更多信息。
     *
     * @param canContact 是否允许被联系
     */
    data class SetCanContact(val canContact: Boolean) : BugReportEvents

    /**
     * 设置是否发送截图
     *
     * 控制在问题报告中是否包含屏幕截图。
     *
     * @param sendScreenshot 是否发送截图
     */
    data class SetSendScreenshot(val sendScreenshot: Boolean) : BugReportEvents

    /**
     * 设置是否发送推送规则
     *
     * 控制在问题报告中是否包含推送规则信息。
     *
     * @param sendPushRules 是否发送推送规则
     */
    data class SetSendPushRules(val sendPushRules: Boolean) : BugReportEvents
}
