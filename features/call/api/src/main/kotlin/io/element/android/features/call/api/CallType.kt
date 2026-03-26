/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.api

import android.os.Parcelable
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.parcelize.Parcelize

/**
 * 通话类型密封接口
 *
 * 定义了 Element Call 支持的通话类型，用于区分外部 URL 通话和房间内通话。
 * 该接口继承 NodeInputs 和 Parcelable，用于在 Activity 之间传递通话参数。
 *
 * @see ExternalUrl 外部 URL 通话类型
 * @see RoomCall 房间内通话类型
 */
sealed interface CallType : NodeInputs, Parcelable {
    /**
     * 外部 URL 通话类型
     *
     * 用于通过外部 Element Call URL 发起通话的情况。
     *
     * @property url 外部 Element Call 的 URL 地址
     */
    @Parcelize
    data class ExternalUrl(val url: String) : CallType {
        override fun toString(): String {
            return "ExternalUrl"
        }
    }

    /**
     * 房间内通话类型
     *
     * 用于在 Matrix 房间内发起的通话。
     *
     * @property sessionId 会话 ID，表示当前用户
     * @property roomId 房间 ID，表示要通话的房间
     */
    @Parcelize
    data class RoomCall(
        val sessionId: SessionId,
        val roomId: RoomId,
    ) : CallType {
        override fun toString(): String {
            return "RoomCall(sessionId=$sessionId, roomId=$roomId)"
        }
    }
}
