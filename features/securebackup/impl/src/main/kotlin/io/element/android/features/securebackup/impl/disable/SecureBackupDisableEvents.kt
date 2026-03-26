/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.disable

/**
 * 禁用安全备份事件密封接口
 *
 * 定义了禁用安全备份页面的用户交互事件。
 */
sealed interface SecureBackupDisableEvents {
    /** 禁用备份事件 - 触发禁用安全备份操作 */
    data object DisableBackup : SecureBackupDisableEvents

    /** 关闭对话框事件 - 关闭任何显示的对话框 */
    data object DismissDialogs : SecureBackupDisableEvents
}
