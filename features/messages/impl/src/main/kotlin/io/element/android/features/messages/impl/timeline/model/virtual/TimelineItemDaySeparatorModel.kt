/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.virtual

/**
 * 日期分隔符虚拟项目模型数据类
 *
 * 用于在时间线中显示日期分隔符，标识不同日期的消息分组。
 *
 * @property formattedDate 格式化的日期字符串（如 "今天"、"昨天"、"2024年1月1日"）
 */
data class TimelineItemDaySeparatorModel(
    val formattedDate: String
) : TimelineItemVirtualModel {
    override val type: String = "TimelineItemDaySeparatorModel"
}
