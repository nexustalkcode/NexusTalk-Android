/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.createaccount

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.SessionId

/**
 * 创建账号页面展示状态。
 *
 * @property url 当前注册页面 URL。
 * @property pageProgress WebView 加载进度，范围 0..100。
 * @property createAction 当前导入新会话的异步状态。
 * @property isDebugBuild 是否为可调试构建，用于决定注入调试能力。
 * @property eventSink 页面事件分发函数。
 */
data class CreateAccountState(
    val url: String,
    val pageProgress: Int,
    val createAction: AsyncAction<SessionId>,
    val isDebugBuild: Boolean,
    val eventSink: (CreateAccountEvents) -> Unit
)
