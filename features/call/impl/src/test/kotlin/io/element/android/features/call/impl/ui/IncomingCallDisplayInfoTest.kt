/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import com.google.common.truth.Truth.assertThat
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.libraries.matrix.test.AN_EVENT_ID
import io.element.android.libraries.matrix.test.A_ROOM_ID
import io.element.android.libraries.matrix.test.A_SESSION_ID
import io.element.android.libraries.matrix.test.A_USER_ID_2
import org.junit.Test

class IncomingCallDisplayInfoTest {
    @Test
    fun `incoming call display info - uses room information first`() {
        val notificationData = aCallNotificationData(
            roomName = "考虑考虑",
            senderName = "qwr",
            isDm = false,
        )

        assertThat(notificationData.incomingCallTitle()).isEqualTo("考虑考虑")
        assertThat(notificationData.incomingCallSubtitle()).isEqualTo("qwr")
        assertThat(notificationData.incomingCallAvatarId()).isEqualTo(A_ROOM_ID.value)
        assertThat(notificationData.incomingCallAvatarName()).isEqualTo("考虑考虑")
    }

    @Test
    fun `incoming call display info - falls back to sender when room is missing`() {
        val notificationData = aCallNotificationData(
            roomName = null,
            senderName = "qwr",
            isDm = false,
        )

        assertThat(notificationData.incomingCallTitle()).isEqualTo("qwr")
        assertThat(notificationData.incomingCallSubtitle()).isEqualTo("qwr")
        assertThat(notificationData.incomingCallAvatarName()).isEqualTo("qwr")
    }

    @Test
    fun `incoming call display info - uses sender information for dm rooms`() {
        val notificationData = aCallNotificationData(
            roomName = "考虑考虑",
            senderName = "qwr",
            isDm = true,
        )

        assertThat(notificationData.incomingCallTitle()).isEqualTo("qwr")
        assertThat(notificationData.incomingCallSubtitle()).isEqualTo("qwr")
        assertThat(notificationData.incomingCallAvatarId()).isEqualTo(A_USER_ID_2.value)
        assertThat(notificationData.incomingCallAvatarName()).isEqualTo("qwr")
    }

    private fun aCallNotificationData(
        roomName: String?,
        senderName: String?,
        isDm: Boolean,
    ): CallNotificationData {
        return CallNotificationData(
            sessionId = A_SESSION_ID,
            roomId = A_ROOM_ID,
            eventId = AN_EVENT_ID,
            senderId = A_USER_ID_2,
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
