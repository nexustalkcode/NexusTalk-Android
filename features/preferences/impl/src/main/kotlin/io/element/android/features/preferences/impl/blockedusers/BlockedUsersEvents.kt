/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.blockedusers

import io.element.android.libraries.matrix.api.core.UserId

/**
 * 被屏蔽用户页面事件密封接口
 *
 * 定义被屏蔽用户页面中可能发生的各种用户交互事件。
 */
sealed interface BlockedUsersEvents {
    /** 解封用户 */
    data class Unblock(val userId: UserId) : BlockedUsersEvents
    /** 确认解封 */
    data object ConfirmUnblock : BlockedUsersEvents
    /** 取消操作 */
    data object Cancel : BlockedUsersEvents
}
