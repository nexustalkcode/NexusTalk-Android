/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl.tools

import io.element.android.libraries.matrix.api.encryption.BackupUploadState
import io.element.android.libraries.matrix.api.encryption.SteadyStateException

/**
 * 检查备份上传状态是否为"正在备份中"
 *
 * 当备份正在等待上传或正在进行上传时返回 true。
 * 如果发生网络连接异常，也视为正在备份中（需要提醒用户）。
 *
 * @return Boolean 是否正在进行备份
 */
internal fun BackupUploadState.isBackingUp(): Boolean {
    return when (this) {
        // 等待备份上传
        BackupUploadState.Waiting,
        // 正在上传备份
        is BackupUploadState.Uploading -> true
        // 备份正在进行中，但出现了网络问题，需要警告用户
        is BackupUploadState.SteadyException -> exception is SteadyStateException.Connection
        // 其他状态（未知、已完成、错误）不视为正在备份
        BackupUploadState.Unknown,
        BackupUploadState.Done,
        BackupUploadState.Error -> false
    }
}
