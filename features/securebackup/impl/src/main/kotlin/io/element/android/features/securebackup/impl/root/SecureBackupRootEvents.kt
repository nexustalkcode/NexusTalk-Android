/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.root

/**
 * 安全备份根页面事件密封接口
 *
 * 定义了安全备份根页面的用户交互事件。
 */
sealed interface SecureBackupRootEvents {
    /** 重试密钥备份状态事件 */
    data object RetryKeyBackupState : SecureBackupRootEvents

    /** 启用密钥存储事件 */
    data object EnableKeyStorage : SecureBackupRootEvents

    /** 显示密钥存储禁用错误事件 */
    data object DisplayKeyStorageDisabledError : SecureBackupRootEvents

    /** 关闭对话框事件 */
    data object DismissDialog : SecureBackupRootEvents
}
