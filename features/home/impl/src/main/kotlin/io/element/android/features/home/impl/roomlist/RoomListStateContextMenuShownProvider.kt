/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 房间列表上下文菜单显示状态提供者
 *
 * 为预览和测试提供 RoomListState.ContextMenu.Shown 示例数据。
 *
 * @see RoomListState.ContextMenu.Shown 上下文菜单显示状态
 */
open class RoomListStateContextMenuShownProvider : PreviewParameterProvider<RoomListState.ContextMenu.Shown> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<RoomListState.ContextMenu.Shown>
        get() = sequenceOf(
            aContextMenuShown(hasNewContent = true),
            aContextMenuShown(isDm = true),
            aContextMenuShown(roomName = null)
        )
}

/**
 * 创建示例上下文菜单显示状态
 *
 * @param roomName 房间名称
 * @param isDm 是否为直接消息
 * @param hasNewContent 是否有新内容
 * @param isFavorite 是否为收藏
 * @return 上下文菜单显示状态
 */
internal fun aContextMenuShown(
    roomName: String? = "aRoom",
    isDm: Boolean = false,
    hasNewContent: Boolean = false,
    isFavorite: Boolean = false,
) = RoomListState.ContextMenu.Shown(
    roomId = RoomId("!aRoom:aDomain"),
    roomName = roomName,
    isDm = isDm,
    hasNewContent = hasNewContent,
    isFavorite = isFavorite,
    displayClearRoomCacheAction = false,
)
