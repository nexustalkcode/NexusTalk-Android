/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.R
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.grouplist.GroupListContentState
import io.element.android.features.home.impl.grouplist.GroupListEvents
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.features.home.impl.grouplist.GroupListContentStateProvider
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 社区列表内容视图
 *
 * 渲染社区列表的内容视图，根据内容状态显示骨架屏、空状态或房间列表。
 *
 * @param contentState 社区列表内容状态
 * @param lazyListState 懒加载列表状态
 * @param onRoomClick 房间点击事件
 * @param onCreateRoomClick 创建房间点击事件
 * @param contentPadding 内容内边距
 * @param eventSink 事件处理函数
 * @param modifier 修饰符
 */
@Composable
fun GroupListContentView(
    contentState: GroupListContentState,
    lazyListState: LazyListState,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
    eventSink: (GroupListEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (contentState) {
        is GroupListContentState.Skeleton -> {
            SkeletonView(
                modifier = modifier,
                count = contentState.count,
                contentPadding = contentPadding,
            )
        }
        is GroupListContentState.Empty -> {
            EmptyView(
                modifier = modifier.padding(contentPadding),
                onCreateRoomClick = onCreateRoomClick,
            )
        }
        is GroupListContentState.Rooms -> {
            RoomsView(
                modifier = modifier,
                state = contentState,
                onCreateRoomClick = onCreateRoomClick,
                onRoomClick = onRoomClick,
                lazyListState = lazyListState,
                contentPadding = contentPadding,
                eventSink = eventSink,
            )
        }
    }
}

/**
 * 骨架屏视图
 *
 * 渲染加载中的占位符列表。
 *
 * @param count 骨架项数量
 * @param contentPadding 内容内边距
 * @param modifier 修饰符
 */
@Composable
private fun SkeletonView(
    count: Int,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        repeat(count) { index ->
            item {
                RoomSummaryPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

/**
 * 空状态视图
 *
 * 渲染社区列表为空时的界面，提供创建社区按钮。
 *
 * @param onCreateRoomClick 创建房间点击事件
 * @param modifier 修饰符
 */
@Composable
private fun EmptyView(
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = CommonStrings.screen_group_roomlist_empty_no_call_title,
            subtitle = R.string.screen_community_roomlist_empty_message,
            action = {

                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(CommonStrings.action_create_community),
                    onClick = onCreateRoomClick,
                    size = ButtonSize.Medium,
                    cornerRadius = 14.dp,
                )
            },
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/**
 * 房间列表视图
 *
 * 根据是否有房间显示房间列表或空状态。
 *
 * @param state 社区列表内容状态（房间列表）
 * @param onRoomClick 房间点击事件
 * @param onCreateRoomClick 创建房间点击事件
 * @param contentPadding 内容内边距
 * @param lazyListState 懒加载列表状态
 * @param eventSink 事件处理函数
 * @param modifier 修饰符
 */
@Composable
private fun RoomsView(
    state: GroupListContentState.Rooms,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    eventSink: (GroupListEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.summaries.isEmpty()) {
        EmptyView(
            modifier = modifier.padding(contentPadding),
            onCreateRoomClick = onCreateRoomClick,
        )
    } else {
        RoomsViewList(
            state = state,
            onRoomClick = onRoomClick,
            contentPadding = contentPadding,
            lazyListState = lazyListState,
            eventSink = eventSink,
            modifier = modifier.fillMaxSize(),
        )
    }
}

/**
 * 社区头部视图
 *
 * 渲染社区列表的头部区域，显示社区数量和描述信息。
 *
 * @param communityCount 社区数量
 * @param modifier 修饰符
 */
@Composable
private fun CommunityHeaderView(
    communityCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(io.element.android.appicon.element.R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(118.dp),
            tint = Color.Unspecified,
        )
        Text(
            text = stringResource(R.string.screen_community_list_header_count, communityCount),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.screen_community_list_header_description),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun CommunityHeaderViewPreview() = ElementPreview {
    CommunityHeaderView(communityCount = 3)
}

/**
 * 房间列表视图
 *
 * 渲染社区房间列表，包含头部视图和房间摘要列表。
 *
 * @param state 社区列表内容状态（房间列表）
 * @param onRoomClick 房间点击事件
 * @param contentPadding 内容内边距
 * @param lazyListState 懒加载列表状态
 * @param eventSink 事件处理函数
 * @param modifier 修饰符
 */
@Composable
private fun RoomsViewList(
    state: GroupListContentState.Rooms,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    eventSink: (GroupListEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleRange by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val firstItemIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            val size = layoutInfo.visibleItemsInfo.size
            firstItemIndex until firstItemIndex + size
        }
    }
    val updatedOnRoomClick by rememberUpdatedState(newValue = onRoomClick)
    val updatedEventSink by rememberUpdatedState(newValue = eventSink)
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            CommunityHeaderView(communityCount = state.summaries.size)
        }
        item {
            HorizontalDivider()
        }
        itemsIndexed(
            items = state.summaries,
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            RoomSummaryRow(
                room = room,
                hideInviteAvatars = false,
                isInviteSeen = false,
                onClick = updatedOnRoomClick,
                eventSink = { listEvent ->
                    when (listEvent) {
                        is RoomListEvents.ShowContextMenu -> {
                            updatedEventSink(GroupListEvents.ShowContextMenu(listEvent.roomSummary))
                        }
                        else -> {}
                    }
                },
            )
            if (index != state.summaries.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

/**
 * 空状态脚手架
 *
 * 渲染空状态的标准布局，包含图标、标题、描述和操作按钮。
 *
 * @param title 标题字符串资源 ID
 * @param subtitle 描述字符串资源 ID
 * @param modifier 修饰符
 * @param action 可选的操作按钮
 */
@Composable
private fun EmptyScaffold(
    title: Int,
    subtitle: Int,
    modifier: Modifier = Modifier,
    action: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(io.element.android.appicon.element.R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(118.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(30.dp))
        androidx.compose.material.Text(
            text = stringResource(title),
            style = ElementTheme.typography.fontHeadingMdBold,
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material.Text(
            text = stringResource(subtitle),
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        action?.invoke(this)
    }
}

@PreviewsDayNight
@Composable
internal fun GroupListContentViewPreview(
    @PreviewParameter(GroupListContentStateProvider::class) state: GroupListContentState
) = ElementPreview {
    GroupListContentView(
        contentState = state,
        lazyListState = rememberLazyListState(),
        onRoomClick = {},
        onCreateRoomClick = {},
        contentPadding = PaddingValues(0.dp),
        eventSink = {},
    )
}
