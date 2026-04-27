/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.factories.virtual

import dev.zacsweers.metro.Inject
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemLastForwardIndicatorModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemLoadingIndicatorModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemReadMarkerModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemRoomBeginningModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemTypingNotificationModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemVirtualModel
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.virtual.VirtualTimelineItem

@Inject
/**
 * 虚拟时间线项工厂。
 */
class TimelineItemVirtualFactory(
    private val daySeparatorFactory: TimelineItemDaySeparatorFactory,
) {
    /**
     * 将底层虚拟时间线项转换为 UI 虚拟项。
     */
    fun create(
        virtualTimelineItem: MatrixTimelineItem.Virtual,
    ): TimelineItem.Virtual {
        return TimelineItem.Virtual(
            id = virtualTimelineItem.uniqueId,
            model = virtualTimelineItem.computeModel()
        )
    }

    /**
     * 根据底层虚拟项类型生成对应 UI 模型。
     */
    private fun MatrixTimelineItem.Virtual.computeModel(): TimelineItemVirtualModel {
        return when (val inner = virtual) {
            is VirtualTimelineItem.DayDivider -> daySeparatorFactory.create(inner)
            is VirtualTimelineItem.ReadMarker -> TimelineItemReadMarkerModel
            is VirtualTimelineItem.RoomBeginning -> TimelineItemRoomBeginningModel
            is VirtualTimelineItem.LoadingIndicator -> TimelineItemLoadingIndicatorModel(
                direction = inner.direction,
                timestamp = inner.timestamp
            )
            is VirtualTimelineItem.LastForwardIndicator -> TimelineItemLastForwardIndicatorModel
            VirtualTimelineItem.TypingNotification -> TimelineItemTypingNotificationModel
        }
    }
}
