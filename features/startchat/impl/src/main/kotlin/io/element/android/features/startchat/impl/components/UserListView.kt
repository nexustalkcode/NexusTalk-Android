/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.startchat.impl.userlist.UserListEvents
import io.element.android.features.startchat.impl.userlist.UserListState
import io.element.android.features.startchat.impl.userlist.UserListStateProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.ListSectionHeader
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.homeIconBackground
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.CheckableUserRow
import io.element.android.libraries.matrix.ui.components.CheckableUserRowData
import io.element.android.libraries.matrix.ui.components.SelectedUsersRowList
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.getBestName
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
/**
 * 渲染用户列表区域。
 *
 * 根据是否在搜索模式，展示搜索栏或最近私聊建议。
 */
fun UserListView(
    state: UserListState,
    onSelectUser: (MatrixUser) -> Unit,
    onDeselectUser: (MatrixUser) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    Column(
        modifier = modifier,
    ) {
        //未激活的情况下 显示自定义样式搜索框
        if (state.isSearchActive.not()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(
                        color = ElementTheme.colors.homeIconBackground,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(onClick = { state.eventSink(UserListEvents.OnSearchActiveChanged(true)) }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .alpha(0.5f),
                    imageVector = CompoundIcons.Search(),
                    contentDescription = "创建",
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    modifier = Modifier
                        .semantics {
                            heading()
                        }
                        .alpha(0.5f),
                    style = ElementTheme.typography.fontBodyMdRegular.copy(fontSize = 16.4.sp),
                    text = stringResource(CommonStrings.action_search),
                )
            }
        } else {
            SearchUserBar(
                modifier = Modifier.fillMaxWidth(),
                queryState = state.searchQuery,
                resultState = state.searchResults,
                selectedUsers = state.selectedUsers,
                active = state.isSearchActive,
                showLoader = state.showSearchLoader,
                isMultiSelectionEnable = state.isMultiSelectionEnabled,
                showBackButton = showBackButton,
                onActiveChange = { state.eventSink(UserListEvents.OnSearchActiveChanged(it)) },
                onUserSelect = {
                    state.eventSink(UserListEvents.AddToSelection(it))
                    onSelectUser(it)
                },
                onUserDeselect = {
                    state.eventSink(UserListEvents.RemoveFromSelection(it))
                    onDeselectUser(it)
                },
            )
        }


        if (state.isMultiSelectionEnabled && !state.isSearchActive && state.selectedUsers.isNotEmpty()) {
            SelectedUsersRowList(
                contentPadding = PaddingValues(16.dp),
                selectedUsers = state.selectedUsers,
                autoScroll = true,
                onUserRemove = {
                    state.eventSink(UserListEvents.RemoveFromSelection(it))
                    onDeselectUser(it)
                },
            )
        }
        if (!state.isSearchActive && state.recentDirectRooms.isNotEmpty()) {
            LazyColumn {
                item {
                    ListSectionHeader(
                        title = stringResource(id = CommonStrings.common_suggestions),
                        hasDivider = false,
                    )
                }
                state.recentDirectRooms.forEachIndexed { index, recentDirectRoom ->
                    item {
                        val isSelected = state.selectedUsers.any {
                            recentDirectRoom.matrixUser.userId == it.userId
                        }
                        CheckableUserRow(
                            checked = isSelected,
                            onCheckedChange = {
                                if (isSelected) {
                                    state.eventSink(UserListEvents.RemoveFromSelection(recentDirectRoom.matrixUser))
                                    onDeselectUser(recentDirectRoom.matrixUser)
                                } else {
                                    state.eventSink(UserListEvents.AddToSelection(recentDirectRoom.matrixUser))
                                    onSelectUser(recentDirectRoom.matrixUser)
                                }
                            },
                            data = CheckableUserRowData.Resolved(
                                avatarData = recentDirectRoom.matrixUser.getAvatarData(AvatarSize.UserListItem),
                                name = recentDirectRoom.matrixUser.getBestName(),
                                subtext = recentDirectRoom.matrixUser.userId.value,
                            ),
                        )
                        if (index < state.recentDirectRooms.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun UserListViewPreview(@PreviewParameter(UserListStateProvider::class) state: UserListState) = ElementPreview {
    UserListView(
        state = state,
        onSelectUser = {},
        onDeselectUser = {},
    )
}
