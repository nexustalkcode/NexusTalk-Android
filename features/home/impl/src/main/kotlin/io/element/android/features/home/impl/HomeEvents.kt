/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.libraries.matrix.api.core.SessionId

/**
 * 首页事件密封接口
 *
 * 定义首页可能发生的用户交互事件。
 */
sealed interface HomeEvents {
    /**
     * 选择底部导航栏项目
     *
     * @property item 导航栏项目
     */
    data class SelectHomeNavigationBarItem(val item: HomeNavigationBarItem) : HomeEvents
    /**
     * 切换账户
     *
     * @property sessionId 会话 ID
     */
    data class SwitchToAccount(val sessionId: SessionId) : HomeEvents
}
