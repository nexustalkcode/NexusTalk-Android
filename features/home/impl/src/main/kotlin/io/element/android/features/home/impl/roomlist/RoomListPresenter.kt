/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.zacsweers.metro.Inject
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.api.AnnouncementService
import io.element.android.features.home.impl.datasource.RoomListDataSource
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.search.RoomListSearchEvents
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents.AcceptInvite
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents.DeclineInvite
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomEvent
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.encryption.RecoveryState
import io.element.android.libraries.matrix.api.roomlist.RoomList
import io.element.android.libraries.matrix.api.timeline.ReceiptType
import io.element.android.libraries.matrix.api.verification.SessionVerifiedStatus
import io.element.android.libraries.matrix.ui.safety.rememberHideInvitesAvatar
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import io.element.android.libraries.push.api.notifications.NotificationCleaner
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import timber.log.Timber

private const val EXTENDED_RANGE_SIZE = 40
private const val SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS = 300L

/**
 * 房间列表 Presenter
 *
 * 负责处理房间列表的业务逻辑，管理房间显示、上下文菜单、邀请处理等功能。
 *
 * @property client Matrix 客户端
 * @property leaveRoomPresenter 离开房间 Presenter
 * @property roomListDataSource 房间列表数据源
 * @property filtersPresenter 筛选器 Presenter
 * @property searchPresenter 搜索 Presenter
 * @property sessionPreferencesStore 会话偏好设置存储
 * @property analyticsService 分析服务
 * @property acceptDeclineInvitePresenter 接受/拒绝邀请 Presenter
 * @property fullScreenIntentPermissionsPresenter 全屏intent权限 Presenter
 * @property batteryOptimizationPresenter 电池优化 Presenter
 * @property notificationCleaner 通知清理器
 * @property appPreferencesStore 应用偏好设置存储
 * @property seenInvitesStore 已查看邀请存储
 * @property announcementService 公告服务
 * @property coldStartWatcher 冷启动观察者
 */
@Inject
class RoomListPresenter(
    private val client: MatrixClient,
    private val leaveRoomPresenter: Presenter<LeaveRoomState>,
    private val roomListDataSource: RoomListDataSource,
    private val filtersPresenter: Presenter<RoomListFiltersState>,
    private val searchPresenter: Presenter<RoomListSearchState>,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val analyticsService: AnalyticsService,
    private val acceptDeclineInvitePresenter: Presenter<AcceptDeclineInviteState>,
    private val fullScreenIntentPermissionsPresenter: Presenter<FullScreenIntentPermissionsState>,
    private val batteryOptimizationPresenter: Presenter<BatteryOptimizationState>,
    private val notificationCleaner: NotificationCleaner,
    private val appPreferencesStore: AppPreferencesStore,
    private val seenInvitesStore: SeenInvitesStore,
    private val announcementService: AnnouncementService,
    private val coldStartWatcher: AnalyticsColdStartWatcher,
) : Presenter<RoomListState> {
    private val encryptionService = client.encryptionService

    @Composable
    override fun present(): RoomListState {
        val coroutineScope = rememberCoroutineScope()
        val leaveRoomState = leaveRoomPresenter.present()
        val filtersState = filtersPresenter.present()
        val searchState = searchPresenter.present()
        val acceptDeclineInviteState = acceptDeclineInvitePresenter.present()

        LaunchedEffect(Unit) {
            roomListDataSource.launchIn(this)
        }

        var securityBannerDismissed by remember { mutableStateOf(false) }
        var fullScreenIntentPermissionBannerDismissed by remember { mutableStateOf(false) }
        var batteryOptimizationBannerDismissed by remember { mutableStateOf(false) }
        var newNotificationSoundBannerDismissed by remember { mutableStateOf(false) }
        val showNewNotificationSoundBanner by remember {
            announcementService.announcementsToShowFlow().map { announcements ->
                announcements.contains(Announcement.NewNotificationSound)
            }
        }.collectAsState(false)

        // 隐藏邀请头像
        val hideInvitesAvatar by client.rememberHideInvitesAvatar()

        val contextMenu = remember { mutableStateOf<RoomListState.ContextMenu>(RoomListState.ContextMenu.Hidden) }
        val declineInviteMenu = remember { mutableStateOf<RoomListState.DeclineInviteMenu>(RoomListState.DeclineInviteMenu.Hidden) }

        fun handleEvent(event: RoomListEvents) {
            when (event) {
                is RoomListEvents.UpdateVisibleRange -> coroutineScope.launch {
                    updateVisibleRange(event.range)
                }
                RoomListEvents.DismissRequestVerificationPrompt -> {
                    securityBannerDismissed = true
                    Timber.d("RoomListBanner debug: dismiss request verification prompt")
                }
                RoomListEvents.DismissBanner -> {
                    securityBannerDismissed = true
                    Timber.d("RoomListBanner debug: dismiss security banner")
                }
                RoomListEvents.DismissFullScreenIntentPermissionBanner -> {
                    fullScreenIntentPermissionBannerDismissed = true
                    Timber.d("RoomListBanner debug: dismiss full screen intent banner in current session")
                }
                RoomListEvents.DismissBatteryOptimizationBanner -> {
                    batteryOptimizationBannerDismissed = true
                    Timber.d("RoomListBanner debug: dismiss battery optimization banner in current session")
                }
                RoomListEvents.DismissNewNotificationSoundBanner -> {
                    newNotificationSoundBannerDismissed = true
                    Timber.d("RoomListBanner debug: dismiss new notification sound banner in current session")
                }
                RoomListEvents.ToggleSearchResults -> searchState.eventSink(RoomListSearchEvents.ToggleSearchVisibility)
                is RoomListEvents.ShowContextMenu -> {
                    coroutineScope.showContextMenu(event, contextMenu)
                }
                is RoomListEvents.HideContextMenu -> {
                    contextMenu.value = RoomListState.ContextMenu.Hidden
                }
                is RoomListEvents.LeaveRoom -> {
                    leaveRoomState.eventSink(LeaveRoomEvent.LeaveRoom(event.roomId, needsConfirmation = event.needsConfirmation))
                }
                is RoomListEvents.SetRoomIsFavorite -> coroutineScope.setRoomIsFavorite(event.roomId, event.isFavorite)
                is RoomListEvents.MarkAsRead -> coroutineScope.markAsRead(event.roomId)
                is RoomListEvents.MarkAsUnread -> coroutineScope.markAsUnread(event.roomId)
                is RoomListEvents.AcceptInvite -> {
                    acceptDeclineInviteState.eventSink(
                        AcceptInvite(event.roomSummary.toInviteData())
                    )
                }
                is RoomListEvents.DeclineInvite -> {
                    acceptDeclineInviteState.eventSink(
                        DeclineInvite(event.roomSummary.toInviteData(), blockUser = event.blockUser, shouldConfirm = false)
                    )
                }
                is RoomListEvents.ShowDeclineInviteMenu -> declineInviteMenu.value = RoomListState.DeclineInviteMenu.Shown(event.roomSummary)
                RoomListEvents.HideDeclineInviteMenu -> declineInviteMenu.value = RoomListState.DeclineInviteMenu.Hidden
                is RoomListEvents.ClearCacheOfRoom -> coroutineScope.clearCacheOfRoom(event.roomId)
            }
        }

        val contentState = roomListContentState(
            securityBannerDismissed,
            fullScreenIntentPermissionBannerDismissed,
            batteryOptimizationBannerDismissed,
            newNotificationSoundBannerDismissed,
            showNewNotificationSoundBanner,
        )

        LaunchedEffect(
            contentState,
            securityBannerDismissed,
            fullScreenIntentPermissionBannerDismissed,
            batteryOptimizationBannerDismissed,
            newNotificationSoundBannerDismissed,
            showNewNotificationSoundBanner,
        ) {
            when (contentState) {
                is RoomListContentState.Skeleton -> {
                    Timber.d("RoomListBanner debug: contentState=Skeleton")
                }
                is RoomListContentState.Empty -> {
                    Timber.d(
                        "RoomListBanner debug: contentState=Empty security=%s securityDismissed=%s fullScreen=%s fullScreenDismissed=%s battery=%s batteryDismissed=%s newSound=%s newSoundDismissed=%s",
                        contentState.securityBannerState,
                        securityBannerDismissed,
                        contentState.fullScreenIntentPermissionsState.shouldDisplayBanner,
                        fullScreenIntentPermissionBannerDismissed,
                        contentState.batteryOptimizationState.shouldDisplayBanner,
                        batteryOptimizationBannerDismissed,
                        contentState.showNewNotificationSoundBanner,
                        newNotificationSoundBannerDismissed,
                    )
                }
                is RoomListContentState.Rooms -> {
                    Timber.d(
                        "RoomListBanner debug: contentState=Rooms security=%s securityDismissed=%s fullScreen=%s fullScreenDismissed=%s battery=%s batteryDismissed=%s newSound=%s newSoundDismissed=%s summaries=%s",
                        contentState.securityBannerState,
                        securityBannerDismissed,
                        contentState.fullScreenIntentPermissionsState.shouldDisplayBanner,
                        fullScreenIntentPermissionBannerDismissed,
                        contentState.batteryOptimizationState.shouldDisplayBanner,
                        batteryOptimizationBannerDismissed,
                        contentState.showNewNotificationSoundBanner,
                        newNotificationSoundBannerDismissed,
                        contentState.summaries.size,
                    )
                }
            }
        }

        val canReportRoom by produceState(false) { value = client.canReportRoom() }

        return RoomListState(
            contextMenu = contextMenu.value,
            declineInviteMenu = declineInviteMenu.value,
            leaveRoomState = leaveRoomState,
            filtersState = filtersState,
            searchState = searchState,
            contentState = contentState,
            acceptDeclineInviteState = acceptDeclineInviteState,
            hideInvitesAvatars = hideInvitesAvatar,
            canReportRoom = canReportRoom,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 记忆安全横幅状态
     *
     * @param securityBannerDismissed 安全横幅是否已关闭
     * @return 安全横幅状态
     */
    @Composable
    private fun rememberSecurityBannerState(
        securityBannerDismissed: Boolean,
    ): State<SecurityBannerState> {
        val currentSecurityBannerDismissed by rememberUpdatedState(securityBannerDismissed)
        val recoveryState by encryptionService.recoveryStateStateFlow.collectAsState()
        val sessionVerifiedStatus by client.sessionVerificationService.sessionVerifiedStatus.collectAsState()
        return remember {
            derivedStateOf {
                calculateBannerState(
                    securityBannerDismissed = currentSecurityBannerDismissed,
                    recoveryState = recoveryState,
                    sessionVerifiedStatus = sessionVerifiedStatus,
                )
            }
        }
    }

    /**
     * 计算横幅状态
     *
     * @param securityBannerDismissed 安全横幅是否已关闭
     * @param recoveryState 恢复状态
     * @return 安全横幅状态
     */
    private fun calculateBannerState(
        securityBannerDismissed: Boolean,
        recoveryState: RecoveryState,
        sessionVerifiedStatus: SessionVerifiedStatus,
    ): SecurityBannerState {
        if (securityBannerDismissed) {
            return SecurityBannerState.None
        }

        if (sessionVerifiedStatus == SessionVerifiedStatus.NotVerified) {
            return SecurityBannerState.EnterRecoveryKey
        }

        when (recoveryState) {
            RecoveryState.DISABLED -> return SecurityBannerState.SetUpRecovery
            RecoveryState.INCOMPLETE -> return SecurityBannerState.RecoveryKeyConfirmation
            RecoveryState.UNKNOWN,
            RecoveryState.WAITING_FOR_SYNC,
            RecoveryState.ENABLED -> Unit
        }

        return SecurityBannerState.None
    }

    /**
     * 房间列表内容状态
     *
     * @param securityBannerDismissed 安全横幅是否已关闭
     * @param showNewNotificationSoundBanner 是否显示新通知声音横幅
     * @return 房间列表内容状态
     */
    @Composable
    private fun roomListContentState(
        securityBannerDismissed: Boolean,
        fullScreenIntentPermissionBannerDismissed: Boolean,
        batteryOptimizationBannerDismissed: Boolean,
        newNotificationSoundBannerDismissed: Boolean,
        showNewNotificationSoundBanner: Boolean,
    ): RoomListContentState {
        val roomSummaries by produceState(initialValue = AsyncData.Loading()) {
            roomListDataSource.allRooms.collect { value = AsyncData.Success(it) }
        }
        val loadingState by roomListDataSource.loadingState.collectAsState()
        val showEmpty by remember {
            derivedStateOf {
                (loadingState as? RoomList.LoadingState.Loaded)?.numberOfRooms == 0
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                loadingState == RoomList.LoadingState.NotLoaded || roomSummaries is AsyncData.Loading
            }
        }
        val seenRoomInvites by remember { seenInvitesStore.seenRoomIds() }.collectAsState(emptySet())
        val securityBannerState by rememberSecurityBannerState(securityBannerDismissed)
        val fullScreenIntentPermissionsState = fullScreenIntentPermissionsPresenter.present()
        val batteryOptimizationState = batteryOptimizationPresenter.present()
        return when {
            showEmpty -> RoomListContentState.Empty(
                securityBannerState = securityBannerState,
                fullScreenIntentPermissionsState = fullScreenIntentPermissionsState.copy(
                    shouldDisplayBanner = fullScreenIntentPermissionsState.shouldDisplayBanner && !fullScreenIntentPermissionBannerDismissed,
                ),
                batteryOptimizationState = batteryOptimizationState.copy(
                    shouldDisplayBanner = batteryOptimizationState.shouldDisplayBanner && !batteryOptimizationBannerDismissed,
                ),
                showNewNotificationSoundBanner = showNewNotificationSoundBanner && !newNotificationSoundBannerDismissed,
            )
            showSkeleton -> RoomListContentState.Skeleton(count = 16)
            else -> {
                coldStartWatcher.onRoomListVisible()

                RoomListContentState.Rooms(
                    securityBannerState = securityBannerState,
                    showNewNotificationSoundBanner = showNewNotificationSoundBanner && !newNotificationSoundBannerDismissed,
                    fullScreenIntentPermissionsState = fullScreenIntentPermissionsState.copy(
                        shouldDisplayBanner = fullScreenIntentPermissionsState.shouldDisplayBanner && !fullScreenIntentPermissionBannerDismissed,
                    ),
                    batteryOptimizationState = batteryOptimizationState.copy(
                        shouldDisplayBanner = batteryOptimizationState.shouldDisplayBanner && !batteryOptimizationBannerDismissed,
                    ),
                    summaries = roomSummaries.dataOrNull()
                        .orEmpty()
                        .prioritizeUnseenInvites(seenRoomInvites)
                        .toImmutableList(),
                    seenRoomInvites = seenRoomInvites.toImmutableSet(),
                )
            }
        }
    }

    /**
     * 显示上下文菜单
     *
     * @param event 显示上下文菜单事件
     * @param contextMenuState 上下文菜单状态
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.showContextMenu(event: RoomListEvents.ShowContextMenu, contextMenuState: MutableState<RoomListState.ContextMenu>) = launch {
        val initialState = RoomListState.ContextMenu.Shown(
            roomId = event.roomSummary.roomId,
            roomName = event.roomSummary.name,
            isDm = event.roomSummary.isDm,
            isFavorite = event.roomSummary.isFavorite,
            hasNewContent = event.roomSummary.hasNewContent,
            displayClearRoomCacheAction = appPreferencesStore.isDeveloperModeEnabledFlow().first(),
        )
        contextMenuState.value = initialState

        client.getRoom(event.roomSummary.roomId)?.use { room ->

            val isShowingContextMenuFlow = snapshotFlow { contextMenuState.value is RoomListState.ContextMenu.Shown }
                .distinctUntilChanged()

            val isFavoriteFlow = room.roomInfoFlow
                .map { it.isFavorite }
                .distinctUntilChanged()

            isFavoriteFlow
                .onEach { isFavorite ->
                    contextMenuState.value = initialState.copy(isFavorite = isFavorite)
                }
                .flatMapLatest { isShowingContextMenuFlow }
                .takeWhile { isShowingContextMenu -> isShowingContextMenu }
                .collect()
        }
    }

    /**
     * 设置房间为收藏
     *
     * @param roomId 房间 ID
     * @param isFavorite 是否收藏
     */
    private fun CoroutineScope.setRoomIsFavorite(roomId: RoomId, isFavorite: Boolean) = launch {
        client.getRoom(roomId)?.use { room ->
            room.setIsFavorite(isFavorite)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuFavouriteToggle)
                }
        }
    }

    /**
     * 标记为已读
     *
     * @param roomId 房间 ID
     */
    private fun CoroutineScope.markAsRead(roomId: RoomId) = launch {
        notificationCleaner.clearMessagesForRoom(client.sessionId, roomId)
        client.getRoom(roomId)?.use { room ->
            room.setUnreadFlag(isUnread = false)
            val receiptType = if (sessionPreferencesStore.isSendPublicReadReceiptsEnabled().first()) {
                ReceiptType.READ
            } else {
                ReceiptType.READ_PRIVATE
            }
            room.markAsRead(receiptType)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuUnreadToggle)
                }
        }
    }

    /**
     * 标记为未读
     *
     * @param roomId 房间 ID
     */
    private fun CoroutineScope.markAsUnread(roomId: RoomId) = launch {
        client.getRoom(roomId)?.use { room ->
            room.setUnreadFlag(isUnread = true)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuUnreadToggle)
                }
        }
    }

    /**
     * 清除房间缓存
     *
     * @param roomId 房间 ID
     */
    private fun CoroutineScope.clearCacheOfRoom(roomId: RoomId) = launch {
        client.getRoom(roomId)?.use { room ->
            room.clearEventCacheStorage()
        }
    }

    private var currentUpdateVisibleRangeJob: Job? = null

    /**
     * 更新可见范围
     *
     * @param range 可见范围
     */
    private fun CoroutineScope.updateVisibleRange(range: IntRange) {
        currentUpdateVisibleRangeJob?.cancel()
        currentUpdateVisibleRangeJob = launch {
            // 防抖订阅以避免订阅过多房间
            delay(SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS)

            if (range.isEmpty()) return@launch
            val currentRoomList = roomListDataSource.allRooms.first()
            // 使用扩展范围来预取下一个房间信息
            val midExtendedRangeSize = EXTENDED_RANGE_SIZE / 2
            val extendedRange = range.first until range.last + midExtendedRangeSize
            val roomIds = extendedRange.mapNotNull { index ->
                currentRoomList.getOrNull(index)?.roomId
            }
            roomListDataSource.subscribeToVisibleRooms(roomIds)
        }
    }
}

/**
 * 将“尚未查看的邀请”稳定提到列表最前面。
 *
 * 这里只调整新邀请相对其他房间的位置，不改动其余房间之间原有的排序结果，
 * 这样可以在修复邀请置顶问题的同时，尽量减少对现有列表行为的影响。
 */
private fun List<RoomListRoomSummary>.prioritizeUnseenInvites(
    seenRoomInvites: Set<RoomId>,
): List<RoomListRoomSummary> {
    val (unseenInvites, otherRooms) = partition { summary ->
        summary.displayType == RoomSummaryDisplayType.INVITE &&
            summary.roomId !in seenRoomInvites
    }
    return unseenInvites + otherRooms
}
