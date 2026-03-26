/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl

import com.google.common.truth.Truth.assertThat
import io.element.android.features.call.api.CallType
import io.element.android.features.call.test.FakeElementCallEntryPoint
import io.element.android.libraries.matrix.api.notification.NotificationContent
import io.element.android.libraries.matrix.api.notification.RtcNotificationType
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.CallNotifyContent
import io.element.android.libraries.matrix.test.AN_EVENT_ID
import io.element.android.libraries.matrix.test.A_ROOM_ID
import io.element.android.libraries.matrix.test.A_SESSION_ID
import io.element.android.libraries.matrix.test.A_UNIQUE_ID
import io.element.android.libraries.matrix.test.A_USER_ID_2
import io.element.android.libraries.matrix.test.FakeMatrixClient
import io.element.android.libraries.matrix.test.FakeMatrixClientProvider
import io.element.android.libraries.matrix.test.notification.FakeNotificationService
import io.element.android.libraries.matrix.test.notification.aNotificationData
import io.element.android.libraries.matrix.test.room.FakeJoinedRoom
import io.element.android.libraries.matrix.test.room.aRoomSummary
import io.element.android.libraries.matrix.test.roomlist.FakeRoomListService
import io.element.android.libraries.matrix.test.timeline.FakeTimeline
import io.element.android.libraries.matrix.test.timeline.anEventTimelineItem
import io.element.android.libraries.push.impl.notifications.channels.NotificationChannels
import io.element.android.libraries.push.impl.notifications.model.NotifiableRingingCallEvent
import io.element.android.libraries.push.test.notifications.FakeCallNotificationEventResolver
import io.element.android.libraries.sessionstorage.test.InMemorySessionStore
import io.element.android.libraries.sessionstorage.test.aSessionData
import io.element.android.services.appnavstate.test.FakeAppForegroundStateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ForegroundIncomingCallObserverTest {
    private val expirationTimestamp = 3_000L

    @Test
    fun `start - handles ringing call when app is in foreground`() = runTest {
        val timelineItems = MutableStateFlow<List<MatrixTimelineItem>>(emptyList())
        val roomListService = FakeRoomListService()
        val room = FakeJoinedRoom(
            liveTimeline = FakeTimeline(timelineItems = timelineItems),
        )
        val notificationService = FakeNotificationService().apply {
            givenGetNotificationsResult(
                Result.success(
                    mapOf(
                        AN_EVENT_ID to Result.success(
                            aNotificationData(
                                content = NotificationContent.MessageLike.RtcNotification(
                                    senderId = A_USER_ID_2,
                                    type = RtcNotificationType.RING,
                                    expirationTimestampMillis = expirationTimestamp,
                                ),
                            ).copy(
                                sessionId = A_SESSION_ID,
                                roomId = A_ROOM_ID,
                                eventId = AN_EVENT_ID,
                            )
                        )
                    )
                )
            )
        }
        val matrixClient = FakeMatrixClient(
            notificationService = notificationService,
            roomListService = roomListService,
        ).apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val entryPointCalls = mutableListOf<Pair<CallType.RoomCall, String>>()
        val observer = createObserver(
            matrixClient = matrixClient,
            elementCallEntryPoint = FakeElementCallEntryPoint(
                handleIncomingCallResult = { callType, eventId, _, _, _, _, channelId, _ ->
                    entryPointCalls += callType to "${eventId.value}|$channelId"
                }
            ),
            callNotificationEventResolver = FakeCallNotificationEventResolver(
                resolveEventLambda = { _, _, _ ->
                    Result.success(
                        NotifiableRingingCallEvent(
                            sessionId = A_SESSION_ID,
                            roomId = A_ROOM_ID,
                            eventId = AN_EVENT_ID,
                            editedEventId = null,
                            description = "description",
                            canBeReplaced = false,
                            isRedacted = false,
                            isUpdated = false,
                            roomName = "roomName",
                            senderId = A_USER_ID_2,
                            senderDisambiguatedDisplayName = "Bob",
                            senderAvatarUrl = null,
                            roomAvatarUrl = null,
                            rtcNotificationType = RtcNotificationType.RING,
                            timestamp = 0L,
                            expirationTimestamp = expirationTimestamp,
                        )
                    )
                }
            ),
        )

        observer.start()
        sessionStore.addSession(aSessionData(sessionId = A_SESSION_ID.value))
        roomListService.postAllRooms(listOf(aRoomSummary(roomId = A_ROOM_ID, hasRoomCall = true)))
        timelineItems.value = listOf(
            MatrixTimelineItem.Event(
                uniqueId = A_UNIQUE_ID,
                event = anEventTimelineItem(
                    eventId = AN_EVENT_ID,
                    content = CallNotifyContent,
                )
            )
        )

        advanceUntilIdle()

        assertThat(entryPointCalls).containsExactly(
            CallType.RoomCall(A_SESSION_ID, A_ROOM_ID) to "${AN_EVENT_ID.value}|ringing_channel"
        )
    }

    @Test
    fun `start - ignores ringing call when app is in background`() = runTest {
        val timelineItems = MutableStateFlow<List<MatrixTimelineItem>>(emptyList())
        val roomListService = FakeRoomListService()
        val room = FakeJoinedRoom(
            liveTimeline = FakeTimeline(timelineItems = timelineItems),
        )
        val matrixClient = FakeMatrixClient(roomListService = roomListService).apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        var handledCallCount = 0
        val observer = createObserver(
            matrixClient = matrixClient,
            appForegroundStateService = FakeAppForegroundStateService(initialForegroundValue = false),
            elementCallEntryPoint = FakeElementCallEntryPoint(
                handleIncomingCallResult = { _, _, _, _, _, _, _, _ ->
                    handledCallCount++
                }
            ),
        )

        observer.start()
        sessionStore.addSession(aSessionData(sessionId = A_SESSION_ID.value))
        roomListService.postAllRooms(listOf(aRoomSummary(roomId = A_ROOM_ID, hasRoomCall = true)))
        timelineItems.value = listOf(
            MatrixTimelineItem.Event(
                uniqueId = A_UNIQUE_ID,
                event = anEventTimelineItem(
                    eventId = AN_EVENT_ID,
                    content = CallNotifyContent,
                )
            )
        )

        advanceUntilIdle()

        assertThat(handledCallCount).isEqualTo(0)
    }

    @Test
    fun `start - deduplicates the same event when room observer restarts`() = runTest {
        val timelineItems = MutableStateFlow<List<MatrixTimelineItem>>(emptyList())
        val roomListService = FakeRoomListService()
        val room = FakeJoinedRoom(
            liveTimeline = FakeTimeline(timelineItems = timelineItems),
        )
        val notificationService = FakeNotificationService().apply {
            givenGetNotificationsResult(
                Result.success(
                    mapOf(
                        AN_EVENT_ID to Result.success(
                            aNotificationData(
                                content = NotificationContent.MessageLike.RtcNotification(
                                    senderId = A_USER_ID_2,
                                    type = RtcNotificationType.RING,
                                    expirationTimestampMillis = expirationTimestamp,
                                ),
                            ).copy(
                                sessionId = A_SESSION_ID,
                                roomId = A_ROOM_ID,
                                eventId = AN_EVENT_ID,
                            )
                        )
                    )
                )
            )
        }
        val matrixClient = FakeMatrixClient(
            notificationService = notificationService,
            roomListService = roomListService,
        ).apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        var handledCallCount = 0
        val observer = createObserver(
            matrixClient = matrixClient,
            elementCallEntryPoint = FakeElementCallEntryPoint(
                handleIncomingCallResult = { _, _, _, _, _, _, _, _ ->
                    handledCallCount++
                }
            ),
            callNotificationEventResolver = FakeCallNotificationEventResolver(
                resolveEventLambda = { _, _, _ ->
                    Result.success(
                        NotifiableRingingCallEvent(
                            sessionId = A_SESSION_ID,
                            roomId = A_ROOM_ID,
                            eventId = AN_EVENT_ID,
                            editedEventId = null,
                            description = "description",
                            canBeReplaced = false,
                            isRedacted = false,
                            isUpdated = false,
                            roomName = "roomName",
                            senderId = A_USER_ID_2,
                            senderDisambiguatedDisplayName = "Bob",
                            senderAvatarUrl = null,
                            roomAvatarUrl = null,
                            rtcNotificationType = RtcNotificationType.RING,
                            timestamp = 0L,
                            expirationTimestamp = expirationTimestamp,
                        )
                    )
                }
            ),
        )

        observer.start()
        sessionStore.addSession(aSessionData(sessionId = A_SESSION_ID.value))
        timelineItems.value = listOf(
            MatrixTimelineItem.Event(
                uniqueId = A_UNIQUE_ID,
                event = anEventTimelineItem(
                    eventId = AN_EVENT_ID,
                    content = CallNotifyContent,
                )
            )
        )

        roomListService.postAllRooms(listOf(aRoomSummary(roomId = A_ROOM_ID, hasRoomCall = true)))
        advanceUntilIdle()
        roomListService.postAllRooms(emptyList())
        advanceUntilIdle()
        roomListService.postAllRooms(listOf(aRoomSummary(roomId = A_ROOM_ID, hasRoomCall = true)))
        advanceUntilIdle()

        assertThat(handledCallCount).isEqualTo(1)
    }

    private val sessionStore = InMemorySessionStore()

    private fun TestScope.createObserver(
        matrixClient: FakeMatrixClient,
        appForegroundStateService: FakeAppForegroundStateService = FakeAppForegroundStateService(),
        callNotificationEventResolver: FakeCallNotificationEventResolver = FakeCallNotificationEventResolver(
            resolveEventLambda = { _, _, _ -> Result.failure(IllegalStateException("Unexpected resolveEvent call")) }
        ),
        elementCallEntryPoint: FakeElementCallEntryPoint = FakeElementCallEntryPoint(
            handleIncomingCallResult = { _, _, _, _, _, _, _, _ -> }
        ),
    ): ForegroundIncomingCallObserver {
        return DefaultForegroundIncomingCallObserver(
            appCoroutineScope = backgroundScope,
            sessionStore = sessionStore,
            matrixClientProvider = FakeMatrixClientProvider { Result.success(matrixClient) },
            callNotificationEventResolver = callNotificationEventResolver,
            elementCallEntryPoint = elementCallEntryPoint,
            notificationChannels = object : NotificationChannels {
                override fun getChannelForIncomingCall(ring: Boolean): String = "ringing_channel"

                override fun getChannelIdForMessage(noisy: Boolean): String = "message_channel"

                override fun getChannelIdForTest(): String = "test_channel"
            },
            appForegroundStateService = appForegroundStateService,
        )
    }
}
