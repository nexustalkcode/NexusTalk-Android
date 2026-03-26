/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.editroomaddress

/**
 * 编辑房间地址事件密封接口
 *
 * 定义编辑房间地址页面中可能发生的用户交互事件。
 */
sealed interface EditRoomAddressEvents {
    /** 保存房间地址 */
    data object Save : EditRoomAddressEvents

    /** 关闭错误提示 */
    data object DismissError : EditRoomAddressEvents

    /**
     * 房间地址变更事件
     * @property roomAddress 新的房间地址
     */
    data class RoomAddressChanged(val roomAddress: String) : EditRoomAddressEvents
}
