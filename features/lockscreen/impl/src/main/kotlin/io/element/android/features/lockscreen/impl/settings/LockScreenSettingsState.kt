/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.settings

/**
 * 锁屏设置状态数据类
 *
 * 表示锁屏设置界面的当前状态。
 *
 * @property showRemovePinOption 是否显示移除 PIN 码选项
 * @property isBiometricEnabled 是否启用生物识别
 * @property showRemovePinConfirmation 是否显示移除 PIN 码确认对话框
 * @property showToggleBiometric 是否显示切换生物识别选项
 * @property eventSink 事件处理函数
 */
data class LockScreenSettingsState(
    val showRemovePinOption: Boolean,
    val isBiometricEnabled: Boolean,
    val showRemovePinConfirmation: Boolean,
    val showToggleBiometric: Boolean,
    val eventSink: (LockScreenSettingsEvents) -> Unit
)
