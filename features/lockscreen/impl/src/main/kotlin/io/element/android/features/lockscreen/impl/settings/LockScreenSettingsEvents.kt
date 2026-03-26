/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.settings

/**
 * 锁屏设置事件密封接口
 *
 * 定义锁屏设置界面的各种用户交互事件。
 */
sealed interface LockScreenSettingsEvents {
    /** 点击移除 PIN 码 */
    data object OnRemovePin : LockScreenSettingsEvents
    /** 确认移除 PIN 码 */
    data object ConfirmRemovePin : LockScreenSettingsEvents
    /** 取消移除 PIN 码 */
    data object CancelRemovePin : LockScreenSettingsEvents
    /** 切换生物识别解锁开关 */
    data object ToggleBiometricAllowed : LockScreenSettingsEvents
}
