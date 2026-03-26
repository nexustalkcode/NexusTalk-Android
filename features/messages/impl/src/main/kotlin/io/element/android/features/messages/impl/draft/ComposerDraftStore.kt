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
 * 消息编辑器草稿存储接口
 *
 * 定义消息编辑器草稿的本地存储操作接口。
 * 用于持久化保存消息草稿数据。
 *
 * @see RoomId 房间ID
 * @see ThreadId 线程ID
 * @see ComposerDraft 消息编辑器草稿
 */
interface ComposerDraftStore {
    /**
     * 加载消息草稿
     *
     * @param roomId 房间ID
     * @param threadRoot 线程根ID（可选）
     * @return 消息草稿内容，如果没有草稿则返回 null
     */
    suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?): ComposerDraft?

    /**
     * 更新消息草稿
     *
     * @param roomId 房间ID
     * @param threadRoot 线程根ID（可选）
     * @param draft 消息草稿内容
     */
    suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?)
}
