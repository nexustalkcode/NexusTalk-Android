/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist

import io.element.android.features.messages.impl.UserEventPermissions
import io.element.android.features.messages.impl.timeline.model.TimelineItem

/**
 * 动作列表事件密封接口
 *
 * 定义消息动作列表中的各种事件类型。
 * 包括清除动作列表和为消息计算可用动作。
 *
 * @see TimelineItem.Event 时间线事件
 * @see UserEventPermissions 用户事件权限
 */
sealed interface ActionListEvents {
    /**
     * 清除动作列表事件
     *
     * 当用户关闭动作列表或完成某个动作后触发，
     * 用于重置动作列表状态到初始状态。
     */
    data object Clear : ActionListEvents

    /**
     * 为消息计算可用动作事件
     *
     * 当用户点击消息以显示动作列表时触发，
     * 根据消息内容和用户权限计算可用的动作选项。
     *
     * @property event 时间线事件，包含消息的详细信息
     * @property userEventPermissions 用户对事件的操作权限
     */
    data class ComputeForMessage(
        val event: TimelineItem.Event,
        val userEventPermissions: UserEventPermissions,
    ) : ActionListEvents
}
