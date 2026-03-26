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
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias

/**
 * 敲门请求接口
 *
 * 定义了向房间发送敲门请求的操作。
 * 敲门是 Matrix 协议中用于请求加入私有房间的机制。
 */
interface KnockRoom {
    /**
     * 执行敲门请求操作
     *
     * @param roomIdOrAlias 房间 ID 或别名
     * @param message 敲门消息，向房间管理员发送的请求消息
     * @param serverNames 服务器名称列表，用于房间发现
     * @return Result<Unit> 操作结果，成功时返回 Unit，失败时返回错误
     */
    suspend operator fun invoke(
        roomIdOrAlias: RoomIdOrAlias,
        message: String,
        serverNames: List<String>,
    ): Result<Unit>
}

@ContributesBinding(SessionScope::class)
/**
 * 默认的敲门请求实现类
 *
 * 使用 MatrixClient 实现敲门请求功能。
 * 通过调用 MatrixClient 的 knockRoom 方法向房间发送敲门请求。
 */
class DefaultKnockRoom(private val client: MatrixClient) : KnockRoom {
    /**
     * 执行敲门请求操作
     *
     * @param roomIdOrAlias 房间 ID 或别名
     * @param message 敲门消息
     * @param serverNames 服务器名称列表
     * @return Result<Unit> 操作结果
     */
    override suspend fun invoke(
        roomIdOrAlias: RoomIdOrAlias,
        message: String,
        serverNames: List<String>
    ): Result<Unit> {
        return client
            .knockRoom(roomIdOrAlias, message, serverNames)
            .map { }
    }
}
