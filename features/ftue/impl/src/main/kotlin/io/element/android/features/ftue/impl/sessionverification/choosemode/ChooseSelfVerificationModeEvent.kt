/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

/**
 * 选择自验证方式事件密封接口
 *
 * 定义用户在选择自验证方式界面可能触发的事件。
 */
sealed interface ChooseSelfVerificationModeEvent {
    /**
     * 点击退出登录事件
     *
     * 当用户选择退出当前账户时触发。
     */
    data object SignOut : ChooseSelfVerificationModeEvent
}
