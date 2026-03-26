/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.impl.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 房间目录状态提供器
 *
 * 用于在预览模式下提供示例房间目录状态数据。
 * 继承自 Compose 的 PreviewParameterProvider，用于 UI 预览功能。
 */
open class RoomDirectoryStateProvider : PreviewParameterProvider<RoomDirectoryState> {
    override val values: Sequence<RoomDirectoryState>
        get() = sequenceOf(
            aRoomDirectoryState(),
            aRoomDirectoryState(
                query = "Element",
                roomDescriptions = aRoomDescriptionList(),
            ),
            aRoomDirectoryState(
                query = "Element",
                roomDescriptions = aRoomDescriptionList(),
                displayLoadMoreIndicator = true,
            ),
        )
}

/**
 * 创建示例房间目录状态
 *
 * 用于测试和预览功能的辅助函数。
 *
 * @param query 搜索关键词，默认为空字符串
 * @param displayLoadMoreIndicator 是否显示加载更多指示器，默认为 false
 * @param roomDescriptions 房间描述列表，默认为空列表
 * @param eventSink 事件处理函数，默认为空函数
 * @return RoomDirectoryState 创建的房间目录状态对象
 */
fun aRoomDirectoryState(
    query: String = "",
    displayLoadMoreIndicator: Boolean = false,
    roomDescriptions: ImmutableList<RoomDescription> = persistentListOf(),
    eventSink: (RoomDirectoryEvents) -> Unit = {},
) = RoomDirectoryState(
    query = query,
    roomDescriptions = roomDescriptions,
    displayLoadMoreIndicator = displayLoadMoreIndicator,
    eventSink = eventSink,
)

/**
 * 创建示例房间描述列表
 *
 * 用于测试和预览功能的辅助函数，返回包含示例房间的列表。
 *
 * @return ImmutableList<RoomDescription> 示例房间描述列表
 */
fun aRoomDescriptionList(): ImmutableList<RoomDescription> {
    return persistentListOf(
        RoomDescription(
            roomId = RoomId("!exa:matrix.org"),
            name = "Element X Android",
            topic = "Element X is a secure, private and decentralized messenger.",
            alias = RoomAlias("#element-x-android:matrix.org"),
            avatarUrl = null,
            joinRule = RoomDescription.JoinRule.PUBLIC,
            numberOfMembers = 2765,
        ),
        RoomDescription(
            roomId = RoomId("!exi:matrix.org"),
            name = "Element X iOS",
            topic = "Element X is a secure, private and decentralized messenger.",
            alias = RoomAlias("#element-x-ios:matrix.org"),
            avatarUrl = null,
            joinRule = RoomDescription.JoinRule.UNKNOWN,
            numberOfMembers = 356,
        )
    )
}
