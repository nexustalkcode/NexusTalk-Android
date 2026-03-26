/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.messages.impl.pinned.DefaultPinnedEventsTimelineProvider
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.room.BaseRoom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * 固定消息横幅 Presenter
 *
 * 负责管理固定消息横幅界面的业务逻辑和状态。
 * 处理置顶消息的加载、显示和用户交互。
 *
 * @property room Matrix房间，提供房间信息和事件数据
 * @property itemFactory 固定消息横幅项目工厂，用于创建横幅显示项目
 * @property pinnedEventsTimelineProvider 固定事件时间线提供器，提供置顶消息时间线数据
 *
 * @see Presenter 基础Presenter接口
 * @see PinnedMessagesBannerState 固定消息横幅状态
 * @see PinnedMessagesBannerEvents 固定消息横幅事件
 */
@Inject
class PinnedMessagesBannerPresenter(
    /**
     * Matrix房间
     *
     * 提供房间的基本信息，包括：
     * - 房间信息流（roomInfoFlow）：包含房间名称、 pinnedEventIds等
     * - 用于获取房间中置顶消息的数量
     */
    private val room: BaseRoom,
    /**
     * 固定消息横幅项目工厂
     *
     * 负责将时间线项目转换为横幅显示项目
     */
    private val itemFactory: PinnedMessagesBannerItemFactory,
    /**
     * 固定事件时间线提供器
     *
     * 提供专门针对置顶消息的时间线数据流，
     * 包含所有被置顶的消息事件。
     */
    private val pinnedEventsTimelineProvider: DefaultPinnedEventsTimelineProvider,
) : Presenter<PinnedMessagesBannerState> {
    /**
     * 固定消息项目列表
     *
     * 使用可变状态保存当前加载的置顶消息项目列表。
     * 使用AsyncData包装以支持加载状态的跟踪。
     */
    private val pinnedItems = mutableStateOf<AsyncData<ImmutableList<PinnedMessagesBannerItem>>>(AsyncData.Uninitialized)

    /**
     * 生成界面状态
     *
     * Composable函数，用于生成并返回当前的界面状态。
     * 监听房间信息流以获取预期的置顶消息数量，
     * 并处理用户交互事件。
     *
     * @return PinnedMessagesBannerState 固定消息横幅的当前状态
     */
    @Composable
    override fun present(): PinnedMessagesBannerState {
        // 监听房间信息流，获取预期的置顶消息数量
        val expectedPinnedMessagesCount by remember {
            room.roomInfoFlow.map { roomInfo -> roomInfo.pinnedEventIds.size }
        }.collectAsState(initial = 0)

        // 当前显示的置顶消息索引，保存到状态中以支持配置变更后保持
        var currentPinnedMessageIndex by rememberSaveable { mutableIntStateOf(-1) }

        // 订阅置顶消息项目变化
        PinnedMessagesBannerItemsEffect(
            onItemsChange = { newItems ->
                val pinnedMessageCount = newItems.dataOrNull().orEmpty().size
                // 确保索引在有效范围内
                if (currentPinnedMessageIndex >= pinnedMessageCount || currentPinnedMessageIndex < 0) {
                    currentPinnedMessageIndex = pinnedMessageCount - 1
                }
                pinnedItems.value = newItems
            },
        )

        /**
         * 处理用户事件
         *
         * @param event 固定消息横幅事件
         */
        fun handleEvent(event: PinnedMessagesBannerEvents) {
            when (event) {
                is PinnedMessagesBannerEvents.MoveToNextPinned -> {
                    // 切换到前一个置顶消息，使用模运算实现循环
                    val loadedCount = pinnedItems.value.dataOrNull().orEmpty().size
                    currentPinnedMessageIndex = (currentPinnedMessageIndex - 1).mod(loadedCount)
                }
            }
        }

        return pinnedMessagesBannerState(
            expectedPinnedMessagesCount = expectedPinnedMessagesCount,
            pinnedItems = pinnedItems.value,
            currentPinnedMessageIndex = currentPinnedMessageIndex,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 生成固定消息横幅状态
     *
     * 根据当前的加载状态和消息数据生成相应的UI状态。
     *
     * @param expectedPinnedMessagesCount 预期的置顶消息数量（从房间信息中获取）
     * @param pinnedItems 当前加载的置顶消息项目列表
     * @param currentPinnedMessageIndex 当前显示的消息索引
     * @param eventSink 事件处理函数
     * @return PinnedMessagesBannerState 对应的UI状态
     */
    @Composable
    private fun pinnedMessagesBannerState(
        expectedPinnedMessagesCount: Int,
        pinnedItems: AsyncData<ImmutableList<PinnedMessagesBannerItem>>,
        currentPinnedMessageIndex: Int,
        eventSink: (PinnedMessagesBannerEvents) -> Unit
    ): PinnedMessagesBannerState {
        return when (pinnedItems) {
            // 加载失败或未初始化状态，隐藏横幅
            is AsyncData.Failure, is AsyncData.Uninitialized -> PinnedMessagesBannerState.Hidden
            // 加载中状态
            is AsyncData.Loading -> {
                if (expectedPinnedMessagesCount == 0) {
                    // 如果预期数量为0，隐藏横幅
                    PinnedMessagesBannerState.Hidden
                } else {
                    // 显示加载状态，包含预期数量
                    PinnedMessagesBannerState.Loading(expectedPinnedMessagesCount = expectedPinnedMessagesCount)
                }
            }
            // 加载成功状态
            is AsyncData.Success -> {
                // 获取当前索引对应的消息
                val currentPinnedMessage = pinnedItems.data.getOrNull(currentPinnedMessageIndex)
                if (currentPinnedMessage == null) {
                    // 如果没有消息，隐藏横幅
                    PinnedMessagesBannerState.Hidden
                } else {
                    // 显示已加载的消息
                    PinnedMessagesBannerState.Loaded(
                        loadedPinnedMessagesCount = pinnedItems.data.size,
                        currentPinnedMessageIndex = currentPinnedMessageIndex,
                        currentPinnedMessage = currentPinnedMessage,
                        eventSink = eventSink
                    )
                }
            }
        }
    }

    /**
     * 固定消息横幅项目变化副作用
     *
     * 订阅时间线提供器的数据流，当置顶消息发生变化时回调。
     * 使用flatMapLatest处理异步时间线数据的变更。
     *
     * @param onItemsChange 项目变化回调，接收新的置顶消息列表
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    private fun PinnedMessagesBannerItemsEffect(
        onItemsChange: (AsyncData<ImmutableList<PinnedMessagesBannerItem>>) -> Unit,
    ) {
        // 使用rememberUpdatedState确保使用最新的回调引用
        val updatedOnItemsChange by rememberUpdatedState(onItemsChange)
        LaunchedEffect(Unit) {
            // 订阅时间线状态流
            pinnedEventsTimelineProvider.timelineStateFlow
                // 使用flatMapLatest处理最新的时间线状态
                .flatMapLatest { asyncTimeline ->
                    when (asyncTimeline) {
                        AsyncData.Uninitialized -> flowOf(AsyncData.Uninitialized)
                        is AsyncData.Failure -> flowOf(AsyncData.Failure(asyncTimeline.error))
                        is AsyncData.Loading -> flowOf(AsyncData.Loading())
                        is AsyncData.Success -> {
                            // 从成功的时间线中获取时间线项目流
                            asyncTimeline.data.timelineItems
                                .map { timelineItems ->
                                    // 将每个时间线项目转换为横幅项目
                                    val pinnedItems = timelineItems.mapNotNull { timelineItem ->
                                        itemFactory.create(timelineItem)
                                    }.toImmutableList()

                                    AsyncData.Success(pinnedItems)
                                }
                        }
                    }
                }
                // 每次数据变化时回调
                .onEach { newItems ->
                    updatedOnItemsChange(newItems)
                }
                // 在当前协程作用域中启动收集
                .launchIn(this)
        }
    }
}
