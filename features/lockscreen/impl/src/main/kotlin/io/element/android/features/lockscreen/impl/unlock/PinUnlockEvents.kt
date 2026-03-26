/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock

import io.element.android.features.lockscreen.impl.unlock.keypad.PinKeypadModel

/**
 * PIN 解锁事件密封接口
 *
 * 定义 PIN 解锁界面的各种用户交互事件。
 */
sealed interface PinUnlockEvents {
    /** 按下 PIN 键盘按键
     * @param pinKeypadModel 按键模型
     */
    data class OnPinKeypadPressed(val pinKeypadModel: PinKeypadModel) : PinUnlockEvents
    /** PIN 码输入变化
     * @param entryAsText 输入的文本
     */
    data class OnPinEntryChanged(val entryAsText: String) : PinUnlockEvents
    /** 忘记 PIN 码 */
    data object OnForgetPin : PinUnlockEvents
    /** 清除退出提示 */
    data object ClearSignOutPrompt : PinUnlockEvents
    /** 退出登录 */
    data object SignOut : PinUnlockEvents
    /** 使用生物识别解锁 */
    data object OnUseBiometric : PinUnlockEvents
    /** 清除生物识别错误 */
    data object ClearBiometricError : PinUnlockEvents
}
