/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.appconfig.ElementCallConfig
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.exception.NotificationResolverException
import io.element.android.libraries.matrix.api.notification.NotificationContent
import io.element.android.libraries.matrix.api.notification.NotificationData
import io.element.android.libraries.matrix.api.notification.RtcNotificationType
import io.element.android.libraries.matrix.api.room.CurrentUserMembership
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.CallNotifyContent
import io.element.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.getAvatarUrl
import io.element.android.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import io.element.android.libraries.push.impl.notifications.CallNotificationEventResolver
import io.element.android.libraries.push.impl.notifications.channels.NotificationChannels
import io.element.android.libraries.push.impl.notifications.model.NotifiableRingingCallEvent
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.services.appnavstate.api.AppForegroundStateService
import io.element.android.services.toolbox.api.systemclock.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

private const val incomingCallTraceTag = "IncomingCallTrace"
private const val callNotifyTypeRing = "ring"
private const val callNotifyTypeNotification = "notification"
private const val callNotifyTypeNotify = "notify"

/**
 * 监听前台会话中的来电信号。
 *
 * 该接口用于在应用已经位于前台时补齐系统通知之外的来电入口，
 * 避免因为通知被折叠、过滤或延迟而错过房间通话的响铃状态。
 */
interface ForegroundIncomingCallObserver {
    /**
     * 启动监听流程。
     *
     * 实现类需要自行保证重复调用是幂等的，避免注册出多条并行观察链路。
     */
    fun start()
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
/**
 * 基于会话列表和房间时间线的前台来电观察器。
 *
 * 它会按「会话 -> 房间 -> 最新 `m.call.notify` 事件」的层级建立监听，
 * 并在前台确认来电后把结果转交给 [ElementCallEntryPoint] 统一展示 overlay 或全屏来电页。
 */
class DefaultForegroundIncomingCallObserver(
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val sessionStore: SessionStore,
    private val matrixClientProvider: MatrixClientProvider,
    private val callNotificationEventResolver: CallNotificationEventResolver,
    private val elementCallEntryPoint: ElementCallEntryPoint,
    private val notificationChannels: NotificationChannels,
    private val appForegroundStateService: AppForegroundStateService,
    private val systemClock: SystemClock,
) : ForegroundIncomingCallObserver {
    private val isStarted = AtomicBoolean(false)
    private val processedEventKeys = LinkedHashSet<String>()
    private val maxProcessedEventKeys = 128

    /**
     * 启动前台来电观察。
     *
     * 这里只负责拉起顶层协程；实际的会话和房间订阅逻辑放在后续挂起函数中处理。
     */
    override fun start() {
        if (!isStarted.compareAndSet(false, true)) {
            Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver.start ignored because it is already started")
            return
        }
        Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver.start")
        appCoroutineScope.launch {
            observeSessions()
        }
    }

    /**
     * 监听已登录会话集合的变化，并为每个会话维护独立的观察任务。
     *
     * 当会话新增时创建对应监听；当会话移除时取消对应任务，避免已登出账户继续占用资源。
     */
    private suspend fun observeSessions() {
        val sessionJobs = mutableMapOf<SessionId, Job>()
        sessionStore.sessionsFlow()
            .map { sessions -> sessions.map { SessionId(it.userId) }.toSet() }
            .distinctUntilChanged()
            .collectLatest { sessionIds ->
                Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver sessions=%s", sessionIds)
                val removedSessionIds = sessionJobs.keys - sessionIds
                removedSessionIds.forEach { sessionId ->
                    Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver stop session observer sessionId=%s", sessionId)
                    sessionJobs.remove(sessionId)?.cancelAndJoin()
                }
                val newSessionIds = sessionIds - sessionJobs.keys
                newSessionIds.forEach { sessionId ->
                    Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver start session observer sessionId=%s", sessionId)
                    sessionJobs[sessionId] = appCoroutineScope.launch {
                        observeSession(sessionId)
                    }
                }
            }
    }

    /**
     * 监听单个会话里当前存在活动通话的房间集合。
     *
     * @param sessionId 需要恢复并观察的会话标识。
     */
    private suspend fun observeSession(sessionId: SessionId) {
        val client = matrixClientProvider.getOrRestore(sessionId).getOrNull() ?: run {
            Timber.tag(incomingCallTraceTag).w("ForegroundIncomingCallObserver cannot restore Matrix client sessionId=%s", sessionId)
            return
        }
        val roomJobs = mutableMapOf<RoomId, Job>()
        client.roomListService.allRooms.summaries
            .map { summaries ->
                summaries
                    .filter { summary ->
                        summary.info.currentUserMembership == CurrentUserMembership.JOINED && summary.info.hasRoomCall
                    }
                    .map { it.roomId }
                    .toSet()
            }
            .distinctUntilChanged()
            .collectLatest { roomIdsWithActiveCall ->
                Timber.tag(incomingCallTraceTag).i(
                    "ForegroundIncomingCallObserver rooms with active call sessionId=%s roomIds=%s",
                    sessionId,
                    roomIdsWithActiveCall,
                )
                val removedRoomIds = roomJobs.keys - roomIdsWithActiveCall
                removedRoomIds.forEach { roomId ->
                    Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver stop room observer roomId=%s", roomId)
                    roomJobs.remove(roomId)?.cancelAndJoin()
                }
                val newRoomIds = roomIdsWithActiveCall - roomJobs.keys
                newRoomIds.forEach { roomId ->
                    Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver start room observer roomId=%s", roomId)
                    roomJobs[roomId] = appCoroutineScope.launch {
                        observeRoomCallNotifications(client, roomId)
                    }
                }
            }
    }

    /**
     * 监听指定房间的实时时间线，并提取最近的来电通知事件。
     *
     * @param client 当前会话对应的 Matrix 客户端。
     * @param roomId 需要观察来电通知的房间 ID。
     */
    private suspend fun observeRoomCallNotifications(client: MatrixClient, roomId: RoomId) {
        val room = client.getJoinedRoom(roomId) ?: run {
            Timber.tag(incomingCallTraceTag).w("ForegroundIncomingCallObserver cannot find joined room roomId=%s", roomId)
            return
        }
        Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver observing live timeline roomId=%s", roomId)
        room.liveTimeline.timelineItems
            .map(::latestCallNotifyEvent)
            .distinctUntilChanged()
            .collectLatest { event ->
                Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver latest call notify roomId=%s eventId=%s", roomId, event?.eventId)
                if (event != null) {
                    processCallNotificationEvent(client, room, event)
                }
            }
    }

    private suspend fun processCallNotificationEvent(
        client: MatrixClient,
        room: JoinedRoom,
        event: EventTimelineItem,
    ) {
        val roomId = room.roomId
        val eventId = event.eventId ?: return
        Timber.tag(incomingCallTraceTag).i(
            "ForegroundIncomingCallObserver process call notify sessionId=%s roomId=%s eventId=%s senderId=%s senderName=%s isInForeground=%s",
            client.sessionId,
            roomId,
            eventId,
            event.sender,
            event.senderProfile.getDisambiguatedDisplayName(event.sender),
            appForegroundStateService.isInForeground.value,
        )
        if (!appForegroundStateService.isInForeground.value) {
            Timber.tag(incomingCallTraceTag).w("ForegroundIncomingCallObserver ignored call notify because app is not foreground")
            return
        }
        val eventKey = "${client.sessionId.value}|${roomId.value}|${eventId.value}"
        if (hasSeenEventKey(eventKey)) {
            Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver ignored duplicate eventKey=%s", eventKey)
            return
        }

        val notificationsResult = client.notificationService.getNotifications(mapOf(roomId to listOf(eventId)))
        val notifications = notificationsResult.getOrElse { error ->
            Timber.tag(incomingCallTraceTag).w(error, "ForegroundIncomingCallObserver notification lookup failed eventKey=%s", eventKey)
            return
        }
        val notificationResult = notifications[eventId] ?: run {
            Timber.tag(incomingCallTraceTag).w(
                "ForegroundIncomingCallObserver notification lookup missing eventKey=%s returnedEventIds=%s",
                eventKey,
                notifications.keys,
            )
            return
        }
        val notificationData = notificationResult.getOrElse { error ->
            Timber.tag(incomingCallTraceTag).w(error, "ForegroundIncomingCallObserver notification lookup returned event failure eventKey=%s", eventKey)
            if (error == NotificationResolverException.EventFilteredOut) {
                dispatchIncomingCallFromTimelineEvent(client, room, event)
            }
            return
        }
        val notifiableEvent = callNotificationEventResolver.resolveEvent(client.sessionId, notificationData).getOrNull()
        val ringingCallEvent = notifiableEvent as? NotifiableRingingCallEvent ?: run {
            Timber.tag(incomingCallTraceTag).w(
                "ForegroundIncomingCallObserver resolved event is not ringing, trying notification data fallback eventKey=%s eventType=%s",
                eventKey,
                notifiableEvent?.javaClass?.simpleName,
            )
            if (dispatchIncomingCallFromNotificationData(notificationData, eventKey)) {
                return
            }
            dispatchIncomingCallFromTimelineEvent(client, room, event)
            return
        }
        markEventKeyAsSeen(eventKey)
        Timber.d("Foreground observer detected incoming call for roomId=%s eventId=%s", roomId, eventId)
        Timber.tag(incomingCallTraceTag).i(
            "ForegroundIncomingCallObserver dispatch incoming call eventKey=%s senderId=%s senderName=%s roomName=%s",
            eventKey,
            ringingCallEvent.senderId,
            ringingCallEvent.senderDisambiguatedDisplayName,
            ringingCallEvent.roomName,
        )
        dispatchIncomingCall(ringingCallEvent, notificationData.isDm)
    }

    private suspend fun dispatchIncomingCall(
        ringingCallEvent: NotifiableRingingCallEvent,
        isDm: Boolean,
    ) {
        Timber.tag(incomingCallTraceTag).i(
            "ForegroundIncomingCallObserver dispatchIncomingCall sessionId=%s roomId=%s eventId=%s senderId=%s senderName=%s",
            ringingCallEvent.sessionId,
            ringingCallEvent.roomId,
            ringingCallEvent.eventId,
            ringingCallEvent.senderId,
            ringingCallEvent.senderDisambiguatedDisplayName,
        )
        elementCallEntryPoint.handleIncomingCall(
            callType = CallType.RoomCall(ringingCallEvent.sessionId, ringingCallEvent.roomId),
            eventId = ringingCallEvent.eventId,
            senderId = ringingCallEvent.senderId,
            roomName = ringingCallEvent.roomName,
            senderName = ringingCallEvent.senderDisambiguatedDisplayName,
            avatarUrl = ringingCallEvent.roomAvatarUrl,
            timestamp = ringingCallEvent.timestamp,
            expirationTimestamp = ringingCallEvent.expirationTimestamp,
            notificationChannelId = notificationChannels.getChannelForIncomingCall(ring = true),
            textContent = ringingCallEvent.description,
            isDm = isDm,
        )
    }

    private suspend fun dispatchIncomingCallFromNotificationData(
        notificationData: NotificationData,
        eventKey: String,
    ): Boolean {
        val content = notificationData.content as? NotificationContent.MessageLike.RtcNotification ?: run {
            Timber.tag(incomingCallTraceTag).w(
                "ForegroundIncomingCallObserver notification data fallback ignored non-rtc content eventKey=%s contentType=%s",
                eventKey,
                notificationData.content.javaClass.simpleName,
            )
            return false
        }
        if (!content.type.shouldShowForegroundIncomingCall()) {
            Timber.tag(incomingCallTraceTag).w(
                "ForegroundIncomingCallObserver notification data fallback ignored unsupported rtc type eventKey=%s rtcType=%s",
                eventKey,
                content.type,
            )
            return false
        }
        if (hasSeenEventKey(eventKey)) {
            Timber.tag(incomingCallTraceTag).w("ForegroundIncomingCallObserver notification data fallback ignored duplicate eventKey=%s", eventKey)
            return true
        }

        // 前台 observer 已经从 room list 确认房间存在 active call；resolver 的二次确认可能落后于同步状态。
        // 因此当 notification data 本身是 RTC 通话通知时，直接注册前台来电，避免被降级成普通消息后丢失 overlay。
        markEventKeyAsSeen(eventKey)
        Timber.tag(incomingCallTraceTag).w(
            "ForegroundIncomingCallObserver dispatch incoming call from notification data fallback eventKey=%s senderId=%s roomName=%s",
            eventKey,
            content.senderId,
            notificationData.roomDisplayName,
        )
        elementCallEntryPoint.handleIncomingCall(
            callType = CallType.RoomCall(notificationData.sessionId, notificationData.roomId),
            eventId = notificationData.eventId,
            senderId = content.senderId,
            roomName = notificationData.roomDisplayName,
            senderName = notificationData.getDisambiguatedDisplayName(content.senderId),
            avatarUrl = notificationData.roomAvatarUrl,
            timestamp = notificationData.timestamp,
            expirationTimestamp = content.expirationTimestampMillis.asForegroundCallExpirationTimestamp(),
            notificationChannelId = notificationChannels.getChannelForIncomingCall(ring = true),
            textContent = null,
            isDm = notificationData.isDm,
        )
        Timber.tag(incomingCallTraceTag).w(
            "ForegroundIncomingCallObserver notification data fallback completed eventKey=%s",
            eventKey,
        )
        return true
    }

    private suspend fun dispatchIncomingCallFromTimelineEvent(
        client: MatrixClient,
        room: JoinedRoom,
        event: EventTimelineItem,
    ) {
        val eventId = event.eventId ?: return
        val eventKey = "${client.sessionId.value}|${room.roomId.value}|${eventId.value}"
        if (hasSeenEventKey(eventKey)) return

        val notificationMetadata = event.timelineCallNotificationMetadata() ?: run {
            Timber.tag(incomingCallTraceTag).w("ForegroundIncomingCallObserver timeline fallback missing metadata eventKey=%s", eventKey)
            return
        }
        if (!notificationMetadata.notificationType.shouldShowForegroundIncomingCall()) {
            Timber.tag(incomingCallTraceTag).i(
                "ForegroundIncomingCallObserver timeline fallback ignored unsupported notificationType eventKey=%s notificationType=%s",
                eventKey,
                notificationMetadata.notificationType,
            )
            return
        }
        // 前台 timeline 已经确认这是一条 m.call.notify；当通知解析 API 因前台/通知规则返回 EventFilteredOut 时，
        // 仍然需要用 timeline 元数据注册来电，否则 hasRingingCall 永远不会被置为 true。
        markEventKeyAsSeen(eventKey)
        val roomInfo = room.info()
        Timber.tag(incomingCallTraceTag).i("ForegroundIncomingCallObserver dispatch incoming call from timeline fallback eventKey=%s", eventKey)
        Timber.tag(incomingCallTraceTag).i(
            "ForegroundIncomingCallObserver timeline fallback sessionId=%s roomId=%s eventId=%s senderId=%s senderName=%s roomName=%s",
            client.sessionId,
            room.roomId,
            eventId,
            event.sender,
            event.senderProfile.getDisambiguatedDisplayName(event.sender),
            roomInfo.name,
        )
        elementCallEntryPoint.handleIncomingCall(
            callType = CallType.RoomCall(client.sessionId, room.roomId),
            eventId = eventId,
            senderId = event.sender,
            roomName = roomInfo.name,
            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
            // 群房间来电必须保持“房间头像”语义：房间没有头像时交给 Avatar 组件生成房间占位图，
            // 不能回退到发起人的头像，否则 UI 会把 room call 看成个人来电。只有 DM 才沿用发送者头像兜底。
            avatarUrl = roomInfo.avatarUrl ?: event.senderProfile.getAvatarUrl().takeIf { roomInfo.isDm },
            timestamp = event.timestamp,
            expirationTimestamp = notificationMetadata.expirationTimestamp.asForegroundCallExpirationTimestamp(),
            notificationChannelId = notificationChannels.getChannelForIncomingCall(ring = true),
            textContent = null,
            isDm = roomInfo.isDm,
        )
    }

    private fun latestCallNotifyEvent(timelineItems: List<MatrixTimelineItem>): EventTimelineItem? {
        return timelineItems
            .asReversed()
            .firstNotNullOfOrNull { item ->
                val event = (item as? MatrixTimelineItem.Event)?.event ?: return@firstNotNullOfOrNull null
                event.takeIf { event.content is CallNotifyContent && event.eventId != null }
            }
    }

    private fun EventTimelineItem.timelineCallNotificationMetadata(): TimelineCallNotificationMetadata? {
        val originalJson = timelineItemDebugInfoProvider().originalJson ?: return null
        return runCatching {
            val root = JSONObject(originalJson)
            val content = root.optJSONObject("content") ?: root
            val notificationType = content.optString("notification_type").ifBlank { content.optString("notify_type") }
            val defaultLifetime = ElementCallConfig.RINGING_CALL_DURATION_SECONDS * 1000L
            val expirationTimestamp = when {
                content.has("expires_ts") -> content.optLong("expires_ts")
                content.has("expiration_ts") -> content.optLong("expiration_ts")
                else -> content.optLong("sender_ts", timestamp) + content.optLong("lifetime", defaultLifetime)
            }

            TimelineCallNotificationMetadata(
                notificationType = notificationType,
                expirationTimestamp = expirationTimestamp,
            )
        }.onFailure {
            Timber.tag(incomingCallTraceTag).w(it, "ForegroundIncomingCallObserver failed to parse timeline call notify metadata")
        }.getOrNull()
    }

    private fun defaultFallbackExpirationTimestamp(): Long {
        return systemClock.epochMillis() + ElementCallConfig.RINGING_CALL_DURATION_SECONDS * 1000L
    }

    private fun RtcNotificationType.shouldShowForegroundIncomingCall(): Boolean {
        return when (this) {
            RtcNotificationType.RING,
            RtcNotificationType.NOTIFY -> true
        }
    }

    private fun String.shouldShowForegroundIncomingCall(): Boolean {
        return equals(callNotifyTypeRing, ignoreCase = true) ||
            equals(callNotifyTypeNotification, ignoreCase = true) ||
            equals(callNotifyTypeNotify, ignoreCase = true)
    }

    private fun Long.asForegroundCallExpirationTimestamp(): Long {
        val fallbackExpirationTimestamp = defaultFallbackExpirationTimestamp()
        // NOTIFY 事件通常没有有效过期时间；前台 overlay 仍需要一个短生命周期，避免立刻被 ActiveCallManager 判定过期。
        return takeIf { it > systemClock.epochMillis() }?.coerceAtLeast(fallbackExpirationTimestamp) ?: fallbackExpirationTimestamp
    }

    private fun hasSeenEventKey(eventKey: String): Boolean {
        synchronized(processedEventKeys) {
            return eventKey in processedEventKeys
        }
    }

    private fun markEventKeyAsSeen(eventKey: String) {
        synchronized(processedEventKeys) {
            processedEventKeys.add(eventKey)
            while (processedEventKeys.size > maxProcessedEventKeys) {
                processedEventKeys.remove(processedEventKeys.first())
            }
        }
    }
}

/**
 * 从时间线事件解析出的来电通知元数据。
 *
 * @property notificationType 事件声明的通知类型，用于判断是否需要前台响铃。
 * @property expirationTimestamp 本次来电在前台视图中的过期时间戳。
 */
private data class TimelineCallNotificationMetadata(
    val notificationType: String,
    val expirationTimestamp: Long,
)
