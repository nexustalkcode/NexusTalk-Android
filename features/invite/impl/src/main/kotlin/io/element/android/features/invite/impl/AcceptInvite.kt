/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl

import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.JoinedRoom
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.libraries.core.extensions.mapFailure
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.exception.ClientException
import io.element.android.libraries.matrix.api.exception.ErrorKind
import io.element.android.libraries.matrix.api.room.join.JoinRoom
import io.element.android.libraries.push.api.notifications.NotificationCleaner

/**
 * 接受邀请接口
 *
 * 定义了接受房间邀请的业务逻辑。
 * 允许用户接受邀请并加入房间。
 */
interface AcceptInvite {
    /**
     * 执行接受邀请操作
     *
     * @param roomId 房间 ID
     * @return Result<RoomId> 操作结果
     */
    suspend operator fun invoke(roomId: RoomId): Result<RoomId>

    /**
     * 接受邀请操作异常
     *
     * 定义了接受邀请操作可能出现的异常类型。
     */
    sealed class Failures : Exception() {
        /** 无效邀请 */
        data object InvalidInvite : Failures()
    }
}

@ContributesBinding(SessionScope::class)
/**
 * 默认接受邀请实现
 *
 * 实现了 AcceptInvite 接口，提供接受房间邀请的具体业务逻辑。
 * 执行加入房间并清理相关通知的操作。
 *
 * @property client Matrix 客户端
 * @property joinRoom 加入房间服务
 * @property notificationCleaner 通知清理器
 * @property seenInvitesStore 已查看邀请存储
 */
class DefaultAcceptInvite(
    private val client: MatrixClient,
    private val joinRoom: JoinRoom,
    private val notificationCleaner: NotificationCleaner,
    private val seenInvitesStore: SeenInvitesStore,
) : AcceptInvite {
    override suspend fun invoke(roomId: RoomId): Result<RoomId> {
        return joinRoom(
            roomIdOrAlias = roomId.toRoomIdOrAlias(),
            serverNames = emptyList(),
            trigger = JoinedRoom.Trigger.Invite,
        ).onSuccess {
            notificationCleaner.clearMembershipNotificationForRoom(client.sessionId, roomId)
            seenInvitesStore.markAsUnSeen(roomId)
        }.mapFailure {
            if (it is ClientException.MatrixApi) {
                when (it.kind) {
                    ErrorKind.Unknown -> AcceptInvite.Failures.InvalidInvite
                    else -> it
                }
            } else {
                it
            }
        }.map { roomId }
    }
}
