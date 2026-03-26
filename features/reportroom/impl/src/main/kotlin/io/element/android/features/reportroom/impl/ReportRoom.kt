/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 举报房间功能的核心接口
 *
 * 定义了举报房间的操作，包括报告房间和离开房间的功能
 */
interface ReportRoom {
    /**
     * 执行举报房间操作
     *
     * 可以选择是否报告房间以及是否离开房间
     *
     * @param roomId 要举报的房间ID
     * @param shouldReport 是否报告此房间
     * @param reason 举报原因
     * @param shouldLeave 是否在举报后离开此房间
     * @return 返回操作结果，成功返回 [Result.success]，失败返回 [Result.failure]
     */
    suspend operator fun invoke(
        roomId: RoomId,
        shouldReport: Boolean,
        reason: String,
        shouldLeave: Boolean,
    ): Result<Unit>

    /**
     * 举报房间操作可能抛出的异常类型
     *
     * 包含三种异常情况：房间未找到、离开房间失败、举报房间失败
     */
    sealed class Exception : kotlin.Exception() {
        /**
         * 房间未找到异常
         * 当指定的房间ID不存在时抛出
         */
        data object RoomNotFound : Exception()

        /**
         * 离开房间失败异常
         * 当尝试离开房间操作失败时抛出
         */
        data object LeftRoomFailed : Exception()

        /**
         * 举报房间失败异常
         * 当举报房间操作失败时抛出
         */
        data object ReportRoomFailed : Exception()
    }
}

/**
 * [ReportRoom] 接口的默认实现类
 *
 * 使用 Matrix 客户端执行实际的举报和离开房间操作
 * 绑定到 SessionScope 作用域，与用户会话生命周期一致
 *
 * @param client Matrix 客户端实例，用于与 Matrix 服务器通信
 */
@ContributesBinding(SessionScope::class)
class DefaultReportRoom(
    private val client: MatrixClient,
) : ReportRoom {
    /**
     * 执行举报房间操作
     *
     * 根据参数执行举报房间和/或离开房间的操作
     *
     * @param roomId 要举报的房间ID
     * @param shouldReport 是否报告此房间
     * @param reason 举报原因，如果为空字符串则不会发送原因
     * @param shouldLeave 是否在举报后离开此房间
     * @return 返回操作结果，成功返回 [Result.success(Unit)]，失败返回对应的 [ReportRoom.Exception]
     */
    override suspend operator fun invoke(
        roomId: RoomId,
        shouldReport: Boolean,
        reason: String,
        shouldLeave: Boolean
    ): Result<Unit> {
        // 获取房间实例，如果不存在则返回失败
        val room = client.getRoom(roomId)
            ?: return Result.failure(ReportRoom.Exception.RoomNotFound)

        // 如果需要举报房间
        if (shouldReport) {
            room
                .reportRoom(reason.takeIf { it.isNotBlank() })
                .onFailure {
                    return Result.failure(ReportRoom.Exception.ReportRoomFailed)
                }
        }
        // 如果需要离开房间
        if (shouldLeave) {
            room
                .leave()
                .onFailure {
                    return Result.failure(ReportRoom.Exception.LeftRoomFailed)
                }
        }
        return Result.success(Unit)
    }
}
