/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.draft

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.room.draft.ComposerDraft

/**
 * 默认消息编辑器草稿服务实现类
 *
 * 实现 ComposerDraftService 接口，根据参数选择使用临时草稿存储或Matrix草稿存储。
 * 使用 @ContributesBinding 注解绑定到 RoomScope。
 *
 * @property volatileComposerDraftStore 临时草稿存储
 * @property matrixComposerDraftStore Matrix草稿存储
 *
 * @see ComposerDraftService 消息编辑器草稿服务接口
 * @see VolatileComposerDraftStore 临时草稿存储
 * @see MatrixComposerDraftStore Matrix草稿存储
 */
@ContributesBinding(RoomScope::class)
class DefaultComposerDraftService(
    private val volatileComposerDraftStore: VolatileComposerDraftStore,
    private val matrixComposerDraftStore: MatrixComposerDraftStore,
) : ComposerDraftService {
    override suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?, isVolatile: Boolean): ComposerDraft? {
        return getStore(isVolatile).loadDraft(roomId, threadRoot)
    }

    override suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?, isVolatile: Boolean) {
        getStore(isVolatile).updateDraft(roomId, threadRoot, draft)
    }

    /**
     * 获取草稿存储实例
     *
     * 根据 isVolatile 参数选择使用临时草稿存储或Matrix草稿存储。
     *
     * @param isVolatile 是否使用临时存储
     * @return 对应的草稿存储实例
     */
    private fun getStore(isVolatile: Boolean): ComposerDraftStore {
        return if (isVolatile) {
            volatileComposerDraftStore
        } else {
            matrixComposerDraftStore
        }
    }
}
