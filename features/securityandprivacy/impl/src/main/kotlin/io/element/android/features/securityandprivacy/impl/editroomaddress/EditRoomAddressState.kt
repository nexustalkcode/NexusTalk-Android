/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.editroomaddress

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.ui.room.address.RoomAddressValidity

/**
 * 编辑房间地址状态数据类
 *
 * @property homeserverName 服务器名称
 * @property roomAddress 房间地址
 * @property roomAddressValidity 房间地址有效性
 * @property saveAction 保存操作的异步状态
 * @property eventSink 事件处理函数
 * @property canBeSaved 是否可以保存
 */
data class EditRoomAddressState(
    val homeserverName: String,
    val roomAddress: String,
    val roomAddressValidity: RoomAddressValidity,
    val saveAction: AsyncAction<Unit>,
    val eventSink: (EditRoomAddressEvents) -> Unit
) {
    val canBeSaved = roomAddressValidity == RoomAddressValidity.Valid
}
