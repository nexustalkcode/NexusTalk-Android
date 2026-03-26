/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.features.home.impl.grouplist.GroupListState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

/**
 * 首页状态数据类
 *
 * 表示首页的完整状态，包含用户信息、房间列表、空间导航等信息。
 *
 * @property currentUserAndNeighbors 当前用户及相邻用户列表（多账户情况下包含3项，当前用户在中间）
 * @property showAvatarIndicator 是否显示头像指示器
 * @property hasNetworkConnection 是否有网络连接
 * @property currentHomeNavigationBarItem 当前底部导航栏项
 * @property roomListState 房间列表状态
 * @property homeSpacesState 首页空间状态
 * @property snackbarMessage 提示消息
 * @property canReportBug 是否可以报告问题
 * @property directLogoutState 直接退出登录状态
 * @property eventSink 事件处理函数
 */
data class HomeState(
    /**
     * 当前用户及相邻用户列表
     * 在多账户情况下，会包含3个项目，当前用户显示在中间位置。
     */
    val currentUserAndNeighbors: ImmutableList<MatrixUser>,
    val showAvatarIndicator: Boolean,
    val hasNetworkConnection: Boolean,
    val currentHomeNavigationBarItem: HomeNavigationBarItem,
    val roomListState: RoomListState,
    val groupListState: GroupListState,
    val homeSpacesState: HomeSpacesState,
    val snackbarMessage: SnackbarMessage?,
    val canReportBug: Boolean,
    val accountManagementUrl: String?,
    val devicesManagementUrl: String?,
    val showLinkNewDevice: Boolean,
    val showBlockedUsersItem: Boolean,
    val showSecureBackup: Boolean,
    val showSecureBackupBadge: Boolean,
    val directLogoutState: DirectLogoutState,
    val eventSink: (HomeEvents) -> Unit,
    /** Chats 标签页的未读消息数量 */
    val chatsUnreadCount: Int = 0,
) {
    /** 是否显示操作按钮（仅在聊天页面显示） */
    val displayActions = currentHomeNavigationBarItem == HomeNavigationBarItem.Chats
    /** 是否显示房间列表筛选器 */
    val displayRoomListFilters = currentHomeNavigationBarItem == HomeNavigationBarItem.Chats && roomListState.displayFilters
    /** 是否显示底部导航栏 */
    //val showNavigationBar = homeSpacesState.canCreateSpaces || homeSpacesState.spaceRooms.isNotEmpty()
    val showNavigationBar = true
}
