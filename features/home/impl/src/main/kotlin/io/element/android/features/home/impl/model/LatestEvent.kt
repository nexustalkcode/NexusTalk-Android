/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable

/**
 * 最新事件密封接口
 *
 * 表示房间的最新消息事件状态，用于在房间列表中显示消息预览。
 */
@Immutable
sealed interface LatestEvent {
    /** 无事件状态 */
    data object None : LatestEvent

    /**
     * 已同步事件
     *
     * @property content 消息内容
     */
    data class Synced(
        val content: CharSequence?,
    ) : LatestEvent

    /**
     * 发送中事件
     *
     * @property content 消息内容
     */
    data class Sending(
        val content: CharSequence?,
    ) : LatestEvent

    /** 发送错误事件 */
    data object Error : LatestEvent

    /**
     * 获取事件内容
     *
     * @return 消息内容，如果没有则返回 null
     */
    fun content(): CharSequence? {
        return when (this) {
            is None -> null
            is Synced -> content
            is Sending -> content
            is Error -> null
        }
    }
}
