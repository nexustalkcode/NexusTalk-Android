/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.encryption.BackupState
import io.element.android.libraries.matrix.api.encryption.BackupUploadState
import io.element.android.libraries.matrix.api.encryption.RecoveryState

/**
 * 退出登录状态数据类
 *
 * @property isLastDevice 是否为最后一个设备
 * @property backupState 备份状态
 * @property doesBackupExistOnServer 服务器上是否存在备份
 * @property recoveryState 恢复状态
 * @property backupUploadState 备份上传状态
 * @property waitingForALongTime 是否已等待很长时间
 * @property logoutAction 退出登录操作的异步状态
 * @property eventSink 事件处理函数
 */
data class LogoutState(
    val isLastDevice: Boolean,
    val backupState: BackupState,
    val doesBackupExistOnServer: Boolean,
    val recoveryState: RecoveryState,
    val backupUploadState: BackupUploadState,
    val waitingForALongTime: Boolean,
    val logoutAction: AsyncAction<Unit>,
    val eventSink: (LogoutEvents) -> Unit,
)
