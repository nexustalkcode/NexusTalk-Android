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
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.roomlist.RoomListFilter
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import io.element.android.libraries.matrix.api.roomlist.RoomList
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
class GroupListDataSource(
    private val roomListService: RoomListService,
    private val roomListRoomSummaryFactory: RoomListRoomSummaryFactory,
    private val coroutineDispatchers: CoroutineDispatchers,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) {
    private val _groupRooms = MutableSharedFlow<ImmutableList<RoomListRoomSummary>>(replay = 1)

    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<RoomListRoomSummary>()
    private val diffCacheUpdater = DiffCacheUpdater<RoomSummary, RoomListRoomSummary>(diffCache = diffCache, detectMoves = true) { old, new ->
        old?.roomId == new?.roomId
    }

    val groupRooms: Flow<ImmutableList<RoomListRoomSummary>> = _groupRooms

    private val groupRoomList = roomListService.createRoomList(
        pageSize = 50,
        initialFilter = RoomListFilter.Category.Group,
        source = RoomList.Source.All,
        coroutineScope = sessionCoroutineScope,
    )

    val loadingState = groupRoomList.loadingState

    fun launchIn(coroutineScope: CoroutineScope) {
        groupRoomList
            .filteredSummaries
            .onEach { roomSummaries ->
                replaceWith(roomSummaries)
            }
            .launchIn(coroutineScope)
    }

    private suspend fun replaceWith(roomSummaries: List<RoomSummary>) = withContext(coroutineDispatchers.computation) {
        lock.withLock {
            val sortedRoomSummaries = roomSummaries.sortedWith(
                compareByDescending<RoomSummary> { it.info.isFavorite }
                    .thenByDescending { it.latestEventTimestamp }
            )
            diffCacheUpdater.updateWith(sortedRoomSummaries)
            buildAndEmitGroupRooms(sortedRoomSummaries)
        }
    }

    private suspend fun buildAndEmitGroupRooms(roomSummaries: List<RoomSummary>, useCache: Boolean = true) {
        val roomListRoomSummaries = diffCache.indices().mapNotNull { index ->
            if (useCache) {
                diffCache.get(index)?.let { cachedItem ->
                    cachedItem
                } ?: run {
                    buildAndCacheItem(roomSummaries, index)
                }
            } else {
                buildAndCacheItem(roomSummaries, index)
            }
        }
        _groupRooms.emit(roomListRoomSummaries.toImmutableList())
    }

    private fun buildAndCacheItem(roomSummaries: List<RoomSummary>, index: Int): RoomListRoomSummary? {
        val roomListSummary = roomSummaries.getOrNull(index)?.let { roomListRoomSummaryFactory.create(it) }
        diffCache[index] = roomListSummary
        return roomListSummary
    }
}
