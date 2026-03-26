/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.api.direct

import androidx.compose.runtime.Composable

/**
 * 直接退出登录视图接口
 *
 * 定义了直接退出登录界面的渲染函数式接口，
 * 用于在不同模块中提供统一的退出登录 UI 渲染方式。
 */
fun interface DirectLogoutView {
    /**
     * 渲染直接退出登录界面
     * @param state 直接退出登录的当前状态
     */
    @Composable
    fun Render(state: DirectLogoutState)
}
