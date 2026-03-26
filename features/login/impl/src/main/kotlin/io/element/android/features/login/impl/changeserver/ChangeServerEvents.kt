/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.changeserver

import io.element.android.features.login.impl.accountprovider.AccountProvider

/**
 * 更改服务器事件接口
 *
 * 定义更改账户提供商（homeserver）时触发的事件。
 * 这是一个密封接口，用于统一处理登录流程中的服务器切换事件。
 *
 * @see ChangeServerPresenter 处理这些事件的 Presenter
 * @see ChangeServerState 事件对应的状态
 */
sealed interface ChangeServerEvents {
    /**
     * 更改服务器事件
     *
     * 当用户选择新的账户提供商时触发。
     *
     * @property accountProvider 新的账户提供商
     */
    data class ChangeServer(val accountProvider: AccountProvider) : ChangeServerEvents

    /**
     * 清除错误事件
     *
     * 当用户希望清除当前显示的错误信息时触发。
     */
    data object ClearError : ChangeServerEvents
}
