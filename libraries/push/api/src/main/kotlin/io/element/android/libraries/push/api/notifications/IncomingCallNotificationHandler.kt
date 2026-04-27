/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.api.notifications

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 来电通知处理入口。
 *
 * push 层只需要把“收到一通响铃来电”这件事交给上层处理，不应该反向依赖 call feature 的具体入口类型。
 * 这里抽出最小契约，用来承接 push 对来电页面/状态管理的触发需求。
 */
interface IncomingCallNotificationHandler {
    suspend fun handleIncomingCall(
        sessionId: SessionId,
        roomId: RoomId,
        eventId: EventId,
        senderId: UserId,
        roomName: String?,
        senderName: String?,
        avatarUrl: String?,
        timestamp: Long,
        expirationTimestamp: Long,
        notificationChannelId: String,
        textContent: String?,
        isDm: Boolean = false,
    )
}
