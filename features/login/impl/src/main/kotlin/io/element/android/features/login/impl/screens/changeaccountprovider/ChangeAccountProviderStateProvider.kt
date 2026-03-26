/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.changeaccountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.accountprovider.anAccountProvider
import io.element.android.features.login.impl.changeserver.ChangeServerState
import io.element.android.features.login.impl.changeserver.aChangeServerState
import kotlinx.collections.immutable.toImmutableList

/**
 * 更改账户提供商状态预览参数提供者
 *
 * 用于在 Compose 预览中提供不同状态的 ChangeAccountProviderState 测试数据。
 */
open class ChangeAccountProviderStateProvider : PreviewParameterProvider<ChangeAccountProviderState> {
    override val values: Sequence<ChangeAccountProviderState>
        get() = sequenceOf(
            aChangeAccountProviderState(),
            aChangeAccountProviderState(canSearchForAccountProviders = false),
            // Add other state here
        )
}

/**
 * 创建测试用更改账户提供商状态
 *
 * 辅助函数，用于在测试和预览中快速创建 ChangeAccountProviderState 对象。
 */
fun aChangeAccountProviderState(
    accountProviders: List<AccountProvider> = listOf(
        anAccountProvider()
    ),
    canSearchForAccountProviders: Boolean = true,
    changeServerState: ChangeServerState = aChangeServerState(),
) = ChangeAccountProviderState(
    accountProviders = accountProviders.toImmutableList(),
    canSearchForAccountProviders = canSearchForAccountProviders,
    changeServerState = changeServerState,
)
