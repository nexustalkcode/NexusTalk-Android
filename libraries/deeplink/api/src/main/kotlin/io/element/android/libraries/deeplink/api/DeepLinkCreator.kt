/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.deeplink.api

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.ThreadId

/**
 * 应用内部 deeplink 创建接口。
 */
fun interface DeepLinkCreator {
    /**
     * 创建指向指定会话/房间/线程/事件的 deeplink。
     */
    fun create(sessionId: SessionId, roomId: RoomId?, threadId: ThreadId?, eventId: EventId?): String
}
