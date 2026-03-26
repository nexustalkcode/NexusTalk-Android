/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.report

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 举报消息状态提供者
 *
 * 用于预览功能的参数提供者，提供各种举报消息状态的示例。
 * 继承自PreviewParameterProvider，用于Compose预览功能。
 */
open class ReportMessageStateProvider : PreviewParameterProvider<ReportMessageState> {
    override val values: Sequence<ReportMessageState>
        get() = sequenceOf(
            aReportMessageState(),
            aReportMessageState(reason = "This user is making the chat very toxic."),
            aReportMessageState(reason = "This user is making the chat very toxic.", blockUser = true),
            aReportMessageState(reason = "This user is making the chat very toxic.", blockUser = true, result = AsyncAction.Loading),
            aReportMessageState(reason = "This user is making the chat very toxic.", blockUser = true, result = AsyncAction.Failure(RuntimeException("error"))),
            aReportMessageState(reason = "This user is making the chat very toxic.", blockUser = true, result = AsyncAction.Success(Unit)),
            // Add other states here
        )
}

/**
 * 创建举报消息状态的辅助函数
 *
 * 用于在测试和预览中快速创建ReportMessageState实例。
 *
 * @param reason 举报原因，默认为空字符串
 * @param blockUser 是否屏蔽用户，默认为false
 * @param result 举报操作的异步状态，默认为未初始化
 * @return 新的ReportMessageState实例
 */
fun aReportMessageState(
    reason: String = "",
    blockUser: Boolean = false,
    result: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = ReportMessageState(
    reason = reason,
    blockUser = blockUser,
    result = result,
    eventSink = {}
)
