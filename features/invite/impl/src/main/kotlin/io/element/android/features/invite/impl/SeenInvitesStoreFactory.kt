/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl

import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope

/**
 * 已查看邀请存储工厂接口
 *
 * 定义了创建 SeenInvitesStore 实例的工厂方法。
 * 用于为每个会话创建或获取已查看邀请存储实例。
 */
interface SeenInvitesStoreFactory {
    /**
     * 获取或创建已查看邀请存储
     *
     * @param sessionId 会话 ID
     * @param sessionCoroutineScope 会话协程作用域
     * @return SeenInvitesStore 实例
     */
    fun getOrCreate(
        sessionId: SessionId,
        sessionCoroutineScope: CoroutineScope,
    ): SeenInvitesStore
}
