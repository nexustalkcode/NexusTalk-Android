/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist

import androidx.compose.runtime.Immutable
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import kotlinx.collections.immutable.ImmutableList

/**
 * 操作列表状态数据类
 *
 * 描述动作列表UI的当前状态，包含目标消息和可用动作信息。
 * 使用状态模式来处理动作列表的不同阶段：初始、加载中、成功。
 *
 * @property target 动作列表的目标状态，指示当前是隐藏、加载中还是显示成功
 * @property eventSink 事件处理函数，用于将用户操作转换为事件
 *
 * @see Target 动作列表的目标状态密封接口
 * @see ActionListEvents 动作列表事件
 */
data class ActionListState(
    /**
     * 动作列表的目标状态
     *
     * - [Target.None]: 动作列表隐藏，没有选中任何消息
     * - [Target.Loading]: 正在计算可用动作，显示加载状态
     * - [Target.Success]: 成功计算出可用动作，显示动作列表
     */
    val target: Target,
    /**
     * 事件处理函数
     *
     * 接收 [ActionListEvents] 事件并触发相应的业务逻辑处理。
     * 例如：发送清除事件或计算消息动作事件。
     */
    val eventSink: (ActionListEvents) -> Unit,
) {
    /**
     * 动作列表目标状态密封接口
     *
     * 使用密封类来确保状态的安全性，
     * 只有明确定义的状态类型才能出现。
     */
    @Immutable
    sealed interface Target {
        /**
         * 无目标状态
         *
         * 动作列表隐藏，不显示任何内容。
         * 这是初始状态或用户关闭动作列表后的状态。
         */
        data object None : Target

        /**
         * 加载中状态
         *
         * 当用户点击消息后，正在计算可用动作的中间状态。
         * 包含被点击的消息事件信息。
         *
         * @property event 正在计算动作的目标消息事件
         */
        data class Loading(val event: TimelineItem.Event) : Target

        /**
         * 成功状态
         *
         * 动作已计算完成，可以显示完整的动作列表。
         * 包含消息详情、可用动作、表情反应和发送失败信息。
         *
         * @property event 目标消息事件
         * @property sentTimeFull 完整格式的发送时间显示文本
         * @property displayEmojiReactions 是否显示表情反应功能
         * @property recentEmojis 最近使用的表情符号列表
         * @property verifiedUserSendFailure 已验证用户发送失败状态
         * @property actions 可用的动作列表
         */
        data class Success(
            val event: TimelineItem.Event,
            val sentTimeFull: String,
            val displayEmojiReactions: Boolean,
            val recentEmojis: ImmutableList<String>,
            val verifiedUserSendFailure: VerifiedUserSendFailure,
            val actions: ImmutableList<TimelineItemAction>,
        ) : Target
    }
}
