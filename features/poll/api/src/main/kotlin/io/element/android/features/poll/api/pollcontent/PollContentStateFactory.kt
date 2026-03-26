/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.pollcontent

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.PollContent

/**
 * 投票内容状态工厂接口
 *
 * 负责将底层的投票内容数据转换为 UI 状态。
 * 提供了两种创建状态的方法：
 * 1. 从时间线事件项创建（包含更多上下文信息）
 * 2. 从基本参数创建（需要手动传入事件 ID、可编辑性、所有者信息）
 */
interface PollContentStateFactory {
    /**
     * 从时间线事件项创建投票内容状态
     *
     * @param eventTimelineItem 时间线事件项
     * @param content 投票内容
     * @return PollContentState 投票内容状态
     */
    suspend fun create(eventTimelineItem: EventTimelineItem, content: PollContent): PollContentState {
        return create(
            eventId = eventTimelineItem.eventId,
            isEditable = eventTimelineItem.isEditable,
            isOwn = eventTimelineItem.isOwn,
            content = content,
        )
    }

    /**
     * 从基本参数创建投票内容状态
     *
     * @param eventId 投票事件 ID
     * @param isEditable 投票是否可编辑
     * @param isOwn 投票是否由当前用户创建
     * @param content 投票内容
     * @return PollContentState 投票内容状态
     */
    suspend fun create(eventId: EventId?, isEditable: Boolean, isOwn: Boolean, content: PollContent): PollContentState
}
