/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 房间列表状态数据类
 *
 * 表示房间列表的完整状态，包含上下文菜单、邀请管理、筛选器、搜索和内容状态等信息。
 *
 * @property contextMenu 上下文菜单状态
 * @property declineInviteMenu 拒绝邀请菜单状态
 * @property leaveRoomState 离开房间状态
 * @property filtersState 房间筛选状态
 * @property searchState 搜索状态
 * @property contentState 列表内容状态
 * @property acceptDeclineInviteState 接受/拒绝邀请状态
 * @property hideInvitesAvatars 是否隐藏邀请头像
 * @property canReportRoom 是否可以报告房间
 * @property eventSink 事件处理函数
 */
data class RoomListState(
    val contextMenu: ContextMenu,
    val declineInviteMenu: DeclineInviteMenu,
    val leaveRoomState: LeaveRoomState,
    val filtersState: RoomListFiltersState,
    val searchState: RoomListSearchState,
    val contentState: RoomListContentState,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val hideInvitesAvatars: Boolean,
    val canReportRoom: Boolean,
    val eventSink: (RoomListEvents) -> Unit,
) {
    /** 是否显示筛选器（仅在房间列表模式下显示） */
    val displayFilters = contentState is RoomListContentState.Rooms

    /**
     * 上下文菜单密封接口
     */
    sealed interface ContextMenu {
        /** 隐藏状态 */
        data object Hidden : ContextMenu
        /**
         * 显示状态
         *
         * @property roomId 房间 ID
         * @property roomName 房间名称
         * @property isDm 是否为直接消息
         * @property isFavorite 是否为收藏
         * @property hasNewContent 是否有新内容
         * @property displayClearRoomCacheAction 是否显示清除缓存操作
         */
        data class Shown(
            val roomId: RoomId,
            val roomName: String?,
            val isDm: Boolean,
            val isFavorite: Boolean,
            val hasNewContent: Boolean,
            val displayClearRoomCacheAction: Boolean,
        ) : ContextMenu
    }

    /**
     * 拒绝邀请菜单密封接口
     */
    sealed interface DeclineInviteMenu {
        /** 隐藏状态 */
        data object Hidden : DeclineInviteMenu
        /**
         * 显示状态
         *
         * @property roomSummary 房间摘要
         */
        data class Shown(val roomSummary: RoomListRoomSummary) : DeclineInviteMenu
    }
}

/**
 * 安全横幅状态枚举
 *
 * 表示安全相关的提示状态。
 */
enum class SecurityBannerState {
    /** 无状态 */
    None,
    /** 设置恢复 */
    EnterRecoveryKey,
    /** 璁剧疆鎭㈠ */
    SetUpRecovery,
    /** 恢复密钥确认 */
    RecoveryKeyConfirmation,
}

/**
 * 房间列表内容状态密封接口
 *
 * 表示房间列表的不同内容状态（加载中、空状态、房间列表）。
 */
@Immutable
sealed interface RoomListContentState {
    /**
     * 骨架屏加载状态
     *
     * @property count 骨架项数量
     */
    data class Skeleton(val count: Int) : RoomListContentState

    /**
     * 空状态
     *
     * @property securityBannerState 安全横幅状态
     */
    data class Empty(
        val securityBannerState: SecurityBannerState,
        val fullScreenIntentPermissionsState: FullScreenIntentPermissionsState,
        val batteryOptimizationState: BatteryOptimizationState,
        val showNewNotificationSoundBanner: Boolean,
    ) : RoomListContentState

    /**
     * 房间列表状态
     *
     * @property securityBannerState 安全横幅状态
     * @property fullScreenIntentPermissionsState 全屏intent权限状态
     * @property batteryOptimizationState 电池优化状态
     * @property showNewNotificationSoundBanner 是否显示新通知声音横幅
     * @property summaries 房间摘要列表
     * @property seenRoomInvites 已查看的房间邀请 ID 集合
     */
    data class Rooms(
        val securityBannerState: SecurityBannerState,
        val fullScreenIntentPermissionsState: FullScreenIntentPermissionsState,
        val batteryOptimizationState: BatteryOptimizationState,
        val showNewNotificationSoundBanner: Boolean,
        val summaries: ImmutableList<RoomListRoomSummary>,
        val seenRoomInvites: ImmutableSet<RoomId>,
    ) : RoomListContentState
}
