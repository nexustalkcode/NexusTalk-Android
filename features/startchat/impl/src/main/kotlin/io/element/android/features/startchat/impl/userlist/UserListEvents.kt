/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.userlist

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 用户列表组件可能触发的用户事件。
 */
sealed interface UserListEvents {
    /** 把用户加入当前选择集合。 */
    data class AddToSelection(val matrixUser: MatrixUser) : UserListEvents
    /** 把用户从当前选择集合移除。 */
    data class RemoveFromSelection(val matrixUser: MatrixUser) : UserListEvents
    /** 切换搜索栏是否处于激活状态。 */
    data class OnSearchActiveChanged(val active: Boolean) : UserListEvents
}
