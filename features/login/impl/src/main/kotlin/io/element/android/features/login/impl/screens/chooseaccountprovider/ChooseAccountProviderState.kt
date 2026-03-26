/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.chooseaccountprovider

import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList

/**
 * 选择账户提供商状态数据类
 *
 * @property accountProviders 可用账户提供商列表
 * @property selectedAccountProvider 当前选中的账户提供商
 * @property loginMode 登录模式的异步状态
 * @property eventSink 事件处理函数
 */
data class ChooseAccountProviderState(
    val accountProviders: ImmutableList<AccountProvider>,
    val selectedAccountProvider: AccountProvider?,
    val loginMode: AsyncData<LoginMode>,
    val eventSink: (ChooseAccountProviderEvents) -> Unit,
) {
    /** 是否允许提交 - 必须选中提供商且不在处理中 */
    val submitEnabled: Boolean
        get() = selectedAccountProvider != null && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
