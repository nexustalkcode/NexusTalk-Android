/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.TextFieldState
import io.element.android.features.invitepeople.api.InvitePeopleEvents
import io.element.android.features.invitepeople.api.InvitePeopleState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

/**
 * 默认邀请人员状态数据类
 *
 * 实现 InvitePeopleState 接口，提供邀请人员功能的默认状态实现。
 * 包含搜索邀请用户的完整状态信息，支持用户搜索、选择和批量邀请。
 */
data class DefaultInvitePeopleState(
    /**
     * 房间加载的异步状态
     *
     * 表示房间信息的加载状态，包括：未初始化、加载中、成功、失败。
     * 用于在页面中显示房间加载进度或错误信息。
     */
    val room: AsyncData<Unit>,

    /**
     * 当前是否可以发送邀请
     *
     * 当已选择至少一个用户且没有正在进行的邀请操作时为true。
     * 用于控制"发送邀请"按钮的启用/禁用状态。
     */
    override val canInvite: Boolean,

    /**
     * 搜索查询输入框的状态
     *
     * 管理搜索框中的文本内容，支持文本输入和清空操作。
     * 使用TextFieldState管理输入状态。
     */
    val searchQuery: TextFieldState,

    /**
     * 是否显示搜索加载动画
     *
     * 在执行搜索操作时显示加载指示器，提升用户体验。
     */
    val showSearchLoader: Boolean,

    /**
     * 搜索结果的状态
     *
     * 包含搜索返回的可邀请用户列表，支持初始状态、有结果、无结果等状态。
     * 使用SearchBarResultState管理搜索结果的各种状态。
     */
    val searchResults: SearchBarResultState<ImmutableList<InvitableUser>>,

    /**
     * 已选择的要邀请的用户列表
     *
     * 用户通过勾选选中的待邀请用户列表。
     * 用于批量发送邀请时获取邀请目标。
     */
    val selectedUsers: ImmutableList<MatrixUser>,

    /**
     * 搜索功能是否处于激活状态
     *
     * true表示搜索栏正处于活跃状态，显示搜索输入框和搜索结果。
     * false表示搜索栏未激活，显示推荐用户列表。
     */
    override val isSearchActive: Boolean,

    /**
     * 发送邀请操作的状态
     *
     * 跟踪批量发送邀请的异步操作状态，包括：未初始化、加载中、成功、失败。
     * 用于显示发送邀请过程中的加载指示器和结果反馈。
     */
    override val sendInvitesAction: AsyncAction<Unit>,

    /**
     * 推荐的可邀请用户列表
     *
     * 基于最近私聊用户生成的推荐列表，供用户快速选择邀请对象。
     * 不包含已经是房间成员的用户。
     */
    val suggestions: ImmutableList<InvitableUser>,

    /**
     * 事件处理函数
     *
     * 用于将用户交互事件传递给Presenter处理的函数。
     * UI层通过调用此函数来触发业务逻辑处理。
     */
    override val eventSink: (InvitePeopleEvents) -> Unit
) : InvitePeopleState
