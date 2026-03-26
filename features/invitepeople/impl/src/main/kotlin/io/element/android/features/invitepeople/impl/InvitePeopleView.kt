/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.async.AsyncFailure
import io.element.android.libraries.designsystem.components.async.AsyncLoading
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.ListSectionHeader
import io.element.android.libraries.designsystem.theme.components.SearchBar
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.CheckableUserRow
import io.element.android.libraries.matrix.ui.components.CheckableUserRowData
import io.element.android.libraries.matrix.ui.components.SelectedUsersRowList
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.getBestName
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

/**
 * 邀请人员页面主视图
 *
 * 根Composable函数，根据房间加载状态显示不同的UI：
 * - 加载失败：显示错误视图
 * - 加载中/未初始化/成功：显示内容视图
 *
 * @param state 邀请人员页面状态
 * @param modifier Compose修饰符
 */
@Composable
fun InvitePeopleView(
    state: DefaultInvitePeopleState,
    modifier: Modifier = Modifier,
) {
    when (state.room) {
        is AsyncData.Failure -> InvitePeopleViewError(state.room.error, modifier)
        AsyncData.Uninitialized,
        is AsyncData.Loading,
        is AsyncData.Success -> InvitePeopleContentView(state, modifier)
    }
}

/**
 * 邀请人员页面错误视图
 *
 * 当房间加载失败时显示的错误界面。
 * 使用AsyncFailure组件展示错误信息和重试选项。
 *
 * @param error 发生的错误异常
 * @param modifier Compose修饰符
 */
@Composable
private fun InvitePeopleViewError(
    error: Throwable,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncFailure(
            throwable = error,
            onRetry = null,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * 邀请人员页面内容视图
 *
 * 显示邀请人员功能的主要内容界面，包括：
 * - 搜索栏
 * - 已选用户列表（当搜索未激活时）
 * - 推荐用户列表（当搜索未激活时）
 *
 * @param state 邀请人员页面状态
 * @param modifier Compose修饰符
 */
@Composable
private fun InvitePeopleContentView(
    state: DefaultInvitePeopleState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        /**
         * 切换用户选择状态的辅助函数
         *
         * 将用户选择事件发送到eventSink处理。
         *
         * @param user 要切换选择状态的Matrix用户
         */
        fun toggleUser(user: MatrixUser) {
            state.eventSink(DefaultInvitePeopleEvents.ToggleUser(user))
        }

        // 搜索栏组件
        InvitePeopleSearchBar(
            modifier = Modifier.fillMaxWidth(),
            queryState = state.searchQuery,
            showLoader = state.showSearchLoader,
            selectedUsers = state.selectedUsers,
            state = state.searchResults,
            active = state.isSearchActive,
            onActiveChange = {
                state.eventSink(
                    DefaultInvitePeopleEvents.OnSearchActiveChanged(
                        it
                    )
                )
            },
            onToggleUser = ::toggleUser,
        )

        // 当搜索未激活时，显示已选用户列表和推荐列表
        if (!state.isSearchActive) {
            // 显示已选用户列表（可水平滚动）
            if (state.selectedUsers.isNotEmpty()) {
                SelectedUsersRowList(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUsers = state.selectedUsers,
                    autoScroll = true,
                    onUserRemove = ::toggleUser,
                    contentPadding = PaddingValues(all = 16.dp),
                )
            }
            // 显示推荐用户列表
            if (state.suggestions.isNotEmpty()) {
                LazyColumn {
                    item {
                        ListSectionHeader(
                            title = stringResource(id = CommonStrings.common_suggestions),
                            hasDivider = false,
                        )
                    }
                    itemsIndexed(state.suggestions) { index, invitableUser ->
                        CheckableUserRow(
                            checked = invitableUser.isSelected,
                            onCheckedChange = {
                                state.eventSink(DefaultInvitePeopleEvents.ToggleUser(invitableUser.matrixUser))
                            },
                            data = CheckableUserRowData.Resolved(
                                avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                                name = invitableUser.matrixUser.getBestName(),
                                subtext = invitableUser.matrixUser.userId.value,
                            ),
                        )
                        if (index < state.suggestions.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

/**
 * 邀请人员搜索栏组件
 *
 * 搜索栏的完整实现，包含：
 * - 搜索输入框
 * - 已选用户展示（在搜索栏激活时显示在顶部）
 * - 搜索结果列表
 * - 搜索加载指示器
 *
 * @param queryState 搜索框文本状态
 * @param state 搜索结果状态
 * @param showLoader 是否显示搜索加载指示器
 * @param selectedUsers 已选用户列表
 * @param active 搜索栏是否处于激活状态
 * @param onActiveChange 激活状态变更回调
 * @param onToggleUser 用户选择切换回调
 * @param modifier Compose修饰符
 * @param placeHolderTitle 搜索框占位符文本
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitePeopleSearchBar(
    queryState: TextFieldState,
    state: SearchBarResultState<ImmutableList<InvitableUser>>,
    showLoader: Boolean,
    selectedUsers: ImmutableList<MatrixUser>,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onToggleUser: (MatrixUser) -> Unit,
    modifier: Modifier = Modifier,
    placeHolderTitle: String = stringResource(CommonStrings.common_search_for_someone),
) {
    SearchBar(
        queryState = queryState,
        active = active,
        onActiveChange = onActiveChange,
        modifier = modifier,
        placeHolderTitle = placeHolderTitle,
        // 搜索栏激活时显示已选用户列表
        contentPrefix = {
            if (selectedUsers.isNotEmpty()) {
                SelectedUsersRowList(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUsers = selectedUsers,
                    autoScroll = true,
                    onUserRemove = onToggleUser,
                    contentPadding = PaddingValues(all = 16.dp),
                )
            }
        },
        showBackButton = false,
        resultState = state,
        // 搜索加载时显示加载指示器
        contentSuffix = {
            if (showLoader) {
                AsyncLoading()
            }
        },
        // 搜索结果处理和渲染
        resultHandler = { results ->
            // 搜索结果标题
            Text(
                text = stringResource(id = CommonStrings.common_search_results),
                style = ElementTheme.typography.fontBodyLgMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp)
            )

            // 搜索结果列表
            LazyColumn {
                itemsIndexed(results) { index, invitableUser ->
                    // 判断用户状态
                    val invitedOrJoined = invitableUser.isAlreadyInvited || invitableUser.isAlreadyJoined
                    val isUnresolved = invitableUser.isUnresolved && !invitedOrJoined
                    // 已加入或已邀请的用户不允许再次操作（除非是未解析用户）
                    val enabled = isUnresolved || !invitedOrJoined

                    // 根据用户是否已解析显示不同的数据
                    val data = if (isUnresolved) {
                        // 未解析用户，只显示头像和ID
                        CheckableUserRowData.Unresolved(
                            avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                            id = invitableUser.matrixUser.userId.value,
                        )
                    } else {
                        // 已解析用户，显示完整信息
                        CheckableUserRowData.Resolved(
                            avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                            name = invitableUser.matrixUser.getBestName(),
                            subtext = when {
                                // 如果用户已加入或已受邀，显示相应状态信息
                                invitableUser.isAlreadyJoined -> stringResource(R.string.screen_invite_users_already_a_member)
                                invitableUser.isAlreadyInvited -> stringResource(R.string.screen_invite_users_already_invited)
                                // 否则显示用户ID（除非ID已被用作显示名）
                                invitableUser.matrixUser.displayName.isNullOrEmpty()
                                    .not() -> invitableUser.matrixUser.userId.value
                                else -> null
                            }
                        )
                    }
                    CheckableUserRow(
                        checked = invitableUser.isSelected || invitedOrJoined,
                        enabled = enabled,
                        data = data,
                        onCheckedChange = { onToggleUser(invitableUser.matrixUser) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
    )
}

/**
 * 邀请人员视图预览
 *
 * 使用@PreviewsDayNight注解提供日夜两种主题的预览。
 * 使用DefaultInvitePeopleStateProvider提供各种状态的预览数据。
 *
 * @param state 预览用的状态数据，由PreviewParameterProvider提供
 * @see DefaultInvitePeopleStateProvider 状态提供者
 */
@PreviewsDayNight
@Composable
internal fun InvitePeopleViewPreview(@PreviewParameter(DefaultInvitePeopleStateProvider::class) state: DefaultInvitePeopleState) =
    ElementPreview {
        InvitePeopleView(state = state)
    }
