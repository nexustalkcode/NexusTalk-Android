/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.joinbyaddress

/**
 * “按地址加入房间”页面可能产生的用户事件。
 */
sealed interface JoinRoomByAddressEvent {
    /** 关闭当前页面。 */
    data object Dismiss : JoinRoomByAddressEvent

    /** 确认继续，尝试加入解析出的房间。 */
    data object Continue : JoinRoomByAddressEvent

    /** 更新用户输入的房间地址。 */
    data class UpdateAddress(val address: String) : JoinRoomByAddressEvent
}
