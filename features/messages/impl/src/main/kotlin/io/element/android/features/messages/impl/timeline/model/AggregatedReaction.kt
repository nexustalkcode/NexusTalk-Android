/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import io.element.android.libraries.core.extensions.ellipsize
import io.element.android.libraries.matrix.api.core.UserId
import kotlinx.collections.immutable.ImmutableList

/**
 * 聚合反应的最大显示字符数
 *
 * 反应可以是自由文本，因此需要限制屏幕上显示的长度。
 */
private const val MAX_DISPLAY_CHARS = 16

/**
 * 聚合反应数据类
 *
 * 表示一个聚合后的消息反应，包含反应内容和发送者信息。
 * 用于在时间线中显示消息的反应。
 *
 * @property currentUserId 当前登录用户的ID
 * @property key 完整的反应键（如 "👍", "YES!"）
 * @property senders 发送此反应的用户列表
 */
data class AggregatedReaction(
    val currentUserId: UserId,
    val key: String,
    val senders: ImmutableList<AggregatedReactionSender>
) {
    /**
     * The key to be displayed on screen.
     *
     * See [MAX_DISPLAY_CHARS].
     */
    val displayKey: String = key.ellipsize(MAX_DISPLAY_CHARS)

    /**
     * The number of users who reacted with this key.
     */
    val count: Int = senders.count()

    /**
     * True if the reaction has (also) been sent by the current user.
     */
    val isHighlighted: Boolean = senders.any { it.senderId == currentUserId }
}
