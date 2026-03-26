/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.zacsweers.metro.Inject
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.features.home.impl.datasource.GroupListDataSource
import io.element.android.features.home.impl.filters.RoomListFiltersState
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
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.roomlist.RoomList
import io.element.android.libraries.matrix.api.timeline.ReceiptType
import io.element.android.libraries.matrix.ui.safety.rememberHideInvitesAvatar
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.libraries.push.api.notifications.NotificationCleaner
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

/**
 * 社区列表 Presenter
 *
 * 负责处理社区列表的业务逻辑，加载社区群聊数据。
 *
 * @property client Matrix 客户端
 * @property leaveRoomPresenter 离开房间 Presenter
 * @property groupListDataSource 社区列表数据源
 * @property filtersPresenter 筛选器 Presenter
 * @property searchPresenter 搜索 Presenter
 * @property sessionPreferencesStore 会话偏好设置存储
 * @property analyticsService 分析服务
 * @property acceptDeclineInvitePresenter 接受/拒绝邀请 Presenter
 * @property notificationCleaner 通知清理器
 * @property appPreferencesStore 应用偏好设置存储
 * @property seenInvitesStore 已查看邀请存储
 */
@Inject
class GroupListPresenter(
    private val client: MatrixClient,
    private val leaveRoomPresenter: Presenter<LeaveRoomState>,
    private val groupListDataSource: GroupListDataSource,
    private val filtersPresenter: Presenter<RoomListFiltersState>,
    private val searchPresenter: Presenter<RoomListSearchState>,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val analyticsService: AnalyticsService,
    private val acceptDeclineInvitePresenter: Presenter<AcceptDeclineInviteState>,
    private val notificationCleaner: NotificationCleaner,
    private val appPreferencesStore: AppPreferencesStore,
    private val seenInvitesStore: SeenInvitesStore,
) : Presenter<GroupListState> {
    @Composable
    override fun present(): GroupListState {
        val coroutineScope = rememberCoroutineScope()
        val leaveRoomState = leaveRoomPresenter.present()
        val filtersState = filtersPresenter.present()
        val searchState = searchPresenter.present()
        val acceptDeclineInviteState = acceptDeclineInvitePresenter.present()

        LaunchedEffect(Unit) {
            groupListDataSource.launchIn(this)
        }

        var bannerDismissed by rememberSaveable { mutableStateOf(false) }

        // 隐藏邀请头像
        val hideInvitesAvatar by client.rememberHideInvitesAvatar()

        val contextMenu = remember { mutableStateOf<GroupListState.ContextMenu>(GroupListState.ContextMenu.Hidden) }
        val declineInviteMenu = remember { mutableStateOf<GroupListState.DeclineInviteMenu>(GroupListState.DeclineInviteMenu.Hidden) }

        fun handleEvent(event: GroupListEvents) {
            when (event) {
                is GroupListEvents.UpdateVisibleRange -> coroutineScope.launch {
                    // Group list 暂时不需要订阅可见房间
                }
                GroupListEvents.DismissBanner -> bannerDismissed = true
                GroupListEvents.ToggleSearchResults -> searchState.eventSink(RoomListSearchEvents.ToggleSearchVisibility)
                is GroupListEvents.ShowContextMenu -> {
                    coroutineScope.showContextMenu(event, contextMenu)
                }
                is GroupListEvents.HideContextMenu -> {
                    contextMenu.value = GroupListState.ContextMenu.Hidden
                }
                is GroupListEvents.LeaveRoom -> {
                    leaveRoomState.eventSink(LeaveRoomEvent.LeaveRoom(event.roomId, needsConfirmation = event.needsConfirmation))
                }
                is GroupListEvents.SetRoomIsFavorite -> coroutineScope.setRoomIsFavorite(event.roomId, event.isFavorite)
                is GroupListEvents.MarkAsRead -> coroutineScope.markAsRead(event.roomId)
                is GroupListEvents.MarkAsUnread -> coroutineScope.markAsUnread(event.roomId)
                is GroupListEvents.AcceptInvite -> {
                    acceptDeclineInviteState.eventSink(
                        AcceptInvite(event.roomSummary.toInviteData())
                    )
                }
                is GroupListEvents.DeclineInvite -> {
                    acceptDeclineInviteState.eventSink(
                        DeclineInvite(event.roomSummary.toInviteData(), blockUser = event.blockUser, shouldConfirm = false)
                    )
                }
                is GroupListEvents.ShowDeclineInviteMenu -> declineInviteMenu.value = GroupListState.DeclineInviteMenu.Shown(event.roomSummary)
                GroupListEvents.HideDeclineInviteMenu -> declineInviteMenu.value = GroupListState.DeclineInviteMenu.Hidden
                is GroupListEvents.ClearCacheOfRoom -> coroutineScope.clearCacheOfRoom(event.roomId)
            }
        }

        val contentState = groupListContentState()

        val canReportRoom by produceState(false) { value = client.canReportRoom() }

        return GroupListState(
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
     * 社区列表内容状态
     *
     * @return 社区列表内容状态
     */
    @Composable
    private fun groupListContentState(): GroupListContentState {
        val roomSummaries by produceState(initialValue = AsyncData.Loading()) {
            groupListDataSource.groupRooms.collect { value = AsyncData.Success(it) }
        }
        val loadingState by groupListDataSource.loadingState.collectAsState()
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

        return when {
            showEmpty -> GroupListContentState.Empty
            showSkeleton -> GroupListContentState.Skeleton(count = 16)
            else -> GroupListContentState.Rooms(
                summaries = roomSummaries.dataOrNull().orEmpty().toImmutableList(),
                seenRoomInvites = seenRoomInvites.toImmutableSet(),
            )
        }
    }

    /**
     * 显示上下文菜单
     *
     * @param event 显示上下文菜单事件
     * @param contextMenuState 上下文菜单状态
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.showContextMenu(event: GroupListEvents.ShowContextMenu, contextMenuState: MutableState<GroupListState.ContextMenu>) = launch {
        val initialState = GroupListState.ContextMenu.Shown(
            roomId = event.roomSummary.roomId,
            roomName = event.roomSummary.name,
            isDm = event.roomSummary.isDm,
            isFavorite = event.roomSummary.isFavorite,
            hasNewContent = event.roomSummary.hasNewContent,
            displayClearRoomCacheAction = appPreferencesStore.isDeveloperModeEnabledFlow().first(),
        )
        contextMenuState.value = initialState

        client.getRoom(event.roomSummary.roomId)?.use { room ->

            val isShowingContextMenuFlow = snapshotFlow { contextMenuState.value is GroupListState.ContextMenu.Shown }
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
}
