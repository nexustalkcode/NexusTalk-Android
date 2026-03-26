/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.enter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyUserStory
import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyViewState
import io.element.android.features.securebackup.impl.setup.views.aFormattedRecoveryKey
import io.element.android.libraries.architecture.AsyncAction

/**
 * 输入恢复密钥状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [SecureBackupEnterRecoveryKeyState] 示例。
 */
open class SecureBackupEnterRecoveryKeyStateProvider : PreviewParameterProvider<SecureBackupEnterRecoveryKeyState> {
    /** 预览状态序列 */
    override val values: Sequence<SecureBackupEnterRecoveryKeyState>
        get() = sequenceOf(
            aSecureBackupEnterRecoveryKeyState(recoveryKey = ""),
            aSecureBackupEnterRecoveryKeyState(),
            aSecureBackupEnterRecoveryKeyState(submitAction = AsyncAction.Loading),
            aSecureBackupEnterRecoveryKeyState(submitAction = AsyncAction.Failure(Exception("A Failure"))),
            aSecureBackupEnterRecoveryKeyState(displayTextFieldContents = false),
        )
}

/**
 * 创建输入恢复密钥状态的辅助函数
 *
 * @param recoveryKey 恢复密钥内容
 * @param isSubmitEnabled 是否允许提交
 * @param displayTextFieldContents 是否显示文本字段内容
 * @param submitAction 提交操作状态
 * @param eventSink 事件处理函数
 * @return 输入恢复密钥状态实例
 */
fun aSecureBackupEnterRecoveryKeyState(
    recoveryKey: String = aFormattedRecoveryKey(),
    isSubmitEnabled: Boolean = recoveryKey.isNotEmpty(),
    displayTextFieldContents: Boolean = true,
    submitAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (SecureBackupEnterRecoveryKeyEvents) -> Unit = {},
) = SecureBackupEnterRecoveryKeyState(
    recoveryKeyViewState = RecoveryKeyViewState(
        recoveryKeyUserStory = RecoveryKeyUserStory.Enter,
        formattedRecoveryKey = recoveryKey,
        displayTextFieldContents = displayTextFieldContents,
        inProgress = submitAction.isLoading(),
    ),
    isSubmitEnabled = isSubmitEnabled,
    submitAction = submitAction,
    eventSink = eventSink,
)
