/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api

import android.os.Parcelable
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.room.preview.RoomPreviewInfo
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import kotlinx.parcelize.Parcelize

/**
 * 邀请数据类
 *
 * 表示房间邀请的详细信息，包含房间 ID、房间名称和是否为直接消息。
 *
 * @property roomId 房间 ID
 * @property roomName 房间名称
 * @property isDm 是否为直接消息
 */
@Parcelize
data class InviteData(
    val roomId: RoomId,
    val roomName: String,
    val isDm: Boolean,
) : Parcelable

/**
 * 将房间预览信息转换为邀请数据
 *
 * @return InviteData 邀请数据
 */
fun RoomPreviewInfo.toInviteData(): InviteData {
    return InviteData(
        roomId = roomId,
        roomName = name ?: roomId.value,
        isDm = false,
    )
}

/**
 * 将房间信息转换为邀请数据
 *
 * @return InviteData 邀请数据
 */
fun RoomInfo.toInviteData(): InviteData {
    return InviteData(
        roomId = id,
        roomName = name ?: id.value,
        isDm = isDm,
    )
}

/**
 * 将空间房间转换为邀请数据
 *
 * @return InviteData 邀请数据
 */
fun SpaceRoom.toInviteData(): InviteData {
    return InviteData(
        roomId = roomId,
        roomName = displayName,
        isDm = false,
    )
}
