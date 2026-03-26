/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.manageauthorizedspaces

import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 管理授权空间事件密封接口
 *
 * 定义管理授权空间页面中可能发生的用户交互事件。
 *
 * @see ManageAuthorizedSpacesState 页面状态
 */
sealed interface ManageAuthorizedSpacesEvent {
    /** 取消操作，返回上一页 */
    data object Cancel : ManageAuthorizedSpacesEvent

    /** 完成选择，确认授权空间 */
    data object Done : ManageAuthorizedSpacesEvent

    /**
     * 切换空间选择状态
     * @property roomId 要切换选择状态的空间 ID
     */
    data class ToggleSpace(val roomId: RoomId) : ManageAuthorizedSpacesEvent
}
