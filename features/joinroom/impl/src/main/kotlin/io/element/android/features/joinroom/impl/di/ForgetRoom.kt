/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl.di

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 忘记房间接口
 *
 * 定义了从当前会话中移除房间记录的操作。
 * 执行此操作后，房间将从聊天列表中移除，且无法再次加入（除非再次收到邀请或发现房间）。
 */
interface ForgetRoom {
    /**
     * 执行忘记房间操作
     *
     * @param roomId 房间 ID
     * @return Result<Unit> 操作结果，成功时返回 Unit，失败时返回错误
     */
    suspend operator fun invoke(roomId: RoomId): Result<Unit>
}

@ContributesBinding(SessionScope::class)
/**
 * 默认的忘记房间实现类
 *
 * 使用 MatrixClient 实现忘记房间功能。
 * 通过调用房间的 forget 方法来移除房间记录。
 */
class DefaultForgetRoom(private val client: MatrixClient) : ForgetRoom {
    /**
     * 执行忘记房间操作
     *
     * @param roomId 房间 ID
     * @return Result<Unit> 操作结果
     */
    override suspend fun invoke(roomId: RoomId): Result<Unit> {
        return client.getRoom(roomId)?.use { it.forget() }
            ?: Result.failure(IllegalStateException("Room not found"))
    }
}
