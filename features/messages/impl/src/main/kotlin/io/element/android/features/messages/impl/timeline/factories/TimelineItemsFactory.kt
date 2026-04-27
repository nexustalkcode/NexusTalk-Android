/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.factories

import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.messages.impl.timeline.diff.TimelineItemsCacheInvalidator
import io.element.android.features.messages.impl.timeline.factories.event.TimelineItemEventFactory
import io.element.android.features.messages.impl.timeline.factories.virtual.TimelineItemVirtualFactory
import io.element.android.features.messages.impl.timeline.groups.TimelineItemGrouper
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.androidutils.diff.DiffCacheUpdater
import io.element.android.libraries.androidutils.diff.MutableListDiffCache
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import io.element.android.libraries.matrix.api.timeline.item.virtual.VirtualTimelineItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@AssistedInject
/**
 * 时间线 UI 项工厂。
 *
 * 负责把底层 [MatrixTimelineItem] 列表转换为可直接渲染的 [TimelineItem]，
 * 同时结合 diff cache、分组器和成员数据做增量更新。
 */
class TimelineItemsFactory(
    @Assisted config: TimelineItemsFactoryConfig,
    eventItemFactoryCreator: TimelineItemEventFactory.Creator,
    private val dispatchers: CoroutineDispatchers,
    private val virtualItemFactory: TimelineItemVirtualFactory,
    private val timelineItemGrouper: TimelineItemGrouper,
) {
    /**
     * 创建 [TimelineItemsFactory] 的 Assisted 工厂。
     */
    @AssistedFactory
    interface Creator {
        fun create(config: TimelineItemsFactoryConfig): TimelineItemsFactory
    }

    private val eventItemFactory = eventItemFactoryCreator.create(config)
    private val _timelineItems = MutableSharedFlow<ImmutableList<TimelineItem>>(replay = 1)
    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<TimelineItem>()
    private val diffCacheUpdater = DiffCacheUpdater<MatrixTimelineItem, TimelineItem>(
        diffCache = diffCache,
        detectMoves = false,
        cacheInvalidator = TimelineItemsCacheInvalidator()
    ) { old, new ->
        if (old is MatrixTimelineItem.Event && new is MatrixTimelineItem.Event) {
            old.uniqueId == new.uniqueId
        } else {
            false
        }
    }

    val timelineItems: Flow<ImmutableList<TimelineItem>> = _timelineItems.distinctUntilChanged()

    /**
     * 用最新的底层时间线项替换当前 UI 时间线项。
     */
    suspend fun replaceWith(
        timelineItems: List<MatrixTimelineItem>,
        roomMembers: List<RoomMember>,
    ) = withContext(dispatchers.computation) {
        val filteredTimelineItems = timelineItems.filterVisibleTimelineItems()
        lock.withLock {
            diffCacheUpdater.updateWith(filteredTimelineItems)
            buildAndEmitTimelineItemStates(filteredTimelineItems, roomMembers)
        }
    }

    /**
     * 构建并发射新的 UI 时间线项列表。
     */
    private suspend fun buildAndEmitTimelineItemStates(
        timelineItems: List<MatrixTimelineItem>,
        roomMembers: List<RoomMember>,
    ) {
        val newTimelineItemStates = ArrayList<TimelineItem>()
        for (index in diffCache.indices().reversed()) {
            val cacheItem = diffCache.get(index)
            if (cacheItem == null) {
                buildAndCacheItem(timelineItems, index, roomMembers)?.also { timelineItemState ->
                    newTimelineItemStates.add(timelineItemState)
                }
            } else {
                val updatedItem = if (cacheItem is TimelineItem.Event && roomMembers.isNotEmpty()) {
                    eventItemFactory.update(
                        timelineItem = cacheItem,
                        receivedMatrixTimelineItem = timelineItems[index] as MatrixTimelineItem.Event,
                        roomMembers = roomMembers
                    )
                } else {
                    cacheItem
                }
                newTimelineItemStates.add(updatedItem)
            }
        }
        val result = timelineItemGrouper.group(newTimelineItemStates).toImmutableList()
        this._timelineItems.emit(result)
    }

    /**
     * 构建指定索引处的 UI 时间线项并写入缓存。
     */
    private suspend fun buildAndCacheItem(
        timelineItems: List<MatrixTimelineItem>,
        index: Int,
        roomMembers: List<RoomMember>,
    ): TimelineItem? {
        val timelineItem =
            when (val currentTimelineItem = timelineItems[index]) {
                is MatrixTimelineItem.Event -> eventItemFactory.create(currentTimelineItem, index, timelineItems, roomMembers)
                is MatrixTimelineItem.Virtual -> virtualItemFactory.create(currentTimelineItem)
                MatrixTimelineItem.Other -> null
            }
        diffCache[index] = timelineItem
        return timelineItem
    }
}

/**
 * 过滤时间线中不应直接渲染的底层项。
 */
private fun List<MatrixTimelineItem>.filterVisibleTimelineItems(): List<MatrixTimelineItem> {
    return filterNot { timelineItem ->
        timelineItem is MatrixTimelineItem.Event && timelineItem.event.content is ProfileChangeContent
    }
        .removeEmptyDayDividers()
}

/**
 * 移除没有实际事件跟随的空日期分隔符。
 */
private fun List<MatrixTimelineItem>.removeEmptyDayDividers(): List<MatrixTimelineItem> {
    val result = mutableListOf<MatrixTimelineItem>()
    var pendingDayDivider: MatrixTimelineItem.Virtual? = null
    val pendingItemsAfterDivider = mutableListOf<MatrixTimelineItem>()

    fun flushPendingDayDivider(keepDivider: Boolean) {
        pendingDayDivider?.takeIf { keepDivider }?.let(result::add)
        result.addAll(pendingItemsAfterDivider)
        pendingDayDivider = null
        pendingItemsAfterDivider.clear()
    }

    for (timelineItem in this) {
        when {
            timelineItem.isDayDivider() -> {
                flushPendingDayDivider(keepDivider = false)
                pendingDayDivider = timelineItem as MatrixTimelineItem.Virtual
            }
            pendingDayDivider != null && timelineItem is MatrixTimelineItem.Event -> {
                flushPendingDayDivider(keepDivider = true)
                result.add(timelineItem)
            }
            pendingDayDivider != null -> {
                pendingItemsAfterDivider.add(timelineItem)
            }
            else -> {
                result.add(timelineItem)
            }
        }
    }

    flushPendingDayDivider(keepDivider = false)
    return result
}

/**
 * 判断当前底层时间线项是否为日期分隔符。
 */
private fun MatrixTimelineItem.isDayDivider(): Boolean {
    return this is MatrixTimelineItem.Virtual && virtual is VirtualTimelineItem.DayDivider
}
