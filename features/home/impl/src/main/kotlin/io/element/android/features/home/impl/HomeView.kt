/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalHazeMaterialsApi::class)

package io.element.android.features.home.impl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.components.HomeTopBar
import io.element.android.features.home.impl.components.GroupListContentView
import io.element.android.features.home.impl.components.RoomListContentView
import io.element.android.features.home.impl.components.RoomListMenuAction
import io.element.android.features.home.impl.components.SettingsLandingView
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.grouplist.GroupListContextMenu
import io.element.android.features.home.impl.grouplist.GroupListState
import io.element.android.features.home.impl.roomlist.RoomListContextMenu
import io.element.android.features.home.impl.roomlist.RoomListDeclineInviteMenu
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.search.RoomListSearchView
import io.element.android.features.home.impl.spaces.HomeSpacesView
import io.element.android.libraries.androidutils.throttler.FirstThrottler
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.GradientIconButton
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.NavigationBar
import io.element.android.libraries.designsystem.theme.components.NavigationBarIcon
import io.element.android.libraries.designsystem.theme.components.NavigationBarItem
import io.element.android.libraries.designsystem.theme.components.NavigationBarText
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.coroutines.launch

/**
 * 首页视图
 *
 * 渲染应用首页的用户界面，包含顶部栏、房间列表、空间导航、底部导航栏等组件。
 * 支持房间点击、设置、创建聊天、创建空间等多种功能。
 *
 * @param homeState 首页状态
 * @param onRoomClick 房间点击事件
 * @param onSettingsClick 设置点击事件
 * @param onSetUpRecoveryClick 设置恢复点击事件
 * @param onConfirmRecoveryKeyClick 确认恢复密钥点击事件
 * @param onStartChatClick 开始聊天点击事件
 * @param onCreateSpaceClick 创建空间点击事件
 * @param onRoomSettingsClick 房间设置点击事件
 * @param onMenuActionClick 菜单操作点击事件
 * @param onReportRoomClick 报告房间点击事件
 * @param onDeclineInviteAndBlockUser 拒绝邀请并阻止用户事件
 * @param acceptDeclineInviteView 接受/拒绝邀请视图
 * @param modifier 修饰符
 * @param leaveRoomView 离开房间视图
 */
@Composable
fun HomeView(
    homeState: HomeState,
    onRoomClick: (RoomId) -> Unit,
    onSettingsClick: () -> Unit,
    onOpenUserProfile: (MatrixUser) -> Unit,
    onOpenUserQrCode: (MatrixUser) -> Unit,
    onManageAccountClick: (String) -> Unit,
    onManageDevicesClick: (String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLockScreenSettingsClick: () -> Unit,
    onAdvancedSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBlockedUsersClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onStartChatClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onCreateSpaceClick: () -> Unit,
    onRoomSettingsClick: (roomId: RoomId) -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onReportRoomClick: (roomId: RoomId) -> Unit,
    onDeclineInviteAndBlockUser: (roomSummary: RoomListRoomSummary) -> Unit,
    onScanQrCode: () -> Unit,
    acceptDeclineInviteView: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leaveRoomView: @Composable () -> Unit,
) {
    val state: RoomListState = homeState.roomListState
    val groupListState: GroupListState = homeState.groupListState
    val coroutineScope = rememberCoroutineScope()
    val firstThrottler = remember { FirstThrottler(300, coroutineScope) }
    Box(modifier) {
        if (state.contextMenu is RoomListState.ContextMenu.Shown) {
            RoomListContextMenu(
                contextMenu = state.contextMenu,
                canReportRoom = state.canReportRoom,
                eventSink = state.eventSink,
                onRoomSettingsClick = onRoomSettingsClick,
                onReportRoomClick = onReportRoomClick,
            )
        }
        val groupListContextMenu = groupListState.contextMenu
        if (groupListContextMenu is GroupListState.ContextMenu.Shown) {
            GroupListContextMenu(
                contextMenu = groupListContextMenu,
                canReportRoom = groupListState.canReportRoom,
                eventSink = groupListState.eventSink,
                onRoomSettingsClick = onRoomSettingsClick,
                onReportRoomClick = onReportRoomClick,
            )
        }
        if (state.declineInviteMenu is RoomListState.DeclineInviteMenu.Shown) {
            RoomListDeclineInviteMenu(
                menu = state.declineInviteMenu,
                canReportRoom = state.canReportRoom,
                eventSink = state.eventSink,
                onDeclineAndBlockClick = onDeclineInviteAndBlockUser,
            )
        }

        leaveRoomView()

        HomeScaffold(
            state = homeState,
            onSetUpRecoveryClick = onSetUpRecoveryClick,
            onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
            onOpenSettings = { if (firstThrottler.canHandle()) onSettingsClick() },
            onOpenUserProfile = { if (firstThrottler.canHandle()) onOpenUserProfile(it) },
            onOpenUserQrCode = { if (firstThrottler.canHandle()) onOpenUserQrCode(it) },
            onManageAccountClick = { if (firstThrottler.canHandle()) onManageAccountClick(it) },
            onManageDevicesClick = { if (firstThrottler.canHandle()) onManageDevicesClick(it) },
            onLinkNewDeviceClick = { if (firstThrottler.canHandle()) onLinkNewDeviceClick() },
            onNotificationSettingsClick = { if (firstThrottler.canHandle()) onNotificationSettingsClick() },
            onLockScreenSettingsClick = { if (firstThrottler.canHandle()) onLockScreenSettingsClick() },
            onAdvancedSettingsClick = { if (firstThrottler.canHandle()) onAdvancedSettingsClick() },
            onAboutClick = { if (firstThrottler.canHandle()) onAboutClick() },
            onBlockedUsersClick = { if (firstThrottler.canHandle()) onBlockedUsersClick() },
            onSignOutClick = { if (firstThrottler.canHandle()) onSignOutClick() },
            onScanQrCode = onScanQrCode,
            onStartChatClick = { if (firstThrottler.canHandle()) onStartChatClick() },
            onCreateRoomClick = { if (firstThrottler.canHandle()) onCreateRoomClick() },
            onCreateSpaceClick = { if (firstThrottler.canHandle()) onCreateSpaceClick() },
            onMenuActionClick = onMenuActionClick,
        )
        // 此叠加视图仅在 state.displaySearchResults 为 true 时可见
        RoomListSearchView(
            state = state.searchState,
            eventSink = state.eventSink,
            hideInvitesAvatars = state.hideInvitesAvatars,
            onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
            modifier = Modifier
                .fillMaxSize()
                .background(ElementTheme.colors.bgCanvasDefault)
        )
        acceptDeclineInviteView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    state: HomeState,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomId) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenUserProfile: (MatrixUser) -> Unit,
    onOpenUserQrCode: (MatrixUser) -> Unit,
    onManageAccountClick: (String) -> Unit,
    onManageDevicesClick: (String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLockScreenSettingsClick: () -> Unit,
    onAdvancedSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBlockedUsersClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onScanQrCode: () -> Unit,
    onStartChatClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onCreateSpaceClick: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onRoomClick(room: RoomListRoomSummary) {
        onRoomClick(room.roomId)
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)
    val roomListState: RoomListState = state.roomListState

    BackHandler(
        enabled = state.currentHomeNavigationBarItem != HomeNavigationBarItem.Community,
    ) {
        state.eventSink(HomeEvents.SelectHomeNavigationBarItem(HomeNavigationBarItem.Community))
    }

    val hazeState = rememberHazeState()
    val roomsLazyListState = rememberLazyListState()
    val groupLazyListState = rememberLazyListState()
    val spacesLazyListState = rememberLazyListState()
    val settingsLazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (state.currentHomeNavigationBarItem != HomeNavigationBarItem.Settings) {
                HomeTopBar(
                    selectedNavigationItem = state.currentHomeNavigationBarItem,
                    title = stringResource(state.currentHomeNavigationBarItem.labelRes),
                    currentUserAndNeighbors = state.currentUserAndNeighbors,
                    showAvatarIndicator = state.showAvatarIndicator,
                    areSearchResultsDisplayed = roomListState.searchState.isSearchActive,
                    onToggleSearch = { roomListState.eventSink(RoomListEvents.ToggleSearchResults) },
                    onMenuActionClick = onMenuActionClick,
                    onOpenSettings = onOpenSettings,
                    onStartChatClick = onStartChatClick,
                    onCreateRoomClick = onCreateRoomClick,
                    onAccountSwitch = {
                        state.eventSink(HomeEvents.SwitchToAccount(it))
                    },
                    onCreateSpace = onCreateSpaceClick,
                    scrollBehavior = scrollBehavior,
                    displayFilters = state.displayRoomListFilters,
                    filtersState = roomListState.filtersState,
                    canCreateSpaces = state.homeSpacesState.canCreateSpaces,
                    canReportBug = state.canReportBug,
                    onScanQrCode = onScanQrCode,
                    modifier = Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.thick(),
                    )
                )
            }
        },
        bottomBar = {
            if (state.showNavigationBar) {
                val coroutineScope = rememberCoroutineScope()
                HomeBottomBar(
                    currentHomeNavigationBarItem = state.currentHomeNavigationBarItem,
                    chatsUnreadCount = state.chatsUnreadCount,
                    onItemClick = { item ->
                        // scroll to top if selecting the same item
                        if (item == state.currentHomeNavigationBarItem) {
                            val lazyListStateTarget = when (item) {
                                HomeNavigationBarItem.Chats -> roomsLazyListState
                                HomeNavigationBarItem.Community -> groupLazyListState
                                HomeNavigationBarItem.Spaces -> spacesLazyListState
                                HomeNavigationBarItem.Settings -> settingsLazyListState
                            }
                            coroutineScope.launch {
                                if (lazyListStateTarget.firstVisibleItemIndex > 10) {
                                    lazyListStateTarget.scrollToItem(10)
                                }
                                // Also reset the scrollBehavior height offset as it's not triggered by programmatic scrolls
                                scrollBehavior.state.heightOffset = 0f
                                lazyListStateTarget.animateScrollToItem(0)
                            }
                        } else {
                            state.eventSink(HomeEvents.SelectHomeNavigationBarItem(item))
                        }
                    },
                    modifier = Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.thick(),
                    )
                )
            }
        },
        content = { padding ->
            when (state.currentHomeNavigationBarItem) {
                HomeNavigationBarItem.Chats -> {
                    RoomListContentView(
                        contentState = roomListState.contentState,
                        filtersState = roomListState.filtersState,
                        lazyListState = roomsLazyListState,
                        hideInvitesAvatars = roomListState.hideInvitesAvatars,
                        eventSink = roomListState.eventSink,
                        onSetUpRecoveryClick = onSetUpRecoveryClick,
                        onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                        onRoomClick = ::onRoomClick,
                        onCreateRoomClick = onStartChatClick,
                        contentPadding = PaddingValues(
                            // FAB height is 56dp, bottom padding is 16dp, we add 8dp as extra margin -> 56+16+8 = 80,
                            // and include provided bottom padding
                            // Disable contentPadding due to navigation issue using the keyboard
                            // See https://issuetracker.google.com/issues/436432313
                            bottom = 80.dp,
                            // bottom = 80.dp + padding.calculateBottomPadding(),
                            // top = padding.calculateTopPadding()
                        ),
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                                    // Remove these two lines once https://issuetracker.google.com/issues/436432313 has been fixed
                                    bottom = padding.calculateBottomPadding(),
                                    top = padding.calculateTopPadding()
                                )
                            )
                            .consumeWindowInsets(padding)
                            .hazeSource(state = hazeState)
                    )
                }
                HomeNavigationBarItem.Spaces -> {
                    HomeSpacesView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .hazeSource(state = hazeState),
                        state = state.homeSpacesState,
                        lazyListState = spacesLazyListState,
                        onSpaceClick = { spaceId ->
                            onRoomClick(spaceId)
                        },
                        onCreateSpaceClick = onCreateSpaceClick,
                        // TODO use actual callbacks for this
                        onExploreClick = {},
                    )
                }
                HomeNavigationBarItem.Community -> {
                    GroupListContentView(
                        contentState = state.groupListState.contentState,
                        lazyListState = groupLazyListState,
                        onRoomClick = ::onRoomClick,
                        onCreateRoomClick = onCreateRoomClick,
                        contentPadding = PaddingValues(
                            bottom = 80.dp,
                        ),
                        eventSink = state.groupListState.eventSink,
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                                    bottom = padding.calculateBottomPadding(),
                                    top = padding.calculateTopPadding()
                                )
                            )
                            .consumeWindowInsets(padding)
                            .hazeSource(state = hazeState)
                    )
                }
                HomeNavigationBarItem.Settings -> {
                    SettingsLandingView(
                        state = state,
                        lazyListState = settingsLazyListState,
                        onOpenUserProfile = onOpenUserProfile,
                        onOpenUserQrCode = onOpenUserQrCode,
                        onManageAccountClick = onManageAccountClick,
                        onManageDevicesClick = onManageDevicesClick,
                        onLinkNewDeviceClick = onLinkNewDeviceClick,
                        onOpenNotificationSettings = onNotificationSettingsClick,
                        onOpenLockScreenSettings = onLockScreenSettingsClick,
                        onOpenAdvancedSettings = onAdvancedSettingsClick,
                        onOpenAbout = onAboutClick,
                        onOpenBlockedUsers = onBlockedUsersClick,
                        onSignOutClick = onSignOutClick,
                        onSetUpRecoveryClick = onSetUpRecoveryClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                PaddingValues(
                                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                                    bottom = padding.calculateBottomPadding(),
                                    top = padding.calculateTopPadding()
                                )
                            )
                            .consumeWindowInsets(padding)
                            .hazeSource(state = hazeState)
                    )
                }
            }
        },
        floatingActionButton = {
            if (state.displayActions) {
                GradientIconButton(
                    onClick = onStartChatClick,
                    modifier = Modifier.size(82.dp)
                ) {
                    Icon(
                        painter = painterResource(CompoundIcons.Call()),
                        contentDescription = stringResource(id = R.string.screen_roomlist_a11y_create_message),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    )
}

@Composable
private fun HomeBottomBar(
    currentHomeNavigationBarItem: HomeNavigationBarItem,
    chatsUnreadCount: Int,
    onItemClick: (HomeNavigationBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
    ) {
        HomeNavigationBarItem.visibleEntries().forEach { item ->
            val isSelected = currentHomeNavigationBarItem == item
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    NavigationBarIcon(
                        imageVector = item.icon(isSelected),
                        count = if (item == HomeNavigationBarItem.Chats) chatsUnreadCount else 0,
                        iconSize = 30.dp,
                    )
                },
                label = {
                    NavigationBarText(
                        text = stringResource(item.labelRes),
                    )
                }
            )
        }
    }
}

internal fun RoomListRoomSummary.contentType() = displayType.ordinal

@PreviewsDayNight
@Composable
internal fun HomeViewPreview(@PreviewParameter(HomeStateProvider::class) state: HomeState) = ElementPreview {
    HomeView(
        homeState = state,
        onRoomClick = {},
        onSettingsClick = {},
        onOpenUserProfile = {},
        onOpenUserQrCode = {},
        onManageAccountClick = {},
        onManageDevicesClick = {},
        onLinkNewDeviceClick = {},
        onNotificationSettingsClick = {},
        onLockScreenSettingsClick = {},
        onAdvancedSettingsClick = {},
        onAboutClick = {},
        onBlockedUsersClick = {},
        onSignOutClick = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onStartChatClick = {},
        onCreateRoomClick = {},
        onCreateSpaceClick = {},
        onRoomSettingsClick = {},
        onReportRoomClick = {},
        onMenuActionClick = {},
        onDeclineInviteAndBlockUser = {},
        onScanQrCode = {},
        acceptDeclineInviteView = {},
        leaveRoomView = {}
    )
}

@Preview
@Composable
internal fun HomeViewA11yPreview() = ElementPreview {
    HomeView(
        homeState = aHomeState(),
        onRoomClick = {},
        onSettingsClick = {},
        onOpenUserProfile = {},
        onOpenUserQrCode = {},
        onManageAccountClick = {},
        onManageDevicesClick = {},
        onLinkNewDeviceClick = {},
        onNotificationSettingsClick = {},
        onLockScreenSettingsClick = {},
        onAdvancedSettingsClick = {},
        onAboutClick = {},
        onBlockedUsersClick = {},
        onSignOutClick = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onStartChatClick = {},
        onCreateRoomClick = {},
        onCreateSpaceClick = {},
        onRoomSettingsClick = {},
        onReportRoomClick = {},
        onMenuActionClick = {},
        onDeclineInviteAndBlockUser = {},
        onScanQrCode = {},
        acceptDeclineInviteView = {},
        leaveRoomView = {}
    )
}
