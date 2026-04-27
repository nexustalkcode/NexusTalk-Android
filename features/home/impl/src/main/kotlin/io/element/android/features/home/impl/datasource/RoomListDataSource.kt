/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.libraries.androidutils.diff.DiffCacheUpdater
import io.element.android.libraries.androidutils.diff.MutableListDiffCache
import io.element.android.libraries.androidutils.system.DateTimeObserver
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.notificationsettings.NotificationSettingsService
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import kotlin.time.Duration.Companion.seconds

@Inject
/**
 * 首页房间列表数据源。
 *
 * 负责监听全部房间摘要、订阅可见房间、在通知设置或日期变化后重建展示项，
 * 并通过 diff cache 降低首页列表更新成本。
 */
class RoomListDataSource(
    private val roomListService: RoomListService,
    private val roomListRoomSummaryFactory: RoomListRoomSummaryFactory,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val notificationSettingsService: NotificationSettingsService,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val dateTimeObserver: DateTimeObserver,
    private val analyticsService: AnalyticsService,
) {
    /** 初始化时开始监听通知设置和日期变化。 */
    init {
        observeNotificationSettings()
        observeDateTimeChanges()
    }

    private val _allRooms = MutableSharedFlow<ImmutableList<RoomListRoomSummary>>(replay = 1)

    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<RoomListRoomSummary>()
    private val diffCacheUpdater = DiffCacheUpdater<RoomSummary, RoomListRoomSummary>(diffCache = diffCache, detectMoves = true) { old, new ->
        old?.roomId == new?.roomId
    }

    val allRooms: Flow<ImmutableList<RoomListRoomSummary>> = _allRooms

    val loadingState = roomListService.allRooms.loadingState

    /** 在给定协程作用域内启动房间列表监听。 */
    fun launchIn(coroutineScope: CoroutineScope) {
        roomListService
            .allRooms
            .filteredSummaries
            .onEach { roomSummaries ->
                replaceWith(roomSummaries)
            }
            .launchIn(coroutineScope)
    }

    /** 通知底层 SDK 当前首页实际可见的房间集合。 */
    suspend fun subscribeToVisibleRooms(roomIds: List<RoomId>) {
        roomListService.subscribeToVisibleRooms(roomIds)
    }

    /** 监听通知设置变化，并在防抖后重建房间摘要。 */
    @OptIn(FlowPreview::class)
    private fun observeNotificationSettings() {
        notificationSettingsService.notificationSettingsChangeFlow
            .debounce(0.5.seconds)
            .onEach {
                roomListService.allRooms.rebuildSummaries()
            }
            .launchIn(sessionCoroutineScope)
    }

    /** 监听日期和时区变化，确保首页时间展示及时刷新。 */
    private fun observeDateTimeChanges() {
        dateTimeObserver.changes
            .onEach { event ->
                when (event) {
                    is DateTimeObserver.Event.TimeZoneChanged -> rebuildAllRoomSummaries()
                    is DateTimeObserver.Event.DateChanged -> rebuildAllRoomSummaries()
                }
            }
            .launchIn(sessionCoroutineScope)
    }

    /** 用最新房间摘要列表替换当前缓存并重新发射结果。 */
    private suspend fun replaceWith(roomSummaries: List<RoomSummary>) = withContext(coroutineDispatchers.computation) {
        lock.withLock {
            val sortedRoomSummaries = roomSummaries.sortedWith(
                compareByDescending<RoomSummary> { it.info.isFavorite }
                    .thenByDescending { it.latestEventTimestamp }
            )
            diffCacheUpdater.updateWith(sortedRoomSummaries)
            buildAndEmitAllRooms(sortedRoomSummaries)
        }
    }

    /**
     * 依据当前 diff cache 构建并发射房间列表项。
     *
     * @param roomSummaries 当前最新的房间摘要列表。
     * @param useCache 是否优先复用缓存项。
     */
    private suspend fun buildAndEmitAllRooms(roomSummaries: List<RoomSummary>, useCache: Boolean = true) {
        // Used to detect duplicates in the room list summaries - see comment below
        data class CacheResult(val index: Int, val fromCache: Boolean)
        val cachingResults = mutableMapOf<RoomId, MutableList<CacheResult>>()

        val roomListRoomSummaries = diffCache.indices().mapNotNull { index ->
            if (useCache) {
                diffCache.get(index)?.let { cachedItem ->
                    // Add the cached item to the caching results
                    val pairs = cachingResults.getOrDefault(cachedItem.roomId, mutableListOf())
                    pairs.add(CacheResult(index, fromCache = true))
                    cachingResults[cachedItem.roomId] = pairs
                    cachedItem
                } ?: run {
                    roomSummaries.getOrNull(index)?.roomId?.let {
                        // Add the non-cached item to the caching results
                        val pairs = cachingResults.getOrDefault(it, mutableListOf())
                        pairs.add(CacheResult(index, fromCache = false))
                        cachingResults[it] = pairs
                    }
                    buildAndCacheItem(roomSummaries, index)
                }
            } else {
                roomSummaries.getOrNull(index)?.roomId?.let {
                    // Add the non-cached item to the caching results
                    val pairs = cachingResults.getOrDefault(it, mutableListOf())
                    pairs.add(CacheResult(index, fromCache = false))
                    cachingResults[it] = pairs
                }
                buildAndCacheItem(roomSummaries, index)
            }
        }

        // TODO remove once https://github.com/element-hq/element-x-android/issues/5031 has been confirmed as fixed
        val duplicates = cachingResults.filter { (_, operations) -> operations.size > 1 }
        if (duplicates.isNotEmpty()) {
            analyticsService.trackError(
                IllegalStateException(
                    "Found duplicates in room summaries after a local UI update: $duplicates. " +
                    "This could be a race condition/caching issue of some kind"
                )
            )

            // Remove duplicates before emitting the new values
            _allRooms.emit(roomListRoomSummaries.distinctBy { it.roomId }.toImmutableList())
        } else {
            _allRooms.emit(roomListRoomSummaries.toImmutableList())
        }
    }

    /** 创建指定索引处的房间列表项并写回缓存。 */
    private fun buildAndCacheItem(roomSummaries: List<RoomSummary>, index: Int): RoomListRoomSummary? {
        val roomListSummary = roomSummaries.getOrNull(index)?.let { roomListRoomSummaryFactory.create(it) }
        diffCache[index] = roomListSummary
        return roomListSummary
    }

    /** 强制用最新摘要重建全部房间列表项，不复用缓存。 */
    private suspend fun rebuildAllRoomSummaries() {
        lock.withLock {
            roomListService.allRooms.filteredSummaries.replayCache.firstOrNull()?.let { roomSummaries ->
                buildAndEmitAllRooms(roomSummaries, useCache = false)
            }
        }
    }
}
