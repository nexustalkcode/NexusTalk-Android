/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import io.element.android.libraries.matrix.ui.room.RoomMemberIdentityStateChange
import kotlinx.collections.immutable.ImmutableList

/**
 * 身份更改状态数据类
 *
 * @property roomMemberIdentityStateChanges 房间成员身份状态更改列表
 * @property eventSink 事件处理函数
 */
data class IdentityChangeState(
    val roomMemberIdentityStateChanges: ImmutableList<RoomMemberIdentityStateChange>,
    val eventSink: (IdentityChangeEvent) -> Unit,
)
