/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import com.google.common.truth.Truth.assertThat
import io.element.android.features.home.impl.model.LatestEvent
import io.element.android.libraries.eventformatter.test.FakeRoomLatestEventFormatter
import io.element.android.libraries.matrix.test.room.aLocalLatestEvent
import io.element.android.libraries.matrix.test.room.aRemoteLatestEvent
import io.element.android.libraries.matrix.test.room.aRoomSummary
import org.junit.Test

class RoomListRoomSummaryFactoryBehaviorTest {
    @Test
    fun `create returns no latest event when formatter hides a remote event`() {
        val roomLatestEventFormatter = FakeRoomLatestEventFormatter().apply {
            givenFormatResult(null)
        }
        val sut = aRoomListRoomSummaryFactory(roomLatestEventFormatter = roomLatestEventFormatter)

        val result = sut.create(
            aRoomSummary(latestEvent = aRemoteLatestEvent())
        )

        assertThat(result.latestEvent).isEqualTo(LatestEvent.None)
    }

    @Test
    fun `create returns no latest event when formatter hides a local event`() {
        val roomLatestEventFormatter = FakeRoomLatestEventFormatter().apply {
            givenFormatResult(null)
        }
        val sut = aRoomListRoomSummaryFactory(roomLatestEventFormatter = roomLatestEventFormatter)

        val result = sut.create(
            aRoomSummary(latestEvent = aLocalLatestEvent(isSending = true))
        )

        assertThat(result.latestEvent).isEqualTo(LatestEvent.None)
    }

    @Test
    fun `create formats remote latest event mentions`() {
        val roomLatestEventFormatter = FakeRoomLatestEventFormatter().apply {
            givenFormatResult("Hello https://matrix.to/#/@alice:example.com")
        }
        val sut = aRoomListRoomSummaryFactory(
            roomLatestEventFormatter = roomLatestEventFormatter,
            latestEventMentionFormatter = FakeLatestEventMentionFormatter { _, _ -> "Hello @Alice" },
        )

        val result = sut.create(
            aRoomSummary(latestEvent = aRemoteLatestEvent())
        )

        assertThat(result.latestEvent).isEqualTo(LatestEvent.Synced("Hello @Alice"))
    }

    @Test
    fun `create formats local latest event mentions`() {
        val roomLatestEventFormatter = FakeRoomLatestEventFormatter().apply {
            givenFormatResult("Hello https://matrix.to/#/@alice:example.com")
        }
        val sut = aRoomListRoomSummaryFactory(
            roomLatestEventFormatter = roomLatestEventFormatter,
            latestEventMentionFormatter = FakeLatestEventMentionFormatter { _, _ -> "Hello @Alice" },
        )

        val result = sut.create(
            aRoomSummary(latestEvent = aLocalLatestEvent(isSending = true))
        )

        assertThat(result.latestEvent).isEqualTo(LatestEvent.Sending("Hello @Alice"))
    }
}
