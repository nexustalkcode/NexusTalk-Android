/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.call.impl.utils.IntentProvider
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId
import timber.log.Timber

private const val incomingCallTraceTag = "IncomingCallTrace"

/**
 * Element Call 入口点默认实现
 *
 * 实现 ElementCallEntryPoint 接口，提供通话功能的入口逻辑。
 * 负责启动通话和处理来电通知。
 *
 * @param context Android 上下文，用于启动 Activity 和获取系统服务
 * @param activeCallManager 活动通话管理器，用于管理通话状态
 *
 * @see ElementCallEntryPoint 通话入口点接口
 * @see CallType 通话类型
 * @see IntentProvider 意图提供者
 */
@ContributesBinding(AppScope::class)
class DefaultElementCallEntryPoint(
    @ApplicationContext private val context: Context,
    private val activeCallManager: ActiveCallManager,
) : ElementCallEntryPoint {
    companion object {
        /** 传递给 Activity 的通话类型 extra 键名 */
        const val EXTRA_CALL_TYPE = "EXTRA_CALL_TYPE"
        /** 请求码，用于 Activity 结果回调 */
        const val REQUEST_CODE = 2255
    }

    /**
     * 启动指定类型的通话
     *
     * @param callType 要启动的通话类型，可以是外部 URL 通话或房间内通话
     */
    override fun startCall(callType: CallType) {
        context.startActivity(IntentProvider.createIntent(context, callType))
    }

    /**
     * 处理来电通知
     *
     * 当收到来电时，创建一个通话通知数据对象，并注册到活动通话管理器中。
     * 管理系统会显示来电通知，用户可以选择接听或拒绝。
     *
     * @param callType 通话类型（房间内通话）
     * @param eventId 发起通话的事件 ID
     * @param senderId 发起通话的用户 ID
     * @param roomName 房间名称（可选）
     * @param senderName 发起者显示名称（可选）
     * @param avatarUrl 房间或用户头像 URL（可选）
     * @param timestamp 事件的 Unix 时间戳
     * @param expirationTimestamp 通话停止响铃的过期时间戳
     * @param notificationChannelId 通话通知的通知通道 ID
     * @param textContent 通知的文本内容，如果为 null 则使用系统默认内容
     */
    override suspend fun handleIncomingCall(
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
        isDm: Boolean,
    ) {
        /**
         * 自己发起的通话不应该再回流成“来电”。
         * 这里在统一入口做兜底过滤，避免前台 timeline 观察和 push 链路分别重复踩到同一个误判。
         */
        if (senderId == callType.sessionId) {
            Timber.tag(incomingCallTraceTag).i(
                "DefaultElementCallEntryPoint ignored self-originated call sessionId=%s roomId=%s eventId=%s senderId=%s",
                callType.sessionId,
                callType.roomId,
                eventId,
                senderId,
            )
            return
        }
        val incomingCallNotificationData = CallNotificationData(
            sessionId = callType.sessionId,
            roomId = callType.roomId,
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            isDm = isDm,
            senderName = senderName,
            avatarUrl = avatarUrl,
            timestamp = timestamp,
            expirationTimestamp = expirationTimestamp,
            notificationChannelId = notificationChannelId,
            textContent = textContent,
        )
        Timber.tag(incomingCallTraceTag).w(
            "DefaultElementCallEntryPoint registering incoming call sessionId=%s roomId=%s eventId=%s senderId=%s",
            callType.sessionId,
            callType.roomId,
            eventId,
            senderId,
        )
        activeCallManager.registerIncomingCall(notificationData = incomingCallNotificationData)
        Timber.tag(incomingCallTraceTag).w(
            "DefaultElementCallEntryPoint registered incoming call eventId=%s",
            eventId,
        )
    }
}
