/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.grouplist.GroupListContentState
import io.element.android.features.home.impl.grouplist.GroupListEvents
import io.element.android.features.home.impl.grouplist.GroupListState
import io.element.android.features.home.impl.grouplist.aGroupListState as createGroupListState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.roomlist.RoomListStateProvider
import io.element.android.features.home.impl.roomlist.aRoomListState
import io.element.android.features.home.impl.roomlist.aRoomsContentState
import io.element.android.features.home.impl.roomlist.generateRoomListRoomSummaryList
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.home.impl.spaces.aHomeSpacesState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.logout.api.direct.aDirectLogoutState
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

/**
 * 首页状态提供者
 *
 * 为预览和测试提供 HomeState 示例数据。
 *
 * @see HomeState 首页状态
 */
open class HomeStateProvider : PreviewParameterProvider<HomeState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<HomeState>
        get() = sequenceOf(
            aHomeState(),
            aHomeState(hasNetworkConnection = false),
            aHomeState(snackbarMessage = SnackbarMessage(CommonStrings.common_verification_complete)),
            aHomeState(
                roomListState = aRoomListState(
                    // 添加更多房间以查看 NavigationBar 下的模糊效果
                    contentState = aRoomsContentState(
                        summaries = generateRoomListRoomSummaryList(),
                    )
                ),
                // 要在预览中显示底部导航栏，用户必须至少是一个空间的成员
                homeSpacesState = aHomeSpacesState(),
            ),
            aHomeState(
                currentHomeNavigationBarItem = HomeNavigationBarItem.Spaces,
            ),
            // 展示 Chats 角标效果
            aHomeState(
                chatsUnreadCount = 5,
                currentHomeNavigationBarItem = HomeNavigationBarItem.Chats,
            ),
        ) + RoomListStateProvider().values.map {
            aHomeState(roomListState = it)
        }
}

/**
 * 创建示例首页状态
 *
 * @param matrixUser Matrix 用户
 * @param currentUserAndNeighbors 当前用户及相邻用户列表
 * @param showAvatarIndicator 是否显示头像指示器
 * @param hasNetworkConnection 是否有网络连接
 * @param snackbarMessage 提示消息
 * @param currentHomeNavigationBarItem 当前底部导航栏项
 * @param roomListState 房间列表状态
 * @param homeSpacesState 首页空间状态
 * @param canReportBug 是否可以报告问题
 * @param directLogoutState 直接退出登录状态
 * @param eventSink 事件处理函数
 * @return HomeState 示例实例
 */
internal fun aHomeState(
    matrixUser: MatrixUser = MatrixUser(userId = UserId("@id:domain"), displayName = "User#1"),
    currentUserAndNeighbors: List<MatrixUser> = listOf(matrixUser),
    showAvatarIndicator: Boolean = false,
    hasNetworkConnection: Boolean = true,
    snackbarMessage: SnackbarMessage? = null,
    currentHomeNavigationBarItem: HomeNavigationBarItem = HomeNavigationBarItem.Community,
    roomListState: RoomListState = aRoomListState(),
    groupListState: GroupListState = aGroupListState(),
    homeSpacesState: HomeSpacesState = aHomeSpacesState(),
    canReportBug: Boolean = true,
    accountManagementUrl: String? = null,
    devicesManagementUrl: String? = null,
    showLinkNewDevice: Boolean = false,
    showBlockedUsersItem: Boolean = false,
    showSecureBackup: Boolean = true,
    showSecureBackupBadge: Boolean = false,
    directLogoutState: DirectLogoutState = aDirectLogoutState(),
    eventSink: (HomeEvents) -> Unit = {},
    chatsUnreadCount: Int = 0,
) = HomeState(
    currentUserAndNeighbors = currentUserAndNeighbors.toImmutableList(),
    showAvatarIndicator = showAvatarIndicator,
    hasNetworkConnection = hasNetworkConnection,
    snackbarMessage = snackbarMessage,
    canReportBug = canReportBug,
    accountManagementUrl = accountManagementUrl,
    devicesManagementUrl = devicesManagementUrl,
    showLinkNewDevice = showLinkNewDevice,
    showBlockedUsersItem = showBlockedUsersItem,
    showSecureBackup = showSecureBackup,
    showSecureBackupBadge = showSecureBackupBadge,
    directLogoutState = directLogoutState,
    currentHomeNavigationBarItem = currentHomeNavigationBarItem,
    roomListState = roomListState,
    homeSpacesState = homeSpacesState,
    eventSink = eventSink,
    groupListState = groupListState,
    chatsUnreadCount = chatsUnreadCount,
)

internal fun aGroupListState() = createGroupListState()
