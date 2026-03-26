/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.ui.text.AnnotatedString
import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.eventformatter.api.PinnedMessagesBannerFormatter
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import kotlinx.coroutines.withContext

/**
 * 固定消息横幅项目工厂类
 *
 * 负责将 Matrix 时间线项目转换为固定消息横幅项目。
 * 使用依赖注入框架创建，负责格式化置顶消息的内容以在横幅中显示。
 *
 * @property coroutineDispatchers 协程调度器，用于在适当的调度器上执行耗时操作
 * @property formatter 置顶消息横幅格式化器，负责将事件格式化为可显示的文本
 *
 * @see PinnedMessagesBannerItem 固定消息横幅项目
 * @see MatrixTimelineItem Matrix时间线项目
 * @see PinnedMessagesBannerFormatter 格式化器接口
 */
@Inject
class PinnedMessagesBannerItemFactory(
    /**
     * 协程调度器
     *
     * 提供不同用途的调度器：
     * - computation: 用于计算密集型操作
     * - io: 用于IO密集型操作
     * - main: 用于UI相关操作
     */
    private val coroutineDispatchers: CoroutineDispatchers,
    /**
     * 置顶消息横幅格式化器
     *
     * 将 Matrix 事件转换为用户可读的格式化字符串，
     * 支持富文本显示。
     */
    private val formatter: PinnedMessagesBannerFormatter,
) {
    /**
     * 创建固定消息横幅项目
     *
     * 将 Matrix 时间线项目转换为可在固定消息横幅中显示的项目。
     * 如果时间线项目不是有效的事件类型，则返回 null。
     *
     * @param timelineItem Matrix时间线项目，包含房间中的事件数据
     * @return PinnedMessagesBannerItem? 转换后的横幅项目，如果无法转换则返回null
     *
     * @throws 如果格式化过程出错，可能会抛出异常
     */
    suspend fun create(timelineItem: MatrixTimelineItem): PinnedMessagesBannerItem? = withContext(coroutineDispatchers.computation) {
        when (timelineItem) {
            is MatrixTimelineItem.Event -> {
                // 检查事件ID是否存在
                val eventId = timelineItem.eventId ?: return@withContext null
                // 使用格式化器格式化事件内容
                val formatted = formatter.format(timelineItem.event)
                // 创建横幅项目，确保格式化为AnnotatedString
                PinnedMessagesBannerItem(
                    eventId = eventId,
                    formatted = if (formatted is AnnotatedString) {
                        formatted
                    } else {
                        AnnotatedString(formatted.toString())
                    },
                )
            }
            // 非事件类型的时间线项目（如日期分隔符）返回null
            else -> null
        }
    }
}
