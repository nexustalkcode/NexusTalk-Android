/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.virtual

import androidx.compose.runtime.Immutable

/**
 * 时间线虚拟项目模型密封接口
 *
 * 定义时间线中非事件类型的虚拟项目。
 * 例如日期分隔符、加载指示器、阅读标记等。
 */
@Immutable
sealed interface TimelineItemVirtualModel {
    /** 虚拟项目类型 */
    val type: String
}
