/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import com.google.common.truth.Truth.assertThat
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import org.junit.Test

class IncomingCallOverlayStateTest {
    @Test
    fun `incoming call overlay state excludes current fullscreen call`() {
        val fullscreenCall = aCallNotificationData(
            roomId = RoomId("!fullscreen:matrix.org"),
            eventId = EventId("\$fullscreen-event:matrix.org"),
            senderId = UserId("@alice:matrix.org"),
            roomName = "Fullscreen room",
        )
        val duplicateFullscreenCall = fullscreenCall.copy(
            eventId = EventId("\$new-event-same-ringing-identity:matrix.org"),
        )
        val secondaryCall = aCallNotificationData(
            roomId = RoomId("!secondary:matrix.org"),
            eventId = EventId("\$secondary-event:matrix.org"),
            senderId = UserId("@bob:matrix.org"),
            roomName = "Secondary room",
        )

        val state = listOf(fullscreenCall, duplicateFullscreenCall, secondaryCall).toIncomingCallOverlayState(
            excludedCall = fullscreenCall,
            onAnswerClick = {},
            onDeclineClick = {},
        )

        assertThat(state.calls.map { it.id }).containsExactly(secondaryCall.eventId.value)
    }

    @Test
    fun `incoming call overlay state keeps all calls when no call is excluded`() {
        val firstCall = aCallNotificationData(
            roomId = RoomId("!first:matrix.org"),
            eventId = EventId("\$first-event:matrix.org"),
            senderId = UserId("@alice:matrix.org"),
            roomName = "First room",
        )
        val secondCall = aCallNotificationData(
            roomId = RoomId("!second:matrix.org"),
            eventId = EventId("\$second-event:matrix.org"),
            senderId = UserId("@bob:matrix.org"),
            roomName = "Second room",
        )

        val state = listOf(firstCall, secondCall).toIncomingCallOverlayState(
            onAnswerClick = {},
            onDeclineClick = {},
        )

        assertThat(state.calls.map { it.id }).containsExactly(firstCall.eventId.value, secondCall.eventId.value).inOrder()
    }

    @Test
    fun `incoming call overlay state uses sender id as subtitle for DM calls`() {
        val dmCall = aCallNotificationData(
            roomId = RoomId("!dm:matrix.org"),
            eventId = EventId("\$dm-event:matrix.org"),
            senderId = UserId("@alice:matrix.org"),
            roomName = "Alice",
            senderName = "Alice",
            isDm = true,
        )

        val state = listOf(dmCall).toIncomingCallOverlayState(
            onAnswerClick = {},
            onDeclineClick = {},
        )

        assertThat(state.calls.single().subtitle).isEqualTo(dmCall.senderId.value)
    }

    @Test
    fun `incoming call overlay state uses room id as subtitle for room calls`() {
        val roomCall = aCallNotificationData(
            roomId = RoomId("!room:matrix.org"),
            eventId = EventId("\$room-event:matrix.org"),
            senderId = UserId("@alice:matrix.org"),
            roomName = "Design Sync",
            senderName = "Alice",
            isDm = false,
        )

        val state = listOf(roomCall).toIncomingCallOverlayState(
            onAnswerClick = {},
            onDeclineClick = {},
        )

        assertThat(state.calls.single().subtitle).isEqualTo(roomCall.roomId.value)
    }

    private fun aCallNotificationData(
        roomId: RoomId,
        eventId: EventId,
        senderId: UserId,
        roomName: String,
        senderName: String? = senderId.value,
        isDm: Boolean = false,
    ): CallNotificationData {
        return CallNotificationData(
            sessionId = SessionId("@session:matrix.org"),
            roomId = roomId,
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            senderName = senderName,
            avatarUrl = null,
            notificationChannelId = "incoming_call",
            timestamp = 0L,
            textContent = null,
            expirationTimestamp = 1_000L,
            isDm = isDm,
        )
    }
}
