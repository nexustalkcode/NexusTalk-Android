/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.api

import io.element.android.libraries.architecture.AsyncAction

/**
 * 邀请人员页面状态接口
 *
 * 定义邀请人员功能所需的页面状态接口。
 * 包含搜索、选择用户和发送邀请所需的所有状态信息。
 */
interface InvitePeopleState {
    /**
     * 是否可以发送邀请
     *
     * 当已选择用户且没有正在进行的邀请操作时为true。
     * 用于控制"发送邀请"按钮的启用/禁用状态。
     */
    val canInvite: Boolean

    /**
     * 搜索是否处于激活状态
     *
     * true表示搜索栏正处于活跃状态，用户可以输入搜索内容。
     * false表示搜索栏未激活，显示推荐用户列表。
     */
    val isSearchActive: Boolean

    /**
     * 发送邀请操作的状态
     *
     * 跟踪邀请发送的异步操作状态，包括：未初始化、加载中、成功、失败等。
     * 用于显示发送邀请过程中的加载指示器和错误信息。
     */
    val sendInvitesAction: AsyncAction<Unit>

    /**
     * 事件处理函数
     *
     * 用于将用户交互事件传递给Presenter处理的函数。
     * UI层通过调用此函数来触发业务逻辑处理。
     */
    val eventSink: (InvitePeopleEvents) -> Unit
}
