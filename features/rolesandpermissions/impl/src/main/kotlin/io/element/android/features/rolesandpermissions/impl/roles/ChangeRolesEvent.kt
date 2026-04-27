/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.roles

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 修改成员角色页面可能触发的用户事件。
 */
sealed interface ChangeRolesEvent {
    /** 切换搜索模式。 */
    data object ToggleSearchActive : ChangeRolesEvent
    /** 切换某个成员的选中状态。 */
    data class UserSelectionToggled(val matrixUser: MatrixUser) : ChangeRolesEvent
    /** 保存角色修改。 */
    data object Save : ChangeRolesEvent
    /** 退出当前页面。 */
    data object Exit : ChangeRolesEvent
    /** 关闭当前对话框。 */
    data object CloseDialog : ChangeRolesEvent
}
