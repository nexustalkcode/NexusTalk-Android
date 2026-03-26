/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.api.direct

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 直接退出登录状态提供者
 *
 * 用于在 Compose 预览中提供各种 DirectLogoutState 的示例数据。
 * 继承自 PreviewParameterProvider，用于支持多状态预览。
 */
open class DirectLogoutStateProvider : PreviewParameterProvider<DirectLogoutState> {
    /**
     * 提供预览状态序列
     * 包含各种可能的退出登录状态：未操作、确认中、加载中、失败、成功
     */
    override val values: Sequence<DirectLogoutState>
        get() = sequenceOf(
            aDirectLogoutState(),
            aDirectLogoutState(logoutAction = AsyncAction.ConfirmingNoParams),
            aDirectLogoutState(logoutAction = AsyncAction.Loading),
            aDirectLogoutState(logoutAction = AsyncAction.Failure(Exception("Error"))),
            aDirectLogoutState(logoutAction = AsyncAction.Success(Unit)),
        )
}

/**
 * 创建 DirectLogoutState 测试数据的辅助函数
 *
 * @param canDoDirectSignOut 是否可以执行直接退出登录
 * @param logoutAction 退出登录操作的异步状态
 * @param eventSink 事件处理函数
 * @return DirectLogoutState 实例
 */
fun aDirectLogoutState(
    canDoDirectSignOut: Boolean = true,
    logoutAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (DirectLogoutEvents) -> Unit = {},
) = DirectLogoutState(
    canDoDirectSignOut = canDoDirectSignOut,
    logoutAction = logoutAction,
    eventSink = eventSink,
)
