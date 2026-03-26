/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.setup.views

/**
 * 恢复密钥视图状态数据类
 *
 * @property recoveryKeyUserStory 恢复密钥用户场景
 * @property formattedRecoveryKey 格式化后的恢复密钥
 * @property displayTextFieldContents 是否显示文本字段内容
 * @property inProgress 是否正在进行操作
 */
data class RecoveryKeyViewState(
    /** 恢复密钥用户场景（设置、更改、输入） */
    val recoveryKeyUserStory: RecoveryKeyUserStory,
    /** 格式化后的恢复密钥 */
    val formattedRecoveryKey: String?,
    /** 是否显示文本字段内容 */
    val displayTextFieldContents: Boolean,
    /** 是否正在进行操作 */
    val inProgress: Boolean,
)

/**
 * 恢复密钥用户场景枚举
 *
 * 定义了恢复密钥在应用中的不同使用场景。
 */
enum class RecoveryKeyUserStory {
    /** 设置恢复密钥场景 */
    Setup,
    /** 更改恢复密钥场景 */
    Change,
    /** 输入恢复密钥场景 */
    Enter,
}
