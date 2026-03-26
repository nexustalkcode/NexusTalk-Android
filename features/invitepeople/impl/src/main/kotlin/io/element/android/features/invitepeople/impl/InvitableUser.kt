/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 可邀请用户数据类
 *
 * 表示一个可以被邀请到房间的用户的信息。
 * 包含用户基本信息以及该用户当前在房间中的状态。
 *
 * @property matrixUser Matrix用户对象，包含用户ID、显示名等基本信息
 * @property isSelected 用户是否已被选中用于邀请
 * @property isAlreadyJoined 用户是否已经是房间成员
 * @property isAlreadyInvited 用户是否已经收到邀请
 * @property isUnresolved 用户是否为未解析状态（如通过Matrix ID搜索但未确认存在）
 */
data class InvitableUser(
    val matrixUser: MatrixUser,
    val isSelected: Boolean,
    val isAlreadyJoined: Boolean,
    val isAlreadyInvited: Boolean,
    val isUnresolved: Boolean,
)
