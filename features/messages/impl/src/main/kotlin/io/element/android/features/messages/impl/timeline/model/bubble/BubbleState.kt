/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.bubble

import io.element.android.features.messages.impl.timeline.TimelineRoomInfo
import io.element.android.features.messages.impl.timeline.model.TimelineItemGroupPosition

/**
 * 气泡状态数据类
 *
 * @property groupPosition 时间线项目组位置
 * @property isMine 是否为当前用户的消息
 * @property timelineRoomInfo 时间线房间信息
 * @property cutTopStart 是否裁剪气泡左上角（为发送者头像留出边距）
 */
data class BubbleState(
    val groupPosition: TimelineItemGroupPosition,
    val isMine: Boolean,
    val timelineRoomInfo: TimelineRoomInfo,
) {
    /** True to cut out the top start corner of the bubble, to give margin for the sender avatar. */
    val cutTopStart: Boolean = groupPosition.isNew() && !isMine && !timelineRoomInfo.isDm
}
