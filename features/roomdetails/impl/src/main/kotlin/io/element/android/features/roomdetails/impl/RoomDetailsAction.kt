/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

/**
 * 房间详情操作密封接口
 *
 * 定义房间详情页面可能执行的操作。
 * 使用密封接口确保类型安全，只能创建预定义的子类型。
 */
sealed interface RoomDetailsAction {
    /**
     * 编辑房间详情
     *
     * 导航到房间编辑页面，可以修改房间名称、头像、主题等信息。
     */
    data object Edit : RoomDetailsAction

    /**
     * 添加房间主题
     *
     * 导航到房间编辑页面添加房间主题描述。
     */
    data object AddTopic : RoomDetailsAction
}
