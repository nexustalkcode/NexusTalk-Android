/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.ui.text.AnnotatedString
import io.element.android.libraries.matrix.api.core.EventId

/**
 * 固定消息横幅项目数据类
 *
 * 表示一个置顶消息在横幅中显示所需的数据。
 * 包含消息的唯一标识和格式化后的内容，用于在横幅中展示。
 *
 * @property eventId 事件的唯一标识符，用于定位和引用该消息
 * @property formatted 格式化后的消息内容，使用 AnnotatedString 支持富文本显示
 *                  （可能包含加粗、链接等富文本效果）
 *
 * @see EventId 事件ID类型
 * @see AnnotatedString 可标注的字符串，用于支持富文本
 */
data class PinnedMessagesBannerItem(
    /**
     * 事件的唯一标识符
     *
     * 用于在房间时间线中唯一标识这条置顶消息，
     * 可用于导航到该消息、复制链接等操作。
     */
    val eventId: EventId,
    /**
     * 格式化后的消息内容
     *
     * 经过格式化处理的消息文本，可能包含：
     * - 发送者名称的加粗显示
     * - 消息类型的图标标注
     * - 链接的着色处理
     * - 多行文本的截断处理
     */
    val formatted: AnnotatedString,
)
