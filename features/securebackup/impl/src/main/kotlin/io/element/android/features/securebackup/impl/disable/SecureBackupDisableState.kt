/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.disable

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.encryption.BackupState

/**
 * 禁用安全备份状态数据类
 *
 * @property backupState 当前备份状态
 * @property disableAction 禁用备份的异步操作状态
 * @property appName 应用程序名称
 * @property eventSink 事件处理函数
 */
data class SecureBackupDisableState(
    /** 当前备份状态 */
    val backupState: BackupState,
    /** 禁用备份的异步操作状态 */
    val disableAction: AsyncAction<Unit>,
    /** 应用程序名称 */
    val appName: String,
    /** 事件处理函数 */
    val eventSink: (SecureBackupDisableEvents) -> Unit
)
