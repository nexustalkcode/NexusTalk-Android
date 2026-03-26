/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.searchaccountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.appconfig.AuthenticationConfig
import io.element.android.features.login.impl.changeserver.aChangeServerState
import io.element.android.features.login.impl.resolver.HomeserverData
import io.element.android.libraries.architecture.AsyncData

/**
 * 搜索账户提供商状态预览参数提供者
 */
open class SearchAccountProviderStateProvider : PreviewParameterProvider<SearchAccountProviderState> {
    override val values: Sequence<SearchAccountProviderState>
        get() = sequenceOf(
            aSearchAccountProviderState(),
            aSearchAccountProviderState(userInputResult = AsyncData.Success(aHomeserverDataList())),
            // Add other state here
        )
}

/**
 * 创建测试用搜索账户提供商状态
 */
fun aSearchAccountProviderState(
    userInput: String = "",
    userInputResult: AsyncData<List<HomeserverData>> = AsyncData.Uninitialized,
) = SearchAccountProviderState(
    userInput = userInput,
    userInputResult = userInputResult,
    changeServerState = aChangeServerState(),
    eventSink = {}
)

/**
 * 创建测试用 Homeserver 数据列表
 */
fun aHomeserverDataList(): List<HomeserverData> {
    return listOf(
        aHomeserverData(homeserverUrl = AuthenticationConfig.MATRIX_ORG_URL),
        aHomeserverData(homeserverUrl = "https://no.sliding.sync"),
        aHomeserverData(homeserverUrl = "https://invalid"),
    )
}

/**
 * 创建测试用 Homeserver 数据
 */
fun aHomeserverData(
    homeserverUrl: String = AuthenticationConfig.MATRIX_ORG_URL,
): HomeserverData {
    return HomeserverData(homeserverUrl = homeserverUrl)
}
