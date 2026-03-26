/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.setup

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyUserStory
import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyViewState
import io.element.android.features.securebackup.impl.setup.views.aFormattedRecoveryKey

/**
 * 安全备份设置状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [SecureBackupSetupState] 示例。
 */
open class SecureBackupSetupStateProvider : PreviewParameterProvider<SecureBackupSetupState> {
    /** 预览状态序列 */
    override val values: Sequence<SecureBackupSetupState>
        get() = sequenceOf(
            aSecureBackupSetupState(setupState = SetupState.Init),
            aSecureBackupSetupState(setupState = SetupState.Creating),
            aSecureBackupSetupState(setupState = SetupState.Created(aFormattedRecoveryKey())),
            aSecureBackupSetupState(setupState = SetupState.CreatedAndSaved(aFormattedRecoveryKey())),
            aSecureBackupSetupState(
                setupState = SetupState.CreatedAndSaved(aFormattedRecoveryKey()),
                showSaveConfirmationDialog = true,
            ),
            aSecureBackupSetupState(setupState = SetupState.Error(Exception("Test error"))),
            // Add other states here
        )
}

/**
 * 创建安全备份设置状态的辅助函数
 *
 * @param setupState 设置状态
 * @param showSaveConfirmationDialog 是否显示保存确认对话框
 * @return 安全备份设置状态实例
 */
fun aSecureBackupSetupState(
    setupState: SetupState = SetupState.Init,
    showSaveConfirmationDialog: Boolean = false,
) = SecureBackupSetupState(
    isChangeRecoveryKeyUserStory = false,
    setupState = setupState,
    showSaveConfirmationDialog = showSaveConfirmationDialog,
    recoveryKeyViewState = setupState.toRecoveryKeyViewState(),
    eventSink = {}
)

/**
 * 将设置状态转换为恢复密钥视图状态
 *
 * @return 恢复密钥视图状态
 */
private fun SetupState.toRecoveryKeyViewState(): RecoveryKeyViewState {
    return RecoveryKeyViewState(
        recoveryKeyUserStory = RecoveryKeyUserStory.Setup,
        formattedRecoveryKey = recoveryKey(),
        displayTextFieldContents = true,
        inProgress = this is SetupState.Creating,
    )
}
