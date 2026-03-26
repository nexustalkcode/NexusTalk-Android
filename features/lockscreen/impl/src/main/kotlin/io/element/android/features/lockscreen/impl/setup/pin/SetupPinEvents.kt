/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.pin

/**
 * 设置 PIN 码事件密封接口
 *
 * 定义设置 PIN 码流程中的各种用户交互事件。
 */
sealed interface SetupPinEvents {
    /** PIN 码输入变化
     * @param entryAsText 输入的文本
     * @param fromConfirmationStep 是否来自确认步骤
     */
    data class OnPinEntryChanged(val entryAsText: String, val fromConfirmationStep: Boolean) : SetupPinEvents
    /** 清除失败状态 */
    data object ClearFailure : SetupPinEvents
}
