/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.notifications

import android.os.Parcelable
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import kotlinx.parcelize.Parcelize

/**
 * 通话通知数据。
 *
 * 这份数据会在通知、Activity 和来电状态管理之间传递，因此必须同时保留房间信息和发起人信息：
 * 私聊来电展示发起人，群房间来电展示房间。
 */
@Parcelize
data class CallNotificationData(
    /** 会话 ID，表示当前用户。 */
    val sessionId: SessionId,
    /** 房间 ID，表示通话所在的房间。 */
    val roomId: RoomId,
    /** 通话事件 ID。 */
    val eventId: EventId,
    /** 发起通话的用户 ID。 */
    val senderId: UserId,
    /** 房间名称，可为空。 */
    val roomName: String?,
    /** 发起者显示名称，可为空。 */
    val senderName: String?,
    /** 房间或用户头像 URL，可为空。 */
    val avatarUrl: String?,
    /** 通知通道 ID。 */
    val notificationChannelId: String,
    /** 事件时间戳，Unix 毫秒。 */
    val timestamp: Long,
    /** 通知文本内容，可为空。 */
    val textContent: String?,
    /** 过期时间戳，Unix 毫秒。 */
    val expirationTimestamp: Long,
    /** 是否为一对一私聊房间。 */
    val isDm: Boolean = false,
) : Parcelable

fun CallNotificationData.hasSameRingingIdentityAs(other: CallNotificationData): Boolean {
    return sessionId == other.sessionId &&
        roomId == other.roomId &&
        senderId == other.senderId
}
