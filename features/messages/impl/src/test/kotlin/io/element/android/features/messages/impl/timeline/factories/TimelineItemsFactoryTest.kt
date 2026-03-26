/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.factories

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.element.android.features.messages.impl.fixtures.aTimelineItemsFactory
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.virtual.VirtualTimelineItem
import io.element.android.libraries.matrix.test.AN_EVENT_ID
import io.element.android.libraries.matrix.test.AN_EVENT_ID_2
import io.element.android.libraries.matrix.test.A_UNIQUE_ID
import io.element.android.libraries.matrix.test.A_UNIQUE_ID_2
import io.element.android.libraries.matrix.test.timeline.aMessageContent
import io.element.android.libraries.matrix.test.timeline.aProfileChangeMessageContent
import io.element.android.libraries.matrix.test.timeline.anEventTimelineItem
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TimelineItemsFactoryTest {
    @Test
    fun `replaceWith filters profile change events from the timeline`() = runTest {
        val sut = aTimelineItemsFactory(
            config = TimelineItemsFactoryConfig(
                computeReadReceipts = false,
                computeReactions = false,
            ),
        )
        sut.replaceWith(
            timelineItems = listOf(
                MatrixTimelineItem.Event(
                    uniqueId = A_UNIQUE_ID,
                    event = anEventTimelineItem(
                        eventId = AN_EVENT_ID,
                        content = aProfileChangeMessageContent(),
                    ),
                ),
                MatrixTimelineItem.Event(
                    uniqueId = A_UNIQUE_ID_2,
                    event = anEventTimelineItem(
                        eventId = AN_EVENT_ID_2,
                        content = aMessageContent(body = "Visible message"),
                    ),
                ),
            ),
            roomMembers = emptyList(),
        )

        sut.timelineItems.test {
            val timelineItems = awaitItem()
            assertThat(timelineItems).hasSize(1)
            val event = timelineItems.single() as TimelineItem.Event
            assertThat(event.eventId).isEqualTo(AN_EVENT_ID_2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `replaceWith removes day divider when it only contains profile change events`() = runTest {
        val sut = aTimelineItemsFactory(
            config = TimelineItemsFactoryConfig(
                computeReadReceipts = false,
                computeReactions = false,
            ),
        )
        sut.replaceWith(
            timelineItems = listOf(
                MatrixTimelineItem.Virtual(
                    uniqueId = UniqueId("day_divider"),
                    virtual = VirtualTimelineItem.DayDivider(timestamp = 0L),
                ),
                MatrixTimelineItem.Event(
                    uniqueId = A_UNIQUE_ID,
                    event = anEventTimelineItem(
                        eventId = AN_EVENT_ID,
                        content = aProfileChangeMessageContent(),
                    ),
                ),
            ),
            roomMembers = emptyList(),
        )

        sut.timelineItems.test {
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `replaceWith keeps day divider when the day still contains a visible event`() = runTest {
        val sut = aTimelineItemsFactory(
            config = TimelineItemsFactoryConfig(
                computeReadReceipts = false,
                computeReactions = false,
            ),
        )
        sut.replaceWith(
            timelineItems = listOf(
                MatrixTimelineItem.Virtual(
                    uniqueId = UniqueId("day_divider"),
                    virtual = VirtualTimelineItem.DayDivider(timestamp = 0L),
                ),
                MatrixTimelineItem.Event(
                    uniqueId = A_UNIQUE_ID,
                    event = anEventTimelineItem(
                        eventId = AN_EVENT_ID,
                        content = aProfileChangeMessageContent(),
                    ),
                ),
                MatrixTimelineItem.Event(
                    uniqueId = A_UNIQUE_ID_2,
                    event = anEventTimelineItem(
                        eventId = AN_EVENT_ID_2,
                        content = aMessageContent(body = "Visible message"),
                    ),
                ),
            ),
            roomMembers = emptyList(),
        )

        sut.timelineItems.test {
            val timelineItems = awaitItem()
            assertThat(timelineItems).hasSize(2)
            assertThat(timelineItems[0]).isInstanceOf(TimelineItem.Virtual::class.java)
            val event = timelineItems[1] as TimelineItem.Event
            assertThat(event.eventId).isEqualTo(AN_EVENT_ID_2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
