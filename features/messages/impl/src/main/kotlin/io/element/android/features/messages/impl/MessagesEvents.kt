/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.EventOrTransactionId
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 消息页面事件密封接口
 *
 * 定义消息页面可能发生的用户交互事件。
 */
sealed interface MessagesEvents {
    /**
     * 处理操作事件
     *
     * @property action 操作类型
     * @property event 目标事件
     */
    data class HandleAction(val action: TimelineItemAction, val event: TimelineItem.Event) : MessagesEvents
    /**
     * 切换反应事件
     *
     * @property emoji 表情符号
     * @property eventOrTransactionId 事件或事务 ID
     */
    data class ToggleReaction(val emoji: String, val eventOrTransactionId: EventOrTransactionId) : MessagesEvents
    /**
     * 邀请对话框关闭事件
     *
     * @property action 关闭操作
     */
    data class InviteDialogDismissed(val action: InviteDialogAction) : MessagesEvents
    /**
     * 用户点击事件
     *
     * @property user 被点击的用户
     */
    data class OnUserClicked(val user: MatrixUser) : MessagesEvents
    /** 关闭操作列表 */
    data object Dismiss : MessagesEvents
    /** 标记为完全已读并退出 */
    data object MarkAsFullyReadAndExit : MessagesEvents
}

/**
 * 邀请对话框操作枚举
 */
enum class InviteDialogAction {
    /** 取消 */
    Cancel,
    /** 邀请 */
    Invite,
}
