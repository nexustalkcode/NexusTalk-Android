/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.searchaccountprovider

import io.element.android.features.login.impl.changeserver.ChangeServerState
import io.element.android.features.login.impl.resolver.HomeserverData
import io.element.android.libraries.architecture.AsyncData

/**
 * 搜索账户提供商状态数据类
 *
 * @property userInput 用户输入的搜索文本
 * @property userInputResult 搜索结果的异步状态
 * @property changeServerState 服务器切换状态
 * @property eventSink 事件处理函数
 */
data class SearchAccountProviderState(
    val userInput: String,
    val userInputResult: AsyncData<List<HomeserverData>>,
    val changeServerState: ChangeServerState,
    val eventSink: (SearchAccountProviderEvents) -> Unit
)
