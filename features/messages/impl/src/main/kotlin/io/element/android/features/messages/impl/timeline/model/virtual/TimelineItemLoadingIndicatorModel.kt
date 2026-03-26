/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.virtual

import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 加载指示器虚拟项目模型数据类
 *
 * 用于在时间线中显示加载指示器，指示正在加载更多消息。
 *
 * @property direction 翻页方向（向前或向后）
 * @property timestamp 时间戳
 */
data class TimelineItemLoadingIndicatorModel(
    val direction: Timeline.PaginationDirection,
    val timestamp: Long,
) : TimelineItemVirtualModel {
    override val type: String = "TimelineItemLoadingIndicatorModel"
}
