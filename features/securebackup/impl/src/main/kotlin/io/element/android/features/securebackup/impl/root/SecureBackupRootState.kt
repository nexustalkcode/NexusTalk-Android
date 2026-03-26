/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.root

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.encryption.BackupState
import io.element.android.libraries.matrix.api.encryption.RecoveryState

/**
 * 安全备份根页面状态数据类
 *
 * @property enableAction 启用密钥存储的异步操作状态
 * @property backupState 当前备份状态
 * @property doesBackupExistOnServer 服务器上是否存在备份
 * @property recoveryState 当前恢复状态
 * @property appName 应用程序名称
 * @property displayKeyStorageDisabledError 是否显示密钥存储禁用错误
 * @property snackbarMessage 提示消息
 * @property eventSink 事件处理函数
 */
data class SecureBackupRootState(
    /** 启用密钥存储的异步操作状态 */
    val enableAction: AsyncAction<Unit>,
    /** 当前备份状态 */
    val backupState: BackupState,
    /** 服务器上是否存在备份 */
    val doesBackupExistOnServer: AsyncData<Boolean>,
    /** 当前恢复状态 */
    val recoveryState: RecoveryState,
    /** 应用程序名称 */
    val appName: String,
    /** 是否显示密钥存储禁用错误 */
    val displayKeyStorageDisabledError: Boolean,
    /** 提示消息 */
    val snackbarMessage: SnackbarMessage?,
    /** 事件处理函数 */
    val eventSink: (SecureBackupRootEvents) -> Unit,
) {
    /**
     * 密钥存储是否已启用
     *
     * 根据当前备份状态和服务器备份状态判断密钥存储是否已启用。
     */
    val isKeyStorageEnabled: Boolean
        get() = when (backupState) {
            BackupState.UNKNOWN -> doesBackupExistOnServer.dataOrNull() == true
            BackupState.CREATING,
            BackupState.ENABLING,
            BackupState.RESUMING,
            BackupState.DOWNLOADING,
            BackupState.ENABLED -> true
            BackupState.WAITING_FOR_SYNC,
            BackupState.DISABLING -> false
        }
}
