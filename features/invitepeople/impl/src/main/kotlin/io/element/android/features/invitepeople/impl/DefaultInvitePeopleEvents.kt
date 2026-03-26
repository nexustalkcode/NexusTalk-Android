/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import io.element.android.features.invitepeople.api.InvitePeopleEvents
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 默认邀请人员事件实现
 *
 * 密封接口，继承自InvitePeopleEvents，提供邀请人员功能的默认事件实现。
 * 包含用户切换选择、搜索状态变更等事件。
 *
 * @see InvitePeopleEvents 事件接口定义
 */
sealed interface DefaultInvitePeopleEvents : InvitePeopleEvents {
    /**
     * 切换用户选择状态事件
     *
     * 当用户点击选中/取消选中某个可邀请用户时触发。
     * 支持在推荐列表和搜索结果中进行用户选择。
     *
     * @property user 被切换选择状态的Matrix用户对象
     */
    data class ToggleUser(val user: MatrixUser) : DefaultInvitePeopleEvents

    /**
     * 搜索激活状态变更事件
     *
     * 当搜索栏的激活状态发生变化时触发。
     * 激活搜索时会显示搜索输入框，关闭时会清空搜索内容。
     *
     * @property active 搜索栏的新激活状态，true表示激活，false表示关闭
     */
    data class OnSearchActiveChanged(val active: Boolean) : DefaultInvitePeopleEvents
}
