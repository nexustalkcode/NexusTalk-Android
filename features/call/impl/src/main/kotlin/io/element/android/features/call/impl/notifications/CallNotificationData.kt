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
 * 通话通知数据类
 *
 * 包含来电通知所需的全部信息，用于在通知、Activity 和系统组件之间传递通话数据。
 * 实现 Parcelable 接口以便在 Intent 中传递。
 *
 * @property sessionId 会话 ID，表示当前用户
 * @property roomId 房间 ID，表示通话所在的房间
 * @property eventId 通话事件 ID
 * @property senderId 发起通话的用户 ID
 * @property roomName 房间名称（可选）
 * @property senderName 发起者显示名称（可选）
 * @property avatarUrl 房间或用户头像 URL（可选）
 * @property notificationChannelId 通知通道 ID，用于区分不同类型的通知
 * @property timestamp 事件时间戳（Unix 毫秒）
 * @property textContent 通知文本内容（可选）
 * @property expirationTimestamp 过期时间戳（Unix 毫秒），超过此时间后通话通知将自动消失
 */
@Parcelize
data class CallNotificationData(
    /** 会话 ID，表示当前用户 */
    val sessionId: SessionId,
    /** 房间 ID，表示通话所在的房间 */
    val roomId: RoomId,
    /** 通话事件 ID */
    val eventId: EventId,
    /** 发起通话的用户 ID */
    val senderId: UserId,
    /** 房间名称（可选） */
    val roomName: String?,
    /** 发起者显示名称（可选） */
    val senderName: String?,
    /** 房间或用户头像 URL（可选） */
    val avatarUrl: String?,
    /** 通知通道 ID */
    val notificationChannelId: String,
    /** 事件时间戳（Unix 毫秒） */
    val timestamp: Long,
    /** 通知文本内容（可选） */
    val textContent: String?,
    /** 过期时间戳（Unix 毫秒），超过此时间后通话通知将自动消失 */
    val expirationTimestamp: Long,
) : Parcelable
