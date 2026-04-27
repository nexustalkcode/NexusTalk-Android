/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.data

import androidx.compose.runtime.Immutable
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.UserId

@Immutable
/**
 * 供 UI 展示的敲门请求抽象。
 */
interface KnockRequestPresentable {
    val eventId: EventId
    val userId: UserId
    val displayName: String?
    val avatarUrl: String?
    val reason: String?
    val formattedDate: String?

    /**
     * 构造当前敲门请求的头像数据。
     */
    fun getAvatarData(size: AvatarSize) = AvatarData(
        id = userId.value,
        name = displayName,
        url = avatarUrl,
        size = size,
    )

    /**
     * 返回优先用于展示的人类可读名称。
     */
    fun getBestName(): String {
        return displayName?.takeIf { it.isNotEmpty() } ?: userId.value
    }
}
