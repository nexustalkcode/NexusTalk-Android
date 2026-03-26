/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.api

import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 当前通话状态密封接口
 *
 * 表示本地当前的通话状态，用于在应用内追踪通话状态。
 * 注意：此值反映本地通话状态，如果用户在另一个会话中接听通话，此值不会更新。
 *
 * @see None 无通话状态
 * @see RoomCall 房间内通话状态
 * @see ExternalUrl 外部 URL 通话状态
 */
sealed interface CurrentCall {
    /**
     * 无通话状态
     *
     * 表示当前没有正在进行或等待接听的通话。
     */
    data object None : CurrentCall

    /**
     * 房间内通话状态
     *
     * 表示当前正在进行或等待接听的房间内通话。
     *
     * @property roomId 房间 ID，表示通话所在的房间
     */
    data class RoomCall(
        val roomId: RoomId,
    ) : CurrentCall

    /**
     * 外部 URL 通话状态
     *
     * 表示当前正在进行或等待接听的外部 URL 通话。
     *
     * @property url 外部 Element Call 的 URL 地址
     */
    data class ExternalUrl(
        val url: String,
    ) : CurrentCall
}
