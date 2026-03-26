/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.setup

import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyViewState

/**
 * 安全备份设置状态数据类
 *
 * @property isChangeRecoveryKeyUserStory 是否为更改恢复密钥流程
 * @property recoveryKeyViewState 恢复密钥视图状态
 * @property showSaveConfirmationDialog 是否显示保存确认对话框
 * @property setupState 设置流程状态
 * @property eventSink 事件处理函数
 */
data class SecureBackupSetupState(
    /** 是否为更改恢复密钥流程 */
    val isChangeRecoveryKeyUserStory: Boolean,
    /** 恢复密钥视图状态 */
    val recoveryKeyViewState: RecoveryKeyViewState,
    /** 是否显示保存确认对话框 */
    val showSaveConfirmationDialog: Boolean,
    /** 设置流程状态 */
    val setupState: SetupState,
    /** 事件处理函数 */
    val eventSink: (SecureBackupSetupEvents) -> Unit
)

/**
 * 设置流程状态密封接口
 *
 * 定义了安全备份设置流程的不同状态。
 */
sealed interface SetupState {
    /** 初始状态 */
    data object Init : SetupState

    /** 正在创建状态 */
    data object Creating : SetupState

    /** 已创建状态，包含格式化的恢复密钥 */
    data class Created(val formattedRecoveryKey: String) : SetupState

    /** 已创建并保存状态，包含格式化的恢复密钥 */
    data class CreatedAndSaved(val formattedRecoveryKey: String) : SetupState

    /** 错误状态，包含异常信息 */
    data class Error(val exception: Exception) : SetupState
}

/**
 * 从设置状态中获取恢复密钥
 *
 * @return 格式化后的恢复密钥，如果不存在则返回 null
 */
fun SetupState.recoveryKey(): String? = when (this) {
    is SetupState.Created -> formattedRecoveryKey
    is SetupState.CreatedAndSaved -> formattedRecoveryKey
    else -> null
}
