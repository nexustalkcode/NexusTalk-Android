/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.typing

import kotlinx.collections.immutable.ImmutableList

/**
 * 打字通知状态数据类
 *
 * @property renderTypingNotifications 是否根据用户偏好显示打字通知
 * @property typingMembers 当前正在打字的房间成员列表
 * @property reserveSpace 是否在时间线底部为打字通知保留空间
 */
data class TypingNotificationState(
    /** 是否根据用户偏好显示打字通知 */
    val renderTypingNotifications: Boolean,
    /** 当前正在打字的房间成员列表 */
    val typingMembers: ImmutableList<TypingRoomMember>,
    /** 是否在时间线底部为打字通知保留空间 */
    val reserveSpace: Boolean,
)
