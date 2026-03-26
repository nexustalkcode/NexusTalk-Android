/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import com.google.common.truth.Truth.assertThat
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.test.FakeMatrixClient
import io.element.android.libraries.matrix.test.permalink.FakePermalinkParser
import io.element.android.libraries.matrix.test.room.aRoomMember
import io.element.android.libraries.matrix.test.room.aRoomSummary
import io.element.android.libraries.matrix.test.roomlist.FakeRoomListService
import org.junit.Test

class LatestEventMentionFormatterTest {
    @Test
    fun `format uses hero display name for user permalink`() {
        val userId = UserId("@alice:example.com")
        val permalink = "https://matrix.to/#/@alice:example.com"
        val formatter = DefaultLatestEventMentionFormatter(
            permalinkParser = FakePermalinkParser { url ->
                when (url) {
                    permalink -> PermalinkData.UserLink(userId)
                    else -> error("Unexpected url: $url")
                }
            },
            matrixClient = FakeMatrixClient(),
        )

        val result = formatter.format(
            text = "Hello $permalink",
            roomSummary = aRoomSummary(
                heroes = listOf(MatrixUser(userId = userId, displayName = "Alice")),
            ),
        )

        assertThat(result.toString()).isEqualTo("Hello @Alice")
    }

    @Test
    fun `format uses inviter display name when hero is unavailable`() {
        val userId = UserId("@alice:example.com")
        val permalink = "https://matrix.to/#/@alice:example.com"
        val formatter = DefaultLatestEventMentionFormatter(
            permalinkParser = FakePermalinkParser { url ->
                when (url) {
                    permalink -> PermalinkData.UserLink(userId)
                    else -> error("Unexpected url: $url")
                }
            },
            matrixClient = FakeMatrixClient(),
        )

        val result = formatter.format(
            text = permalink,
            roomSummary = aRoomSummary(
                inviter = aRoomMember(userId = userId, displayName = "Alice"),
            ),
        )

        assertThat(result.toString()).isEqualTo("@Alice")
    }

    @Test
    fun `format uses room summary name for room and message permalinks`() {
        val roomId = RoomId("!room:example.com")
        val alias = RoomAlias("#team:example.com")
        val roomPermalink = "https://matrix.to/#/#team:example.com"
        val messagePermalink = "https://matrix.to/#/!room:example.com/\$event"
        val roomListService = FakeRoomListService().apply {
            postAllRooms(
                listOf(
                    aRoomSummary(
                        roomId = roomId,
                        name = "Team",
                        canonicalAlias = alias,
                    )
                )
            )
        }
        val formatter = DefaultLatestEventMentionFormatter(
            permalinkParser = FakePermalinkParser { url ->
                when (url) {
                    roomPermalink -> PermalinkData.RoomLink(alias)
                    messagePermalink -> PermalinkData.RoomLink(roomId, eventId = EventId("\$event"))
                    else -> error("Unexpected url: $url")
                }
            },
            matrixClient = FakeMatrixClient(roomListService = roomListService),
        )

        val result = formatter.format(
            text = "$roomPermalink and $messagePermalink",
            roomSummary = aRoomSummary(
                roomId = roomId,
                name = "Team",
                canonicalAlias = alias,
            ),
        )

        assertThat(result.toString()).isEqualTo("#Team and \uD83D\uDCAC > #Team")
    }
}
