/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 社区列表上下文菜单显示状态提供者
 *
 * 为预览和测试提供 GroupListState.ContextMenu.Shown 示例数据。
 *
 * @see GroupListState.ContextMenu.Shown 上下文菜单显示状态
 */
open class GroupListStateContextMenuShownProvider : PreviewParameterProvider<GroupListState.ContextMenu.Shown> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<GroupListState.ContextMenu.Shown>
        get() = sequenceOf(
            aGroupMenuShown(hasNewContent = true),
            aGroupMenuShown(isDm = true),
            aGroupMenuShown(roomName = null)
        )
}

/**
 * 创建示例社区列表上下文菜单显示状态
 *
 * @param roomName 房间名称
 * @param isDm 是否为直接消息
 * @param hasNewContent 是否有新内容
 * @param isFavorite 是否为收藏
 * @return 上下文菜单显示状态
 */
internal fun aGroupMenuShown(
    roomName: String? = "aRoom",
    isDm: Boolean = false,
    hasNewContent: Boolean = false,
    isFavorite: Boolean = false,
) = GroupListState.ContextMenu.Shown(
    roomId = RoomId("!aRoom:aDomain"),
    roomName = roomName,
    isDm = isDm,
    hasNewContent = hasNewContent,
    isFavorite = isFavorite,
    displayClearRoomCacheAction = false,
)
