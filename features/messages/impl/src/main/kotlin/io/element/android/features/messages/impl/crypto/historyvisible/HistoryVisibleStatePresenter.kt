/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.history.RoomHistoryVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 历史可见性状态展示器
 *
 * 负责管理和呈现房间历史可见性相关的状态。
 * 当加密房间设置为"历史可见"（Shared或WorldReadable）时，此展示器会向用户显示警告提示。
 *
 * 功能说明：
 * - 检测房间的历史可见性设置
 * - 检查功能开关是否启用
 * - 追踪用户是否已确认（acknowledge）警告
 * - 当房间从私密转为公开时，重置确认状态以再次显示警告
 *
 * @property featureFlagService 特性开关服务，用于检查功能是否启用
 * @property repository 历史可见性确认状态存储仓库
 * @property room 已加入的房间实例
 *
 * @see HistoryVisibleState 历史可见性状态数据类
 * @see HistoryVisibleEvent 历史可见性事件
 * @see HistoryVisibleAcknowledgementRepository 确认状态仓库接口
 */
@Inject
class HistoryVisibleStatePresenter(
    private val featureFlagService: FeatureFlagService,
    private val repository: HistoryVisibleAcknowledgementRepository,
    private val room: JoinedRoom,
) : Presenter<HistoryVisibleState> {
    /**
     * 呈现历史可见性状态
     *
     * 此方法是 Presenter 接口的核心实现，负责：
     * 1. 收集功能开关状态（是否启用历史可见性警告）
     * 2. 收集房间信息（历史可见性设置和加密状态）
     * 3. 收集用户的确认状态
     * 4. 根据上述条件计算是否显示警告
     * 5. 处理用户事件（确认警告）
     *
     * 显示警告的条件：
     * - 功能开关已启用
     * - 房间历史可见性为 Shared 或 WorldReadable
     * - 房间已启用加密
     * - 用户尚未确认此警告
     *
     * @return [HistoryVisibleState] 包含当前状态和事件处理函数
     */
    @Composable
    override fun present(): HistoryVisibleState {
        val isFeatureEnabled by featureFlagService.isFeatureEnabledFlow(FeatureFlags.EnableKeyShareOnInvite).collectAsState(initial = false)
        val roomInfo by room.roomInfoFlow.collectAsState()
        // 隐式假设警告初始已被确认，以避免UI闪烁
        // 这是一种优化措施，防止在数据加载完成前短暂显示警告
        val acknowledged by repository.hasAcknowledged(room.roomId).collectAsState(initial = true)
        val isHistoryVisible = roomInfo.historyVisibility == RoomHistoryVisibility.Shared || roomInfo.historyVisibility == RoomHistoryVisibility.WorldReadable

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(isHistoryVisible, acknowledged) {
            if (!isHistoryVisible && acknowledged) {
                // Clear the dismissed flag, if it is set to ensure that if a room is changed public -> private -> public,
                // we show the banner again when it is set back to public.
                repository.setAcknowledged(room.roomId, false)
            }
        }

        fun handleEvent(event: HistoryVisibleEvent) {
            when (event) {
                is HistoryVisibleEvent.Acknowledge -> coroutineScope.setAcknowledged(room.roomId, true)
            }
        }

        return HistoryVisibleState(
            showAlert = isFeatureEnabled && isHistoryVisible && roomInfo.isEncrypted == true && !acknowledged,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 设置用户的确认状态
     *
     * 此扩展函数用于异步更新用户对历史可见性警告的确认状态。
     * 将确认状态持久化到本地存储，以便下次进入房间时能够记住用户的选择。
     *
     * @param roomId 房间的唯一标识
     * @param value 确认状态，true 表示用户已确认，false 表示未确认
     * @see HistoryVisibleAcknowledgementRepository.setAcknowledged 底层存储方法
     */
    private fun CoroutineScope.setAcknowledged(roomId: RoomId, value: Boolean) = launch {
        repository.setAcknowledged(roomId, value)
    }
}
