/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.libraries.architecture.AsyncData

/**
 * 选择自验证方式状态数据类
 *
 * 表示用户选择会话验证方式界面的当前状态。
 * 包含按钮状态和直接退出登录状态。
 *
 * @property buttonsState 按钮状态的异步数据，包含各验证方式是否可用
 * @property directLogoutState 直接退出登录状态，用于在用户选择退出时显示 UI
 * @property eventSink 事件处理函数，用于将用户操作事件传递给 Presenter
 */
data class ChooseSelfVerificationModeState(
    /** 按钮状态，包含各验证方式按钮的可用状态 */
    val buttonsState: AsyncData<ButtonsState>,
    /** 直接退出登录状态 */
    val directLogoutState: DirectLogoutState,
    /** 事件处理函数，用于传递用户操作事件 */
    val eventSink: (ChooseSelfVerificationModeEvent) -> Unit,
) {
    /**
     * 按钮状态数据类
     *
     * 表示验证方式选择按钮的可用状态。
     *
     * @property canUseAnotherDevice 是否可以使用另一台设备验证（有其他已登录设备）
     * @property canEnterRecoveryKey 是否可以输入恢复密钥（已设置恢复密钥）
     */
    data class ButtonsState(
        /** 是否可以使用另一台设备验证 */
        val canUseAnotherDevice: Boolean,
        /** 是否可以输入恢复密钥 */
        val canEnterRecoveryKey: Boolean,
    )
}
