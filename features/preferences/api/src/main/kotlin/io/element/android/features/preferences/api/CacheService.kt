/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.api

import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.Flow

/**
 * 缓存服务接口
 *
 * 提供缓存相关的服务，用于通知应用缓存已被清除。
 */
interface CacheService {
    /**
     * 缓存清除事件流
     *
     * 一个 [SessionId] 的 Flow，用于通知应用某个会话的缓存已被清除，
     * 例如可以用于重启应用。
     */
    val clearedCacheEventFlow: Flow<SessionId>
}
