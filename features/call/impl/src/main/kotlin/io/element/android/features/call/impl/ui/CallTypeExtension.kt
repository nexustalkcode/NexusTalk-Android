/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.features.call.api.CallType
import io.element.android.libraries.matrix.api.core.SessionId

/**
 * CallType 扩展函数
 *
 * 提供 CallType 相关的扩展功能。
 */

/**
 * 获取通话类型的会话 ID
 *
 * 如果是房间通话，返回对应的会话 ID；如果是外部 URL 通话，返回 null。
 *
 * @return SessionId? 房间通话返回会话 ID，外部 URL 通话返回 null
 *
 * @see CallType 通话类型
 * @see CallType.RoomCall 房间通话类型
 * @see CallType.ExternalUrl 外部 URL 通话类型
 */
fun CallType.getSessionId(): SessionId? {
    return when (this) {
        is CallType.ExternalUrl -> null
        is CallType.RoomCall -> sessionId
    }
}
