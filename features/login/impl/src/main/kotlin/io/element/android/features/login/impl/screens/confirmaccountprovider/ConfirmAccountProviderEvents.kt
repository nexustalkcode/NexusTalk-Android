/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

/**
 * 账户提供商确认页面的事件密封接口
 *
 * 定义了该页面所有用户交互事件类型，用于 MVI 架构中的事件驱动
 *
 * 事件流向：[ConfirmAccountProviderView] -> [ConfirmAccountProviderPresenter] -> [ConfirmAccountProviderState]
 */
sealed interface ConfirmAccountProviderEvents {
    /**
     * 继续事件 - 用户点击继续按钮
     *
     * 触发登录或注册流程，根据当前页面模式（登录/注册）调用相应的认证逻辑
     *
     * 使用场景：用户确认选择的账户提供商后，点击继续按钮触发后续认证流程
     */
    data object Continue : ConfirmAccountProviderEvents

    /**
     * 清除错误事件 - 用户请求清除错误信息
     *
     * 当登录或注册过程中发生错误时，用户可以触发此事件来清除界面上的错误显示
     *
     * 使用场景：显示错误信息后，用户选择忽略错误并继续操作
     */
    data object ClearError : ConfirmAccountProviderEvents
}
