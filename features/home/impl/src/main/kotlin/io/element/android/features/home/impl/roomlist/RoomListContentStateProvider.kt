/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import io.element.android.libraries.push.api.battery.aBatteryOptimizationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableSet

/**
 * 房间列表内容状态提供者
 *
 * 为预览和测试提供 RoomListContentState 示例数据。
 *
 * @see RoomListContentState 房间列表内容状态
 */
open class RoomListContentStateProvider : PreviewParameterProvider<RoomListContentState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<RoomListContentState>
        get() = sequenceOf(
            aRoomsContentState(),
            aRoomsContentState(summaries = persistentListOf()),
            aSkeletonContentState(),
            anEmptyContentState(),
            anEmptyContentState(securityBannerState = SecurityBannerState.SetUpRecovery),
            aRoomsContentState(
                showNewNotificationSoundBanner = true,
            ),
        )
}

/**
 * 创建示例房间列表内容状态（房间列表）
 *
 * @param securityBannerState 安全横幅状态
 * @param showNewNotificationSoundBanner 是否显示新通知声音横幅
 * @param summaries 房间摘要列表
 * @param fullScreenIntentPermissionsState 全屏intent权限状态
 * @param batteryOptimizationState 电池优化状态
 * @param seenRoomInvites 已查看的房间邀请 ID 集合
 * @return RoomListContentState.Rooms 示例实例
 */
internal fun aRoomsContentState(
    securityBannerState: SecurityBannerState = SecurityBannerState.None,
    showNewNotificationSoundBanner: Boolean = false,
    summaries: ImmutableList<RoomListRoomSummary> = aRoomListRoomSummaryList(),
    fullScreenIntentPermissionsState: FullScreenIntentPermissionsState = aFullScreenIntentPermissionsState(),
    batteryOptimizationState: BatteryOptimizationState = aBatteryOptimizationState(),
    seenRoomInvites: Set<RoomId> = emptySet(),
) = RoomListContentState.Rooms(
    securityBannerState = securityBannerState,
    showNewNotificationSoundBanner = showNewNotificationSoundBanner,
    fullScreenIntentPermissionsState = fullScreenIntentPermissionsState,
    batteryOptimizationState = batteryOptimizationState,
    summaries = summaries,
    seenRoomInvites = seenRoomInvites.toImmutableSet(),
)

/**
 * 创建示例骨架屏状态
 *
 * @return 骨架屏内容状态
 */
internal fun aSkeletonContentState() = RoomListContentState.Skeleton(16)

/**
 * 创建示例空状态
 *
 * @param securityBannerState 安全横幅状态
 * @return 空内容状态
 */
internal fun anEmptyContentState(
    securityBannerState: SecurityBannerState = SecurityBannerState.None,
    showNewNotificationSoundBanner: Boolean = false,
    fullScreenIntentPermissionsState: FullScreenIntentPermissionsState = aFullScreenIntentPermissionsState(),
    batteryOptimizationState: BatteryOptimizationState = aBatteryOptimizationState(),
) = RoomListContentState.Empty(
    securityBannerState = securityBannerState,
    fullScreenIntentPermissionsState = fullScreenIntentPermissionsState,
    batteryOptimizationState = batteryOptimizationState,
    showNewNotificationSoundBanner = showNewNotificationSoundBanner,
)
