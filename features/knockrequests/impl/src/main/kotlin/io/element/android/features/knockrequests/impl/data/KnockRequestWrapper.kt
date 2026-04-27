/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.data

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.knock.KnockRequest

/**
 * 对底层 [KnockRequest] 的 UI 友好包装。
 *
 * 除了暴露展示字段外，也统一提供接受、拒绝、封禁和标记已读等动作。
 */
class KnockRequestWrapper(
    private val inner: KnockRequest,
    dateFormatter: (Long?) -> String? = { null }
) : KnockRequestPresentable {
    override val eventId: EventId = inner.eventId
    override val userId: UserId = inner.userId
    override val displayName: String? = inner.displayName
    override val avatarUrl: String? = inner.avatarUrl
    override val reason: String? = inner.reason?.trim()
    override val formattedDate: String? = dateFormatter(inner.timestamp)

    val isSeen: Boolean = inner.isSeen

    /** 接受当前敲门请求。 */
    suspend fun accept(): Result<Unit> = inner.accept()

    /** 拒绝当前敲门请求。 */
    suspend fun decline(reason: String?): Result<Unit> = inner.decline(reason)

    /** 拒绝当前敲门请求并封禁对应用户。 */
    suspend fun declineAndBan(reason: String?): Result<Unit> = inner.declineAndBan(reason)

    /** 将当前敲门请求标记为已读。 */
    suspend fun markAsSeen(): Result<Unit> = inner.markAsSeen()
}
