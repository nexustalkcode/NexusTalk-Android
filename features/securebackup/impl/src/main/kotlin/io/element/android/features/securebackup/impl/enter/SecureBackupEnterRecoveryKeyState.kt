/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.enter

import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyViewState
import io.element.android.libraries.architecture.AsyncAction

/**
 * 输入恢复密钥状态数据类
 *
 * @property recoveryKeyViewState 恢复密钥视图状态
 * @property isSubmitEnabled 是否允许提交
 * @property submitAction 提交操作的异步状态
 * @property eventSink 事件处理函数
 */
data class SecureBackupEnterRecoveryKeyState(
    /** 恢复密钥视图状态 */
    val recoveryKeyViewState: RecoveryKeyViewState,
    /** 是否允许提交 */
    val isSubmitEnabled: Boolean,
    /** 提交操作的异步状态 */
    val submitAction: AsyncAction<Unit>,
    /** 事件处理函数 */
    val eventSink: (SecureBackupEnterRecoveryKeyEvents) -> Unit
)
