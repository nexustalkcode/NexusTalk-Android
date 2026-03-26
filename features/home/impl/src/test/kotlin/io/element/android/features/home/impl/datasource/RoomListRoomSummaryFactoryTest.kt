/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import io.element.android.libraries.dateformatter.api.DateFormatter
import io.element.android.libraries.dateformatter.test.FakeDateFormatter
import io.element.android.libraries.eventformatter.api.RoomLatestEventFormatter
import io.element.android.libraries.eventformatter.test.FakeRoomLatestEventFormatter
import io.element.android.libraries.matrix.api.roomlist.RoomSummary

fun aRoomListRoomSummaryFactory(
    dateFormatter: DateFormatter = FakeDateFormatter { _, _, _ -> "Today" },
    roomLatestEventFormatter: RoomLatestEventFormatter = FakeRoomLatestEventFormatter(),
    latestEventMentionFormatter: LatestEventMentionFormatter = FakeLatestEventMentionFormatter(),
) = RoomListRoomSummaryFactory(
    dateFormatter = dateFormatter,
    roomLatestEventFormatter = roomLatestEventFormatter,
    latestEventMentionFormatter = latestEventMentionFormatter,
)

class FakeLatestEventMentionFormatter(
    private val formatLambda: (CharSequence, RoomSummary) -> CharSequence = { text, _ -> text }
) : LatestEventMentionFormatter {
    override fun format(text: CharSequence, roomSummary: RoomSummary): CharSequence {
        return formatLambda(text, roomSummary)
    }
}
