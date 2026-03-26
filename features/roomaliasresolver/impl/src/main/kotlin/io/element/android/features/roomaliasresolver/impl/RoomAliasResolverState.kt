/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

/**
 * 房间别名解析器状态数据类
 *
 * 表示解析房间别名界面的当前状态。
 *
 * @property roomAlias 要解析的房间别名
 * @property resolveState 解析操作的异步状态
 * @property eventSink 事件处理函数
 */
data class RoomAliasResolverState(
    val roomAlias: RoomAlias,
    val resolveState: AsyncData<ResolvedRoomAlias>,
    val eventSink: (RoomAliasResolverEvents) -> Unit
)

/**
 * 房间别名解析器失败异常类
 *
 * 定义别名解析过程中可能出现的失败情况。
 */
sealed class RoomAliasResolverFailures : Exception() {
    /** 未知的别名 */
    data object UnknownAlias : RoomAliasResolverFailures()
}
