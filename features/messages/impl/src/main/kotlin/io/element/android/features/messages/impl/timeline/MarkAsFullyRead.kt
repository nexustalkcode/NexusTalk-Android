/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 标记为已完全阅读接口
 *
 * 定义将房间标记为已完全阅读的操作。
 * 用于在用户滚动到特定事件时更新房间的已读状态。
 *
 * @see RoomId 房间ID
 * @see EventId 事件ID
 */
interface MarkAsFullyRead {
    /**
     * 标记房间为已完全阅读
     *
     * @param roomId 房间ID
     * @param eventId 事件ID（用于确定已读位置）
     * @return 结果，表示操作是否成功
     */
    suspend operator fun invoke(roomId: RoomId, eventId: EventId): Result<Unit>
}

/**
 * 默认标记为完全阅读实现
 *
 * 使用 MatrixClient 将房间标记为已完全阅读。
 * 实现了 MarkAsFullyRead 接口。
 *
 * @property matrixClient Matrix客户端
 * @property coroutineDispatchers 协程调度器
 */
@ContributesBinding(SessionScope::class)
class DefaultMarkAsFullyRead(
    private val matrixClient: MatrixClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : MarkAsFullyRead {
    override suspend fun invoke(roomId: RoomId, eventId: EventId): Result<Unit> = withContext(coroutineDispatchers.io) {
        matrixClient.markRoomAsFullyRead(roomId, eventId).onFailure {
            Timber.e(it, "Failed to mark room $roomId as fully read for event $eventId")
        }
    }
}
