/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.encryption.BackupState
import io.element.android.libraries.matrix.api.encryption.RecoveryState

/**
 * 安全备份根页面状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [SecureBackupRootState] 示例。
 */
open class SecureBackupRootStateProvider : PreviewParameterProvider<SecureBackupRootState> {
    /** 预览状态序列 */
    override val values: Sequence<SecureBackupRootState>
        get() = sequenceOf(
            aSecureBackupRootState(backupState = BackupState.UNKNOWN, doesBackupExistOnServer = AsyncData.Uninitialized),
            aSecureBackupRootState(backupState = BackupState.UNKNOWN, doesBackupExistOnServer = AsyncData.Success(true)),
            aSecureBackupRootState(backupState = BackupState.UNKNOWN, doesBackupExistOnServer = AsyncData.Success(false)),
            aSecureBackupRootState(backupState = BackupState.UNKNOWN, doesBackupExistOnServer = AsyncData.Failure(Exception("An error"))),
            aSecureBackupRootState(backupState = BackupState.WAITING_FOR_SYNC),
            aSecureBackupRootState(backupState = BackupState.CREATING),
            aSecureBackupRootState(
                backupState = BackupState.CREATING,
                enableAction = AsyncAction.Failure(Exception("Error")),
            ),
            aSecureBackupRootState(backupState = BackupState.ENABLING),
            aSecureBackupRootState(backupState = BackupState.RESUMING),
            aSecureBackupRootState(backupState = BackupState.DOWNLOADING),
            aSecureBackupRootState(backupState = BackupState.DISABLING),
            aSecureBackupRootState(backupState = BackupState.ENABLED),
            aSecureBackupRootState(backupState = BackupState.ENABLED, recoveryState = RecoveryState.UNKNOWN),
            aSecureBackupRootState(backupState = BackupState.ENABLED, recoveryState = RecoveryState.ENABLED),
            aSecureBackupRootState(backupState = BackupState.ENABLED, recoveryState = RecoveryState.DISABLED),
            aSecureBackupRootState(backupState = BackupState.ENABLED, recoveryState = RecoveryState.INCOMPLETE),
            aSecureBackupRootState(
                backupState = BackupState.UNKNOWN,
                doesBackupExistOnServer = AsyncData.Success(false),
                recoveryState = RecoveryState.ENABLED,
            ),
            aSecureBackupRootState(
                backupState = BackupState.UNKNOWN,
                doesBackupExistOnServer = AsyncData.Success(false),
                recoveryState = RecoveryState.ENABLED,
                displayKeyStorageDisabledError = true,
            ),
        )
}

/**
 * 创建安全备份根页面状态的辅助函数
 *
 * @param enableAction 启用操作状态
 * @param backupState 备份状态
 * @param doesBackupExistOnServer 服务器是否存在备份
 * @param recoveryState 恢复状态
 * @param displayKeyStorageDisabledError 是否显示密钥存储禁用错误
 * @param snackbarMessage 提示消息
 * @return 安全备份根页面状态实例
 */
fun aSecureBackupRootState(
    enableAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    backupState: BackupState = BackupState.UNKNOWN,
    doesBackupExistOnServer: AsyncData<Boolean> = AsyncData.Uninitialized,
    recoveryState: RecoveryState = RecoveryState.UNKNOWN,
    displayKeyStorageDisabledError: Boolean = false,
    snackbarMessage: SnackbarMessage? = null,
) = SecureBackupRootState(
    enableAction = enableAction,
    backupState = backupState,
    doesBackupExistOnServer = doesBackupExistOnServer,
    recoveryState = recoveryState,
    appName = "Element",
    displayKeyStorageDisabledError = displayKeyStorageDisabledError,
    snackbarMessage = snackbarMessage,
    eventSink = {},
)
