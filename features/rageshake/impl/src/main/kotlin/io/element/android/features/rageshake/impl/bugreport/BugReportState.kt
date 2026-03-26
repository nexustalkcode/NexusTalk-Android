/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.bugreport

import android.os.Parcelable
import io.element.android.libraries.architecture.AsyncAction
import kotlinx.parcelize.Parcelize

/**
 * 问题报告状态数据类
 *
 * 表示问题报告功能的完整状态，包括表单数据、上传进度和错误状态。
 *
 * @property formState 表单数据状态
 * @property hasCrashLogs 是否有崩溃日志
 * @property screenshotUri 截图的URI（如果有）
 * @param sendingProgress 上传进度（0.0到1.0）
 * @param sending 异步操作状态
 * @property eventSink 事件处理函数
 */
data class BugReportState(
    val formState: BugReportFormState,
    val hasCrashLogs: Boolean,
    val screenshotUri: String?,
    val sendingProgress: Float,
    val sending: AsyncAction<Unit>,
    val eventSink: (BugReportEvents) -> Unit
) {
    val submitEnabled = sending !is AsyncAction.Loading
    val isDescriptionInError = sending is AsyncAction.Failure &&
        sending.error is BugReportFormError.DescriptionTooShort
}

/**
 * 问题报告表单状态数据类
 *
 * 表示问题报告表单的完整状态，包含用户填写的内容和选择的选项。
 * 使用 @Parcelize 注解以支持在进程间传递。
 *
 * @property description 问题描述
 * @property sendLogs 是否发送日志
 * @property canContact 是否允许被联系
 * @property sendScreenshot 是否发送截图
 * @property sendPushRules 是否发送推送规则
 */
@Parcelize
data class BugReportFormState(
    val description: String,
    val sendLogs: Boolean,
    val canContact: Boolean,
    val sendScreenshot: Boolean,
    val sendPushRules: Boolean,
) : Parcelable {
    companion object {
        /**
         * 默认表单状态
         *
         * 包含所有字段的默认值，用于初始化表单。
         */
        val Default = BugReportFormState(
            description = "",
            sendLogs = true,
            canContact = false,
            sendScreenshot = false,
            sendPushRules = false,
        )
    }
}
