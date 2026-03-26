/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.draft

import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.room.draft.ComposerDraft

/**
 * 消息编辑器草稿服务接口
 *
 * 定义消息编辑器草稿的加载和更新操作接口。
 * 用于在用户编写消息时保存和恢复草稿内容。
 *
 * @see RoomId 房间ID
 * @see ThreadId 线程ID
 * @see ComposerDraft 消息编辑器草稿
 */
interface ComposerDraftService {
    /**
     * 加载消息草稿
     *
     * @param roomId 房间ID
     * @param threadRoot 线程根ID（可选）
     * @param isVolatile 是否为临时草稿
     * @return 消息草稿内容，如果没有草稿则返回 null
     */
    suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?, isVolatile: Boolean): ComposerDraft?

    /**
     * 更新消息草稿
     *
     * @param roomId 房间ID
     * @param threadRoot 线程根ID（可选）
     * @param draft 消息草稿内容
     * @param isVolatile 是否为临时草稿
     */
    suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?, isVolatile: Boolean)
}
