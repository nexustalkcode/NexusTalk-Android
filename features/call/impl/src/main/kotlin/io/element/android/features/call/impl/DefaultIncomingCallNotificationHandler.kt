/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.push.api.notifications.IncomingCallNotificationHandler

/**
 * push 层到 call 层的来电通知适配器。
 *
 * 这里把 push 的轻量库层契约转换回 call feature 里的 `CallType.RoomCall`，
 * 避免 `DefaultElementCallEntryPoint` 同时承担多个绑定接口而触发 Metro 绑定歧义。
 */
@ContributesBinding(AppScope::class)
class DefaultIncomingCallNotificationHandler(
    private val elementCallEntryPoint: ElementCallEntryPoint,
) : IncomingCallNotificationHandler {
    override suspend fun handleIncomingCall(
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
        isDm: Boolean,
    ) {
        elementCallEntryPoint.handleIncomingCall(
            callType = CallType.RoomCall(sessionId, roomId),
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            senderName = senderName,
            avatarUrl = avatarUrl,
            timestamp = timestamp,
            expirationTimestamp = expirationTimestamp,
            notificationChannelId = notificationChannelId,
            textContent = textContent,
            isDm = isDm,
        )
    }
}
