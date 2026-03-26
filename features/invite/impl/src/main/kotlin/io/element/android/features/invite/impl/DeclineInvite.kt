/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl

import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.push.api.notifications.NotificationCleaner

/**
 * 拒绝邀请接口
 *
 * 定义了拒绝房间邀请的业务逻辑。
 * 允许用户拒绝邀请、可选封禁发送邀请的用户，以及可选举报房间。
 */
interface DeclineInvite {
    /**
     * 执行拒绝邀请操作
     *
     * @param roomId 房间 ID
     * @param blockUser 是否封禁发送邀请的用户
     * @param reportRoom 是否举报房间
     * @param reportReason 举报原因（如果举报房间）
     * @return Result<RoomId> 操作结果
     */
    suspend operator fun invoke(
        roomId: RoomId,
        blockUser: Boolean,
        reportRoom: Boolean,
        reportReason: String?
    ): Result<RoomId>

    /**
     * 拒绝邀请操作异常
     *
     * 定义了拒绝邀请操作可能出现的异常类型。
     */
    sealed class Exception : kotlin.Exception() {
        /** 房间未找到 */
        data object RoomNotFound : Exception()
        /** 拒绝邀请失败 */
        data object DeclineInviteFailed : Exception()
        /** 举报房间失败 */
        data object ReportRoomFailed : Exception()
        /** 封禁用户失败 */
        data object BlockUserFailed : Exception()
    }
}

@ContributesBinding(SessionScope::class)
/**
 * 默认拒绝邀请实现
 *
 * 实现了 DeclineInvite 接口，提供拒绝房间邀请的具体业务逻辑。
 * 执行离开房间、可选封禁用户、可选举报房间的操作。
 *
 * @property client Matrix 客户端
 * @property notificationCleaner 通知清理器
 * @property seenInvitesStore 已查看邀请存储
 */
class DefaultDeclineInvite(
    private val client: MatrixClient,
    private val notificationCleaner: NotificationCleaner,
    private val seenInvitesStore: SeenInvitesStore,
) : DeclineInvite {
    override suspend fun invoke(
        roomId: RoomId,
        blockUser: Boolean,
        reportRoom: Boolean,
        reportReason: String?
    ): Result<RoomId> {
        val room = client.getRoom(roomId) ?: return Result.failure(DeclineInvite.Exception.RoomNotFound)
        room.use {
            room.leave()
                .onFailure { return Result.failure(DeclineInvite.Exception.DeclineInviteFailed) }
                .onSuccess {
                    notificationCleaner.clearMembershipNotificationForRoom(
                        sessionId = client.sessionId,
                        roomId = roomId
                    )
                    seenInvitesStore.markAsUnSeen(roomId)
                }

            if (blockUser) {
                val userIdToBlock = room.info().inviter?.userId
                if (userIdToBlock != null) {
                    client
                        .ignoreUser(userIdToBlock)
                        .onFailure { return Result.failure(DeclineInvite.Exception.BlockUserFailed) }
                }
            }
            if (reportRoom) {
                room
                    .reportRoom(reportReason)
                    .onFailure { return Result.failure(DeclineInvite.Exception.ReportRoomFailed) }
            }
        }
        return Result.success(roomId)
    }
}
