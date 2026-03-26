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
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.room.CurrentUserMembership
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.CallNotifyContent
import io.element.android.libraries.push.impl.notifications.CallNotificationEventResolver
import io.element.android.libraries.push.impl.notifications.channels.NotificationChannels
import io.element.android.libraries.push.impl.notifications.model.NotifiableRingingCallEvent
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

interface ForegroundIncomingCallObserver {
    fun start()
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultForegroundIncomingCallObserver(
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val sessionStore: SessionStore,
    private val matrixClientProvider: MatrixClientProvider,
    private val callNotificationEventResolver: CallNotificationEventResolver,
    private val elementCallEntryPoint: ElementCallEntryPoint,
    private val notificationChannels: NotificationChannels,
    private val appForegroundStateService: AppForegroundStateService,
) : ForegroundIncomingCallObserver {
    private val isStarted = AtomicBoolean(false)
    private val processedEventKeys = LinkedHashSet<String>()
    private val maxProcessedEventKeys = 128

    override fun start() {
        if (!isStarted.compareAndSet(false, true)) return
        appCoroutineScope.launch {
            observeSessions()
        }
    }

    private suspend fun observeSessions() {
        val sessionJobs = mutableMapOf<SessionId, Job>()
        sessionStore.sessionsFlow()
            .map { sessions -> sessions.map { SessionId(it.userId) }.toSet() }
            .distinctUntilChanged()
            .collectLatest { sessionIds ->
                val removedSessionIds = sessionJobs.keys - sessionIds
                removedSessionIds.forEach { sessionId ->
                    sessionJobs.remove(sessionId)?.cancelAndJoin()
                }
                val newSessionIds = sessionIds - sessionJobs.keys
                newSessionIds.forEach { sessionId ->
                    sessionJobs[sessionId] = appCoroutineScope.launch {
                        observeSession(sessionId)
                    }
                }
            }
    }

    private suspend fun observeSession(sessionId: SessionId) {
        val client = matrixClientProvider.getOrRestore(sessionId).getOrNull() ?: return
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
                val removedRoomIds = roomJobs.keys - roomIdsWithActiveCall
                removedRoomIds.forEach { roomId ->
                    roomJobs.remove(roomId)?.cancelAndJoin()
                }
                val newRoomIds = roomIdsWithActiveCall - roomJobs.keys
                newRoomIds.forEach { roomId ->
                    roomJobs[roomId] = appCoroutineScope.launch {
                        observeRoomCallNotifications(client, roomId)
                    }
                }
            }
    }

    private suspend fun observeRoomCallNotifications(client: MatrixClient, roomId: RoomId) {
        val room = client.getJoinedRoom(roomId) ?: return
        room.liveTimeline.timelineItems
            .map(::latestCallNotifyEventId)
            .distinctUntilChanged()
            .collectLatest { eventId ->
                if (eventId != null) {
                    processCallNotificationEvent(client, roomId, eventId)
                }
            }
    }

    private suspend fun processCallNotificationEvent(
        client: MatrixClient,
        roomId: RoomId,
        eventId: EventId,
    ) {
        if (!appForegroundStateService.isInForeground.value) return
        val eventKey = "${client.sessionId.value}|${roomId.value}|${eventId.value}"
        if (hasSeenEventKey(eventKey)) return

        val notificationData = client.notificationService
            .getNotifications(mapOf(roomId to listOf(eventId)))
            .getOrNull()
            ?.get(eventId)
            ?.getOrNull()
            ?: return
        val notifiableEvent = callNotificationEventResolver.resolveEvent(client.sessionId, notificationData).getOrNull()
        val ringingCallEvent = notifiableEvent as? NotifiableRingingCallEvent ?: return
        markEventKeyAsSeen(eventKey)
        Timber.d("Foreground observer detected incoming call for roomId=%s eventId=%s", roomId, eventId)
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
        )
    }

    private fun latestCallNotifyEventId(timelineItems: List<MatrixTimelineItem>): EventId? {
        return timelineItems
            .asReversed()
            .firstNotNullOfOrNull { item ->
                val event = (item as? MatrixTimelineItem.Event)?.event ?: return@firstNotNullOfOrNull null
                event.eventId?.takeIf { event.content is CallNotifyContent }
            }
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
