/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.chooseaccountprovider

import io.element.android.features.login.impl.accountprovider.AccountProvider

/**
 * 选择账户提供商事件接口
 *
 * 定义选择账户提供商流程中触发的事件。
 */
sealed interface ChooseAccountProviderEvents {
    /**
     * 选择账户提供商事件
     *
     * @property accountProvider 选中的账户提供商
     */
    data class SelectAccountProvider(val accountProvider: AccountProvider) : ChooseAccountProviderEvents

    /** 继续事件 - 确认选择并继续登录流程 */
    data object Continue : ChooseAccountProviderEvents

    /** 清除错误事件 */
    data object ClearError : ChooseAccountProviderEvents
}
