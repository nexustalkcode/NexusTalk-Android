/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
package io.element.android.features.call.impl.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.app.Person
import dev.zacsweers.metro.Inject
import io.element.android.appconfig.ElementCallConfig
import io.element.android.features.call.api.CallType
import io.element.android.features.call.impl.R
import io.element.android.features.call.impl.receivers.DeclineCallBroadcastReceiver
import io.element.android.features.call.impl.ui.IncomingCallActivity
import io.element.android.features.call.impl.utils.IntentProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.utils.CommonDrawables
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.ui.media.ImageLoaderHolder
import io.element.android.libraries.push.api.notifications.NotificationBitmapLoader
import kotlin.time.Duration.Companion.seconds

/**
 * 来电通知创建器
 *
 * 负责创建和管理来电响铃通知。该通知会显示在通知栏中，
 * 包含来电者信息、接听和拒绝按钮，以及全屏 intent 以在来电时启动全屏界面。
 *
 * @param context Android 上下文
 * @param matrixClientProvider Matrix 客户端提供者，用于获取用户信息
 * @param imageLoaderHolder 图片加载器持有者，用于加载头像
 * @param notificationBitmapLoader 通知位图加载器，用于加载通知图标
 *
 * @see CallNotificationData 通话通知数据
 * @see Notification Android 通知
 */
@Inject
class RingingCallNotificationCreator(
    @ApplicationContext private val context: Context,
    private val matrixClientProvider: MatrixClientProvider,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val notificationBitmapLoader: NotificationBitmapLoader,
) {
    companion object {
        /** 拒绝操作 PendingIntent 的请求码 */
        const val DECLINE_REQUEST_CODE = 1

        /** 全屏 Intent 的请求码 */
        const val FULL_SCREEN_INTENT_REQUEST_CODE = 2
    }

    /**
     * 创建来电响铃通知
     *
     * 创建一个显示来电信息的通知，包含来电者头像、名称，以及接听和拒绝按钮。
     * 通知会在指定时间后自动过期。
     *
     * @param sessionId 会话 ID
     * @param roomId 房间 ID
     * @param eventId 通话事件 ID
     * @param senderId 发起通话的用户 ID
     * @param roomName 房间名称（可选）
     * @param senderDisplayName 发起者显示名称
     * @param roomAvatarUrl 房间头像 URL（可选）
     * @param notificationChannelId 通知通道 ID
     * @param timestamp 事件时间戳
     * @param expirationTimestamp 过期时间戳
     * @param textContent 通知文本内容（可选）
     * @return 创建的通知对象，如果创建失败则返回 null
     */
    suspend fun createNotification(
        sessionId: SessionId,
        roomId: RoomId,
        eventId: EventId,
        senderId: UserId,
        roomName: String?,
        senderDisplayName: String,
        roomAvatarUrl: String?,
        notificationChannelId: String,
        timestamp: Long,
        expirationTimestamp: Long,
        textContent: String?,
    ): Notification? {
        val matrixClient = matrixClientProvider.getOrRestore(sessionId).getOrNull() ?: return null
        val imageLoader = imageLoaderHolder.get(matrixClient)
        val userIcon = notificationBitmapLoader.getUserIcon(
            avatarData = AvatarData(
                id = roomId.value,
                name = roomName,
                url = roomAvatarUrl,
                size = AvatarSize.RoomDetailsHeader,
            ),
            imageLoader = imageLoader,
        )
        val avatarBitmap = notificationBitmapLoader.getRoomBitmap(
            avatarData = AvatarData(
                id = roomId.value,
                name = roomName ?: senderDisplayName,
                url = roomAvatarUrl,
                size = AvatarSize.RoomDetailsHeader,
            ),
            imageLoader = imageLoader,
        )

        val caller = Person.Builder()
            .setName(senderDisplayName)
            .setIcon(userIcon)
            .setImportant(true)
            .build()

        val answerIntent = IntentProvider.getPendingIntent(context, CallType.RoomCall(sessionId, roomId))
        val notificationData = CallNotificationData(
            sessionId = sessionId,
            roomId = roomId,
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            senderName = senderDisplayName,
            avatarUrl = roomAvatarUrl,
            notificationChannelId = notificationChannelId,
            timestamp = timestamp,
            textContent = textContent,
            expirationTimestamp = expirationTimestamp,
        )

        val declineIntent = PendingIntentCompat.getBroadcast(
            context,
            DECLINE_REQUEST_CODE,
            Intent(context, DeclineCallBroadcastReceiver::class.java).apply {
                putExtra(DeclineCallBroadcastReceiver.EXTRA_NOTIFICATION_DATA, notificationData)
            },
            PendingIntent.FLAG_CANCEL_CURRENT,
            false,
        )!!

        val fullScreenIntent = PendingIntentCompat.getActivity(
            context,
            FULL_SCREEN_INTENT_REQUEST_CODE,
            Intent(context, IncomingCallActivity::class.java).apply {
                putExtra(IncomingCallActivity.EXTRA_NOTIFICATION_DATA, notificationData)
            },
            PendingIntent.FLAG_CANCEL_CURRENT,
            false
        )!!
        val incomingCallText = context.getString(R.string.notification_incoming_call)
        val contentView = createIncomingCallRemoteViews(
            avatarBitmap = avatarBitmap,
            senderDisplayName = senderDisplayName,
            subtitle = incomingCallText,
            answerIntent = answerIntent,
            declineIntent = declineIntent,
            contentIntent = fullScreenIntent,
        )

        return NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(CommonDrawables.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .addPerson(caller)
            .setAutoCancel(true)
            .setWhen(timestamp)
            .setOngoing(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(senderDisplayName)
            .setContentText(incomingCallText)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setCustomHeadsUpContentView(contentView)
            .setCustomBigContentView(contentView)
            .setTimeoutAfter(ElementCallConfig.RINGING_CALL_DURATION_SECONDS.seconds.inWholeMilliseconds)
            .setContentIntent(answerIntent)
            .setDeleteIntent(declineIntent)
            .setFullScreenIntent(fullScreenIntent, true)
            .build()
            .apply {
                flags = flags.or(Notification.FLAG_INSISTENT)
            }
    }

    private fun createIncomingCallRemoteViews(
        avatarBitmap: android.graphics.Bitmap?,
        senderDisplayName: String,
        subtitle: String,
        answerIntent: PendingIntent,
        declineIntent: PendingIntent,
        contentIntent: PendingIntent,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_incoming_call_notification).apply {
            setTextViewText(R.id.incomingCallTitle, senderDisplayName)
            setTextViewText(R.id.incomingCallSubtitle, subtitle)
            avatarBitmap?.let {
                setImageViewBitmap(R.id.incomingCallAvatar, it)
            } ?: setImageViewResource(R.id.incomingCallAvatar, CommonDrawables.ic_notification)
            setOnClickPendingIntent(R.id.incomingCallRoot, contentIntent)
            setOnClickPendingIntent(R.id.incomingCallDeclineButton, declineIntent)
            setOnClickPendingIntent(R.id.incomingCallAnswerButton, answerIntent)
        }
    }
}
