/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.disable

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.encryption.BackupState

/**
 * 禁用安全备份状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [SecureBackupDisableState] 示例。
 */
open class SecureBackupDisableStateProvider : PreviewParameterProvider<SecureBackupDisableState> {
    /** 预览状态序列 */
    override val values: Sequence<SecureBackupDisableState>
        get() = sequenceOf(
            aSecureBackupDisableState(),
            aSecureBackupDisableState(disableAction = AsyncAction.ConfirmingNoParams),
            aSecureBackupDisableState(disableAction = AsyncAction.Loading),
            aSecureBackupDisableState(disableAction = AsyncAction.Failure(Exception("Failed to disable"))),
            // Add other states here
        )
}

/**
 * 创建禁用安全备份状态的辅助函数
 *
 * @param backupState 备份状态，默认为未知状态
 * @param disableAction 禁用操作状态，默认为未初始化
 * @return 禁用安全备份状态实例
 */
fun aSecureBackupDisableState(
    backupState: BackupState = BackupState.UNKNOWN,
    disableAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = SecureBackupDisableState(
    backupState = backupState,
    disableAction = disableAction,
    appName = "Element",
    eventSink = {}
)
