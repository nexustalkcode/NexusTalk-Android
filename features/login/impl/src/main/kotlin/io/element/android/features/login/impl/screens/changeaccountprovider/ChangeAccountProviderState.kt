/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.changeaccountprovider

import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.changeserver.ChangeServerState
import kotlinx.collections.immutable.ImmutableList

/**
 * 更改账户提供商状态数据类
 *
 * @property accountProviders 可用的账户提供商列表
 * @property canSearchForAccountProviders 是否可以搜索账户提供商
 * @property changeServerState 服务器切换状态
 */
data class ChangeAccountProviderState(
    val accountProviders: ImmutableList<AccountProvider>,
    val canSearchForAccountProviders: Boolean,
    val changeServerState: ChangeServerState,
)
