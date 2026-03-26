/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

/**
 * 账户停用事件 sealed 接口
 *
 * 定义账户停用界面所有可能发生的用户事件。
 * 使用 sealed interface 确保所有事件类型都被处理。
 */
sealed interface AccountDeactivationEvents {
    /**
     * 设置是否擦除数据
     *
     * @property eraseData 是否在停用账户时擦除所有消息数据
     */
    data class SetEraseData(val eraseData: Boolean) : AccountDeactivationEvents

    /**
     * 设置账户密码
     *
     * @property password 用户输入的账户密码，用于验证身份
     */
    data class SetPassword(val password: String) : AccountDeactivationEvents

    /**
     * 提交账户停用请求
     *
     * @property isRetry 是否为重试操作（true 表示用户点击重试按钮）
     */
    data class DeactivateAccount(val isRetry: Boolean) : AccountDeactivationEvents

    /**
     * 关闭所有对话框
     *
     * 关闭确认对话框或错误对话框，重置操作状态
     */
    data object CloseDialogs : AccountDeactivationEvents
}
