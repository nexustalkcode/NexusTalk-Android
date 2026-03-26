/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.features.preferences.api.CacheService
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 默认缓存服务实现
 *
 * 提供缓存清除事件的发布功能，当缓存被清除时，会向 clearedCacheEventFlow 发送对应的会话 ID，
 * 以便应用可以据此执行相应操作（如重启应用）。
 *
 * @see CacheService 缓存服务接口
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultCacheService : CacheService {
    /** 缓存清除事件流，用于通知缓存已被清除 */
    private val _clearedCacheEventFlow = MutableSharedFlow<SessionId>(0)
    override val clearedCacheEventFlow: Flow<SessionId> = _clearedCacheEventFlow

    /**
     * 当缓存被清除时触发
     *
     * @param sessionId 被清除缓存的会话 ID
     */
    suspend fun onClearedCache(sessionId: SessionId) {
        _clearedCacheEventFlow.emit(sessionId)
    }
}
