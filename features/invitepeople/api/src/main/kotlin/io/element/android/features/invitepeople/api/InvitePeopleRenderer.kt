/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 邀请人员渲染器接口
 *
 * 定义邀请人员功能的UI渲染接口。
 * 负责根据状态数据渲染邀请人员页面的UI。
 *
 * @see InvitePeopleState 页面状态定义
 */
interface InvitePeopleRenderer {
    /**
     * 渲染邀请人员页面
     *
     * 根据传入的状态数据渲染邀请人员界面的Composable函数。
     *
     * @param state 邀请人员页面的当前状态
     * @param modifier Compose修饰符，用于控制布局和样式
     */
    @Composable
    fun Render(
        state: InvitePeopleState,
        modifier: Modifier,
    )
}
