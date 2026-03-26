/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.api.AnnouncementService
import io.element.android.features.home.impl.grouplist.GroupListState
import io.element.android.features.home.impl.roomlist.RoomListContentState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.indicator.api.IndicatorService
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.oidc.AccountManagementAction
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import io.element.android.libraries.sessionstorage.api.SessionStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 首页 Presenter
 *
 * 负责处理首页的业务逻辑，管理用户状态、房间列表、空间导航等功能。
 *
 * @property client Matrix 客户端
 * @property syncService 同步服务
 * @property snackbarDispatcher 提示消息调度器
 * @property indicatorService 指示器服务
 * @property roomListPresenter 房间列表 Presenter
 * @property homeSpacesPresenter 首页空间 Presenter
 * @property logoutPresenter 退出登录 Presenter
 * @property rageshakeFeatureAvailability 崩溃报告功能可用性
 * @property sessionStore 会话存储
 * @property announcementService 公告服务
 */
@Inject
class HomePresenter(
    private val client: MatrixClient,
    private val syncService: SyncService,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val sessionVerificationService: SessionVerificationService,
    private val featureFlagService: FeatureFlagService,
    private val roomListPresenter: Presenter<RoomListState>,
    private val groupListPresenter: Presenter<GroupListState>,
    private val homeSpacesPresenter: Presenter<HomeSpacesState>,
    private val logoutPresenter: Presenter<DirectLogoutState>,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val sessionStore: SessionStore,
    private val announcementService: AnnouncementService,
) : Presenter<HomeState> {
    private val currentUserWithNeighborsBuilder = CurrentUserWithNeighborsBuilder()

    @Composable
    override fun present(): HomeState {
        val coroutineState = rememberCoroutineScope()
        val matrixUser by client.userProfile.collectAsState()
        val currentUserAndNeighbors by remember {
            combine(
                client.userProfile,
                sessionStore.sessionsFlow(),
                currentUserWithNeighborsBuilder::build,
            )
        }.collectAsState(initial = persistentListOf(matrixUser))
        val isOnline by syncService.isOnline.collectAsState()
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        val showLinkNewDevice by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.QrCodeLogin)
        }.collectAsState(initial = false)
        val roomListState = roomListPresenter.present()
        val groupListState = groupListPresenter.present()
        val homeSpacesState = homeSpacesPresenter.present()
        var currentHomeNavigationBarItemOrdinal by rememberSaveable { mutableIntStateOf(HomeNavigationBarItem.Community.ordinal) }
        val currentHomeNavigationBarItem by remember {
            derivedStateOf {
                HomeNavigationBarItem.from(currentHomeNavigationBarItemOrdinal)
            }
        }
        LaunchedEffect(Unit) {
            // 强制刷新用户资料
            client.getUserProfile()
        }
        LaunchedEffect(currentHomeNavigationBarItem) {
            if (currentHomeNavigationBarItem == HomeNavigationBarItem.Settings) {
                // 进入设置页时刷新资料，确保昵称/头像及时更新
                client.getUserProfile()
            }
        }
        // 头像指示器
        val showAvatarIndicator by indicatorService.showRoomListTopBarIndicator()
        val showSecureBackupBadge by indicatorService.showSettingChatBackupIndicator()
        val canVerifyUserSession by sessionVerificationService.needsSessionVerification.collectAsState(false)
        val showBlockedUsersItem by remember {
            client.ignoredUsersFlow.map { it.isNotEmpty() }
        }.collectAsState(initial = false)
        val accountManagementUrl = remember { mutableStateOf<String?>(null) }
        val devicesManagementUrl = remember { mutableStateOf<String?>(null) }
        LaunchedEffect(Unit) {
            accountManagementUrl.value = client.getAccountManagementUrl(AccountManagementAction.Profile).getOrNull()
            devicesManagementUrl.value = client.getAccountManagementUrl(AccountManagementAction.SessionsList).getOrNull()
        }
        val directLogoutState = logoutPresenter.present()

        // 计算 Chats 标签页的未读消息数量
        val chatsUnreadCount by remember(roomListState) {
            derivedStateOf {
                val contentState = roomListState.contentState
                if (contentState is RoomListContentState.Rooms) {
                    contentState.summaries.sumOf { it.totalUnreadCount.toInt() }
                } else {
                    0
                }
            }
        }

        fun handleEvent(event: HomeEvents) {
            when (event) {
                is HomeEvents.SelectHomeNavigationBarItem -> coroutineState.launch {
                    if (event.item == HomeNavigationBarItem.Spaces) {
                        announcementService.showAnnouncement(Announcement.Space)
                    }
                    currentHomeNavigationBarItemOrdinal = event.item.ordinal
                }
                is HomeEvents.SwitchToAccount -> coroutineState.launch {
                    sessionStore.setLatestSession(event.sessionId.value)
                }
            }
        }

        LaunchedEffect(homeSpacesState.canCreateSpaces, homeSpacesState.spaceRooms.isEmpty()) {
            // 如果创建空间功能禁用且最后一个空间已离开，确保显示聊天视图
            if (!homeSpacesState.canCreateSpaces && homeSpacesState.spaceRooms.isEmpty()) {
                currentHomeNavigationBarItemOrdinal = HomeNavigationBarItem.Community.ordinal
            }
        }
        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()
        return HomeState(
            currentUserAndNeighbors = currentUserAndNeighbors,
            showAvatarIndicator = showAvatarIndicator,
            hasNetworkConnection = isOnline,
            currentHomeNavigationBarItem = currentHomeNavigationBarItem,
            roomListState = roomListState,
            groupListState = groupListState,
            homeSpacesState = homeSpacesState,
            snackbarMessage = snackbarMessage,
            canReportBug = canReportBug,
            accountManagementUrl = accountManagementUrl.value,
            devicesManagementUrl = devicesManagementUrl.value,
            showLinkNewDevice = showLinkNewDevice,
            showBlockedUsersItem = showBlockedUsersItem,
            showSecureBackup = !canVerifyUserSession,
            showSecureBackupBadge = showSecureBackupBadge,
            directLogoutState = directLogoutState,
            eventSink = ::handleEvent,
            chatsUnreadCount = chatsUnreadCount,
        )
    }
}
