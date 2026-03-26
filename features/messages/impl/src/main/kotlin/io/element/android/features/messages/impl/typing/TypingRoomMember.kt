/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.typing

/**
 * 正在打字的房间成员数据类
 *
 * 表示当前正在房间中输入消息的成员。
 *
 * @property disambiguatedDisplayName 用于区分相同名称成员的显示名称
 *                                 如果有重名用户，会包含用户ID来区分
 */
data class TypingRoomMember(
    val disambiguatedDisplayName: String,
)
