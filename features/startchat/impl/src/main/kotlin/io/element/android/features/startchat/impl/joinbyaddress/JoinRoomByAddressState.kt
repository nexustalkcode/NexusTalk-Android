/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.joinbyaddress

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

/**
 * “按地址加入房间”页面的展示状态。
 *
 * @property address 当前输入的房间地址。
 * @property addressState 当前地址的解析/校验状态。
 * @property eventSink 页面事件分发函数。
 */
data class JoinRoomByAddressState(
    val address: String,
    val addressState: RoomAddressState,
    val eventSink: (JoinRoomByAddressEvent) -> Unit
)

@Immutable
/**
 * 房间地址解析状态。
 */
sealed interface RoomAddressState {
    /** 还未开始校验或需要隐藏校验结果。 */
    data object Unknown : RoomAddressState

    /** 输入地址格式非法。 */
    data object Invalid : RoomAddressState

    /** 正在解析房间地址。 */
    data object Resolving : RoomAddressState

    /** 地址格式合法，但没有找到对应房间。 */
    data object RoomNotFound : RoomAddressState

    /** 成功解析到了可加入的房间。 */
    data class RoomFound(val resolved: ResolvedRoomAlias) : RoomAddressState
}
