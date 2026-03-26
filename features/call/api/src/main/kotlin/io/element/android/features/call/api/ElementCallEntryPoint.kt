/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.api

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 通话功能入口点接口
 *
 * 定义了 Element 通话功能的入口接口，负责启动和处理通话。
 */
interface ElementCallEntryPoint {
    /**
     * 启动指定类型的通话
     *
     * @param callType 要启动的通话类型
     */
    fun startCall(callType: CallType)

    /**
     * 处理来电
     *
     * @param callType 通话类型
     * @param eventId 发起通话的事件 ID
     * @param senderId 发起通话的用户 ID
     * @param roomName 房间名称
     * @param senderName 发起者名称
     * @param avatarUrl 房间或直接消息的头像 URL
     * @param timestamp 事件的 Unix 时间戳
     * @param expirationTimestamp 通话停止响铃的过期时间戳
     * @param notificationChannelId 通话通知的通知通道 ID
     * @param textContent 通知的文本内容，如果为 null 则使用系统默认内容
     */
    suspend fun handleIncomingCall(
        callType: CallType.RoomCall,
        eventId: EventId,
        senderId: UserId,
        roomName: String?,
        senderName: String?,
        avatarUrl: String?,
        timestamp: Long,
        expirationTimestamp: Long,
        notificationChannelId: String,
        textContent: String?,
    )
}
