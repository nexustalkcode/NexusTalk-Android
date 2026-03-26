/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 房间列表配置 (Room List Configuration)
 *
 * 此对象包含房间列表（会话列表）界面相关的配置项。
 * 控制房间列表中显示的菜单选项和功能入口。
 */
object RoomListConfig {
    /** 是否在房间列表中显示"邀请"菜单项。启用后，用户可以从房间列表快速发起群聊邀请 */
    const val SHOW_INVITE_MENU_ITEM = false

    /** 是否在房间列表中显示"报告问题"菜单项。启用后，用户可以方便地报告房间相关的问题 */
    const val SHOW_REPORT_PROBLEM_MENU_ITEM = false

    /** 是否显示下拉菜单。当任一菜单项启用时，此值将为true，显示包含所有可用选项的下拉菜单 */
    const val HAS_DROP_DOWN_MENU = SHOW_INVITE_MENU_ITEM || SHOW_REPORT_PROBLEM_MENU_ITEM
}
