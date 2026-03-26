/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 社区列表上下文菜单
 *
 * 渲染社区列表中房间项的上下文菜单，提供标记已读/未读、收藏、设置、离开等操作。
 *
 * @param contextMenu 上下文菜单显示状态
 * @param canReportRoom 是否可以报告房间
 * @param eventSink 事件处理函数
 * @param onRoomSettingsClick 房间设置点击事件
 * @param onReportRoomClick 报告房间点击事件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListContextMenu(
    contextMenu: GroupListState.ContextMenu.Shown,
    canReportRoom: Boolean,
    eventSink: (GroupListEvents.ContextMenuEvents) -> Unit,
    onRoomSettingsClick: (roomId: RoomId) -> Unit,
    onReportRoomClick: (roomId: RoomId) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { eventSink(GroupListEvents.HideContextMenu) },
    ) {
        GroupListModalBottomSheetContent(
            contextMenu = contextMenu,
            canReportRoom = canReportRoom,
            onRoomMarkReadClick = {
                eventSink(GroupListEvents.HideContextMenu)
                eventSink(GroupListEvents.MarkAsRead(contextMenu.roomId))
            },
            onRoomMarkUnreadClick = {
                eventSink(GroupListEvents.HideContextMenu)
                eventSink(GroupListEvents.MarkAsUnread(contextMenu.roomId))
            },
            onRoomSettingsClick = {
                eventSink(GroupListEvents.HideContextMenu)
                onRoomSettingsClick(contextMenu.roomId)
            },
            onLeaveRoomClick = {
                eventSink(GroupListEvents.HideContextMenu)
                eventSink(GroupListEvents.LeaveRoom(contextMenu.roomId, needsConfirmation = true))
            },
            onFavoriteChange = { isFavorite ->
                eventSink(GroupListEvents.SetRoomIsFavorite(contextMenu.roomId, isFavorite))
            },
            onClearCacheRoomClick = {
                eventSink(GroupListEvents.HideContextMenu)
                eventSink(GroupListEvents.ClearCacheOfRoom(contextMenu.roomId))
            },
            onReportRoomClick = {
                eventSink(GroupListEvents.HideContextMenu)
                onReportRoomClick(contextMenu.roomId)
            },
        )
    }
}

/**
 * 社区列表模态底部菜单内容
 *
 * 渲染社区列表上下文菜单的具体内容，包括房间名称和操作列表。
 *
 * @param contextMenu 上下文菜单显示状态
 * @param canReportRoom 是否可以报告房间
 * @param onRoomSettingsClick 房间设置点击事件
 * @param onLeaveRoomClick 离开房间点击事件
 * @param onFavoriteChange 收藏变更事件
 * @param onRoomMarkReadClick 标记已读点击事件
 * @param onRoomMarkUnreadClick 标记未读点击事件
 * @param onClearCacheRoomClick 清除缓存点击事件
 * @param onReportRoomClick 报告房间点击事件
 */
@Composable
private fun GroupListModalBottomSheetContent(
    contextMenu: GroupListState.ContextMenu.Shown,
    canReportRoom: Boolean,
    onRoomSettingsClick: () -> Unit,
    onLeaveRoomClick: () -> Unit,
    onFavoriteChange: (isFavorite: Boolean) -> Unit,
    onRoomMarkReadClick: () -> Unit,
    onRoomMarkUnreadClick: () -> Unit,
    onClearCacheRoomClick: () -> Unit,
    onReportRoomClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = contextMenu.roomName ?: stringResource(id = CommonStrings.common_no_room_name),
                    style = ElementTheme.typography.fontBodyLgMedium,
                    fontStyle = FontStyle.Italic.takeIf { contextMenu.roomName == null }
                )
            }
        )
        if (contextMenu.hasNewContent) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.screen_roomlist_mark_as_read),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                onClick = onRoomMarkReadClick,
                leadingContent = ListItemContent.Icon(
                    iconSource = IconSource.Vector(CompoundIcons.MarkAsRead())
                ),
                style = ListItemStyle.Primary,
            )
        } else {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.screen_roomlist_mark_as_unread),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                onClick = onRoomMarkUnreadClick,
                leadingContent = ListItemContent.Icon(
                    iconSource = IconSource.Vector(CompoundIcons.MarkAsUnread())
                ),
                style = ListItemStyle.Primary,
            )
        }
        ListItem(
            headlineContent = {
                Text(
                    text = if (contextMenu.isFavorite) stringResource(id = CommonStrings.action_unpin) else stringResource(id = CommonStrings.action_pin),
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            leadingContent = ListItemContent.Icon(
                iconSource = IconSource.Vector(
                    CompoundIcons.Pin(),
                )
            ),
            trailingContent = ListItemContent.Switch(
                checked = contextMenu.isFavorite,
            ),
            onClick = {
                onFavoriteChange(!contextMenu.isFavorite)
            },
            style = ListItemStyle.Primary,
        )
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = CommonStrings.common_settings),
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            modifier = Modifier.clickable { onRoomSettingsClick() },
            leadingContent = ListItemContent.Icon(
                iconSource = IconSource.Vector(
                    CompoundIcons.Settings(),
                )
            ),
            style = ListItemStyle.Primary,
        )
        if (canReportRoom) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(CommonStrings.action_report_room))
                },
                modifier = Modifier.clickable { onReportRoomClick() },
                leadingContent = ListItemContent.Icon(
                    iconSource = IconSource.Vector(
                        CompoundIcons.ChatProblem(),
                    )
                ),
                style = ListItemStyle.Destructive,
            )
        }
        ListItem(
            headlineContent = {
                Text(text = stringResource(CommonStrings.action_leave_room))
            },
            modifier = Modifier.clickable { onLeaveRoomClick() },
            leadingContent = ListItemContent.Icon(
                iconSource = IconSource.Vector(
                    CompoundIcons.Leave(),
                )
            ),
            style = ListItemStyle.Destructive,
        )
        if (contextMenu.displayClearRoomCacheAction) {
            ListItem(
                headlineContent = {
                    Text(text = "Clear cache for this room")
                },
                modifier = Modifier.clickable { onClearCacheRoomClick() },
                leadingContent = ListItemContent.Icon(
                    iconSource = IconSource.Vector(CompoundIcons.Delete())
                ),
                style = ListItemStyle.Primary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun GroupListModalBottomSheetContentPreview(
    @PreviewParameter(GroupListStateContextMenuShownProvider::class) contextMenu: GroupListState.ContextMenu.Shown
) = ElementPreview {
    GroupListModalBottomSheetContent(
        contextMenu = contextMenu,
        canReportRoom = true,
        onRoomMarkReadClick = {},
        onRoomMarkUnreadClick = {},
        onRoomSettingsClick = {},
        onLeaveRoomClick = {},
        onFavoriteChange = {},
        onClearCacheRoomClick = {},
        onReportRoomClick = {},
    )
}
