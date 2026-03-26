/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

/**
 * 历史可见性事件密封接口
 *
 * 定义用户与历史可见性警告交互时触发的事件类型。
 * 用于处理用户确认已了解历史可见性提醒的操作。
 *
 * @see HistoryVisibleState 历史可见性状态
 * @see HistoryVisibleStatePresenter 状态展示器
 */
sealed interface HistoryVisibleEvent {
    /**
     * 用户确认已知悉历史可见性警告
     *
     * 当用户点击"我已知悉"或"关闭"按钮时触发此事件。
     * 系统将记录用户的确认状态，以避免重复显示警告。
     *
     * @see HistoryVisibleAcknowledgementRepository 用于持久化确认状态
     */
    data object Acknowledge : HistoryVisibleEvent
}
