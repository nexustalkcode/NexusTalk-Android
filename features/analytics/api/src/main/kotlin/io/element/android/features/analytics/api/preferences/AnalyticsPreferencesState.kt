/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.api.preferences

import io.element.android.features.analytics.api.AnalyticsOptInEvents

/**
 * 分析偏好设置状态数据类
 *
 * 表示分析偏好设置界面的当前状态。
 *
 * @property applicationName 应用名称
 * @property isEnabled 是否启用分析数据收集
 * @property policyUrl 隐私政策 URL
 * @property eventSink 事件处理函数
 */
data class AnalyticsPreferencesState(
    val applicationName: String,
    val isEnabled: Boolean,
    val policyUrl: String,
    val eventSink: (AnalyticsOptInEvents) -> Unit,
)
