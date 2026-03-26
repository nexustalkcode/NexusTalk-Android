/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.actionlist.model.TimelineItemActionPostProcessor

/**
 * 固定消息列表时间线操作后处理器
 *
 * 处理固定消息列表中的时间线操作顺序。
 * 自定义操作列表中显示的操作及其顺序。
 * 在固定消息列表中，"查看在时间线中"操作始终显示在最前面，
 * 取消置顶操作也会被优先显示。
 *
 * @see TimelineItemActionPostProcessor 时间线操作后处理器接口
 * @see TimelineItemAction 时间线操作类型
 */
class PinnedMessagesListTimelineActionPostProcessor : TimelineItemActionPostProcessor {
    /**
     * 处理操作列表
     *
     * 重新排序操作列表，确保以下操作按顺序显示：
     * 1. 查看在时间线中 (ViewInTimeline)
     * 2. 取消置顶 (Unpin) - 如果存在
     * 3. 转发 (Forward) - 如果存在
     * 4. 查看源码 (ViewSource) - 如果存在
     *
     * @param actions 原始操作列表
     * @return List<TimelineItemAction> 处理后的操作列表
     */
    override fun process(actions: List<TimelineItemAction>): List<TimelineItemAction> {
        return buildList {
            // 首先添加"查看在时间线中"操作
            add(TimelineItemAction.ViewInTimeline)
            // 如果存在取消置顶操作，添加到列表
            actions.firstOrNull { it == TimelineItemAction.Unpin }?.let(::add)
            // 如果存在转发操作，添加到列表
            actions.firstOrNull { it == TimelineItemAction.Forward }?.let(::add)
            // 如果存在查看源码操作，添加到列表
            actions.firstOrNull { it == TimelineItemAction.ViewSource }?.let(::add)
        }
    }
}
