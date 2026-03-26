/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.api.direct

import io.element.android.libraries.architecture.AsyncAction

/**
 * 直接退出登录状态数据类
 *
 * 表示直接退出登录界面的状态，包含是否可以执行直接退出以及退出操作的异步状态。
 *
 * @property canDoDirectSignOut 是否可以执行直接退出登录
 * @property logoutAction 退出登录操作的异步状态
 * @property eventSink 事件处理函数
 */
data class DirectLogoutState(
    val canDoDirectSignOut: Boolean,
    val logoutAction: AsyncAction<Unit>,
    val eventSink: (DirectLogoutEvents) -> Unit,
)
