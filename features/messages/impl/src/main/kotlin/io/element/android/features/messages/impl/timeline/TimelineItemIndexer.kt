/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import dev.zacsweers.metro.Inject
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.libraries.matrix.api.core.EventId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

/**
 * 时间线项目索引器
 *
 * 用于管理和查询时间线中事件的索引位置。
 * 通过维护一个事件ID到索引的映射，可以快速定位时间线中的特定事件。
 *
 * 此类线程安全，使用互斥锁保护共享状态。
 * 使用 CompletableDeferred 确保首次处理完成前其他操作等待。
 *
 * @see EventId 事件ID
 * @see TimelineItem 时间线项目
 */
@Inject
class TimelineItemIndexer {
    // 这是一个锁存器，等待第一次 process 调用完成
    private val firstProcessLatch = CompletableDeferred<Unit>()
    /** 事件ID到索引的映射表 */
    private val timelineEventsIndexes = mutableMapOf<EventId, Int>()

    /** 互斥锁，用于保护共享状态 */
    private val mutex = Mutex()

    /**
     * 检查事件ID是否已知
     *
     * @param eventId 事件ID
     * @return 是否已知
     */
    suspend fun isKnown(eventId: EventId): Boolean {
        firstProcessLatch.await()
        return mutex.withLock {
            timelineEventsIndexes.containsKey(eventId).also {
                Timber.d("$eventId isKnown = $it")
            }
        }
    }

    /**
     * 获取事件ID对应的索引
     *
     * @param eventId 事件ID
     * @return 事件索引，如果不存在返回 -1
     */
    suspend fun indexOf(eventId: EventId): Int {
        firstProcessLatch.await()
        return mutex.withLock {
            (timelineEventsIndexes[eventId] ?: -1).also {
                Timber.d("indexOf $eventId= $it")
            }
        }
    }

    /**
     * 处理时间线项目列表，构建索引
     *
     * 清空现有索引并为所有事件项目建立新的索引映射。
     * 处理单独的事件和分组事件。
     *
     * @param timelineItems 时间线项目列表
     */
    suspend fun process(timelineItems: List<TimelineItem>) = mutex.withLock {
        Timber.d("process ${timelineItems.size} items")
        timelineEventsIndexes.clear()
        timelineItems.forEachIndexed { index, timelineItem ->
            when (timelineItem) {
                is TimelineItem.Event -> {
                    processEvent(timelineItem, index)
                }
                is TimelineItem.GroupedEvents -> {
                    timelineItem.events.forEach { event ->
                        processEvent(event, index)
                    }
                }
                else -> Unit
            }
        }
        firstProcessLatch.complete(Unit)
    }

    /**
     * 处理单个事件，建立索引
     *
     * @param event 时间线事件
     * @param index 索引位置
     */
    private fun processEvent(event: TimelineItem.Event, index: Int) {
        if (event.eventId == null) return
        timelineEventsIndexes[event.eventId] = index
    }
}
