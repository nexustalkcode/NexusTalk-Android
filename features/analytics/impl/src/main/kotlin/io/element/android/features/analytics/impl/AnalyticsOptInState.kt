/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import io.element.android.features.analytics.api.AnalyticsOptInEvents

/**
 * 分析功能选择状态数据类
 *
 * 表示分析功能选择界面的当前状态。
 *
 * @property applicationName 应用名称
 * @property hasPolicyLink 是否有隐私政策链接
 * @property eventSink 事件处理函数
 */
data class AnalyticsOptInState(
    val applicationName: String,
    val hasPolicyLink: Boolean,
    val eventSink: (AnalyticsOptInEvents) -> Unit
)
