/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
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
 * 取消敲门请求接口
 *
 * 定义了取消之前发送的敲门请求的操作。
 * 当用户已经向房间发送了敲门请求但想撤回时使用。
 */
interface CancelKnockRoom {
    /**
     * 执行取消敲门请求操作
     *
     * @param roomId 房间 ID
     * @return Result<Unit> 操作结果，成功时返回 Unit，失败时返回错误
     */
    suspend operator fun invoke(roomId: RoomId): Result<Unit>
}

@ContributesBinding(SessionScope::class)
/**
 * 默认的取消敲门请求实现类
 *
 * 使用 MatrixClient 实现取消敲门请求功能。
 * 通过调用 leave 方法离开房间（这会同时取消敲门请求）。
 */
class DefaultCancelKnockRoom(private val client: MatrixClient) : CancelKnockRoom {
    /**
     * 执行取消敲门请求操作
     *
     * @param roomId 房间 ID
     * @return Result<Unit> 操作结果
     */
    override suspend fun invoke(roomId: RoomId): Result<Unit> {
        return client
            .getRoom(roomId)
            ?.use { it.leave() }
            ?: Result.failure(IllegalStateException("No pending room found"))
    }
}
