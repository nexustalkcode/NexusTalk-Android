/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.bugreport

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 问题报告状态预览参数提供者
 *
 * 用于在预览中提供不同状态的 BugReportState 实例。
 */
open class BugReportStateProvider : PreviewParameterProvider<BugReportState> {
    /**
     * 提供预览状态序列
     *
     * 包含多种典型的问题报告状态用于UI预览：
     * 1. 默认状态
     * 2. 填写完成状态，包含截图
     * 3. 上传中状态
     * 4. 上传成功状态
     * 5. 表单验证失败状态
     */
    override val values: Sequence<BugReportState>
        get() = sequenceOf(
            aBugReportState(),
            aBugReportState().copy(
                formState = BugReportFormState.Default.copy(
                    description = "A long enough description",
                    sendScreenshot = true,
                ),
                hasCrashLogs = true,
                screenshotUri = "aUri"
            ),
            aBugReportState().copy(sending = AsyncAction.Loading),
            aBugReportState().copy(sending = AsyncAction.Success(Unit)),
            aBugReportState().copy(sending = AsyncAction.Failure(BugReportFormError.DescriptionTooShort)),
        )
}

/**
 * 创建默认问题报告状态的辅助函数
 *
 * 用于预览和测试，创建一个默认的 BugReportState 实例。
 *
 * @return BugReportState 默认状态的实例
 */
fun aBugReportState() = BugReportState(
    formState = BugReportFormState.Default,
    hasCrashLogs = false,
    screenshotUri = null,
    sendingProgress = 0F,
    sending = AsyncAction.Uninitialized,
    eventSink = {}
)
