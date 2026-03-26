/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api

import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.coroutines.flow.Flow

/**
 * 已查看邀请存储接口
 *
 * 用于管理用户已查看的房间邀请记录。
 * 追踪哪些邀请已被用户查看，以便显示未读邀请数量。
 */
interface SeenInvitesStore {
    /**
     * 获取已查看邀请的房间 ID 列表
     *
     * 返回一个 Flow，持续 emit 已查看邀请的房间 ID 集合。
     *
     * @return Flow<Set<RoomId>> 已查看邀请的房间 ID 集合
     */
    fun seenRoomIds(): Flow<Set<RoomId>>

    /**
     * 将邀请标记为已查看
     *
     * 当向用户展示邀请详情时调用此方法。
     *
     * @param roomId 要标记为已查看的邀请房间 ID
     */
    suspend fun markAsSeen(roomId: RoomId)

    /**
     * 将邀请标记为未查看
     *
     * 当邀请被接受或拒绝时调用此方法。
     *
     * @param roomId 要标记为未查看的邀请房间 ID
     */
    suspend fun markAsUnSeen(roomId: RoomId)

    /**
     * 清空存储
     *
     * 删除所有已查看邀请的记录。
     */
    suspend fun clear()
}
