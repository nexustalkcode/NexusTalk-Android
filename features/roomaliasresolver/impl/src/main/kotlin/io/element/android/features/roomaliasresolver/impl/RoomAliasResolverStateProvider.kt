/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.exception.ClientException
import io.element.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

/**
 * 房间别名解析器状态提供者
 *
 * 该类用于为 Compose 预览功能提供示例状态数据。
 * 继承自 PreviewParameterProvider，提供不同状态的序列供预览使用。
 *
 * @see RoomAliasResolverState 状态数据类
 * @see RoomAliasResolverView 预览目标
 */
open class RoomAliasResolverStateProvider : PreviewParameterProvider<RoomAliasResolverState> {
    /**
     * 提供多种状态序列用于预览
     *
     * 包含三种状态：
     * 1. 未初始化状态（默认）
     * 2. 通用错误状态
     * 3. 未知别名错误状态
     */
    override val values: Sequence<RoomAliasResolverState>
        get() = sequenceOf(
            // 默认未初始化状态
            aRoomAliasResolverState(),
            // 通用错误状态
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(ClientException.Generic("Something went wrong", null)),
            ),
            // 未知别名错误状态
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(RoomAliasResolverFailures.UnknownAlias),
            ),
        )
}

/**
 * 创建测试用的房间别名解析器状态
 *
 * 便捷函数，用于快速创建不同配置的测试状态。
 *
 * @param roomAlias 房间别名，默认为测试用别名
 * @param resolveState 解析状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return 配置好的 RoomAliasResolverState 实例
 */
fun aRoomAliasResolverState(
    roomAlias: RoomAlias = A_ROOM_ALIAS,
    resolveState: AsyncData<ResolvedRoomAlias> = AsyncData.Uninitialized,
    eventSink: (RoomAliasResolverEvents) -> Unit = {}
) = RoomAliasResolverState(
    roomAlias = roomAlias,
    resolveState = resolveState,
    eventSink = eventSink,
)

/** 测试用房间别名常量 */
private val A_ROOM_ALIAS = RoomAlias("#exa:matrix.org")
