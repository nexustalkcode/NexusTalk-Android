/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.api

/**
 * 分析功能选择事件密封接口
 *
 * 定义分析功能界面中可能发生的用户交互事件。
 * 使用密封接口实现类型安全的事件处理。
 *
 * @see AnalyticsOptInEvents.EnableAnalytics 启用/禁用分析事件
 */
sealed interface AnalyticsOptInEvents {
    /**
     * 启用或禁用分析数据收集事件
     *
     * @property isEnabled 是否启用分析
     */
    data class EnableAnalytics(val isEnabled: Boolean) : AnalyticsOptInEvents
}
