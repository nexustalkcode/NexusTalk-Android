/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding.classic

import io.element.android.libraries.architecture.AsyncAction

/**
 * “使用 Element Classic 登录”页面展示状态。
 *
 * @property canLoginWithClassic 当前是否允许执行导入登录。
 * @property loginWithClassicAction 当前登录动作异步状态。
 * @property eventSink 页面事件分发函数。
 */
data class LoginWithClassicState(
    val canLoginWithClassic: Boolean,
    val loginWithClassicAction: AsyncAction<Unit>,
    val eventSink: (LoginWithClassicEvent) -> Unit,
)
