/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.enter

/**
 * 输入恢复密钥事件密封接口
 *
 * 定义了输入恢复密钥页面的用户交互事件。
 */
sealed interface SecureBackupEnterRecoveryKeyEvents {
    /** 恢复密钥内容变化事件 */
    data class OnRecoveryKeyChange(val recoveryKey: String) : SecureBackupEnterRecoveryKeyEvents

    /** 切换恢复密钥字段内容可见性事件 */
    data class ChangeRecoveryKeyFieldContentsVisibility(val visible: Boolean) : SecureBackupEnterRecoveryKeyEvents

    /** 提交恢复密钥事件 */
    data object Submit : SecureBackupEnterRecoveryKeyEvents

    /** 清除对话框事件 */
    data object ClearDialog : SecureBackupEnterRecoveryKeyEvents
}
