/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.setup

/**
 * 安全备份设置事件密封接口
 *
 * 定义了安全备份设置页面的用户交互事件。
 */
sealed interface SecureBackupSetupEvents {
    /** 创建恢复密钥事件 */
    data object CreateRecoveryKey : SecureBackupSetupEvents

    /** 恢复密钥已保存事件 */
    data object RecoveryKeyHasBeenSaved : SecureBackupSetupEvents

    /** 完成事件 */
    data object Done : SecureBackupSetupEvents

    /** 关闭对话框事件 */
    data object DismissDialog : SecureBackupSetupEvents
}
