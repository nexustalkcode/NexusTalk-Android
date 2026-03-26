/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt

/**
 * 通知配置 (Notification Configuration)
 *
 * 此对象包含应用通知功能相关的配置项。
 * 控制通知栏中显示的操作按钮、颜色等视觉和行为设置。
 */
object NotificationConfig {
    /**
     * 是否在通知中显示"标记为已读"操作按钮。
     * 启用后，用户可以在不打开应用的情况下直接标记消息为已读状态。
     */
    const val SHOW_MARK_AS_READ_ACTION = true

    /**
     * 是否在群聊邀请通知中显示"接受"和"拒绝"操作按钮。
     * 启用后，用户可以直接从通知中接受或拒绝群聊邀请，无需打开应用。
     */
    const val SHOW_ACCEPT_AND_DECLINE_INVITE_ACTIONS = true

    /**
     * 是否在通知中显示"快速回复"操作按钮。
     * 启用后，用户可以直接在通知中输入并发送消息，快速回复发送者。
     */
    const val SHOW_QUICK_REPLY_ACTION = true

    /** 通知的主题颜色（ARGB格式的整数值）。用于通知图标和UI元素的着色 */
    @ColorInt
    val NOTIFICATION_ACCENT_COLOR: Int = "#FF0DBD8B".toColorInt()
}
