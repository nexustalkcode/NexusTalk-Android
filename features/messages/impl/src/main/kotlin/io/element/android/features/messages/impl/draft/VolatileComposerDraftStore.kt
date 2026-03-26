/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.draft

import dev.zacsweers.metro.Inject
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.room.draft.ComposerDraft

/**
 * 临时消息编辑器草稿存储实现类
 *
 * 实现 ComposerDraftStore 接口，仅在内存中保存草稿。
 * 草稿内容不会跨应用重启持久保存。
 * 当前主要用于切换到编辑模式时临时存储消息草稿。
 *
 * @see ComposerDraftStore 草稿存储接口
 * @see ComposerDraft 消息编辑器草稿
 */
@Inject
class VolatileComposerDraftStore : ComposerDraftStore {
    private val drafts: MutableMap<String, ComposerDraft> = mutableMapOf()

    override suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?): ComposerDraft? {
        val key = threadRoot?.value ?: roomId.value
        // Remove the draft from the map when it is loaded
        return drafts.remove(key)
    }

    override suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?) {
        val key = threadRoot?.value ?: roomId.value
        if (draft == null) {
            drafts.remove(key)
        } else {
            drafts[key] = draft
        }
    }
}
