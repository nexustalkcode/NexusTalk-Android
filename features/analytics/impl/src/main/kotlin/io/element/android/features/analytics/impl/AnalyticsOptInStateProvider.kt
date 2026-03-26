/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 分析功能选择状态预览参数提供者
 *
 * 提供 AnalyticsOptInState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 *
 * @see AnalyticsOptInState 分析功能选择状态
 */
open class AnalyticsOptInStateProvider : PreviewParameterProvider<AnalyticsOptInState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 AnalyticsOptInState 序列
     */
    override val values: Sequence<AnalyticsOptInState>
        get() = sequenceOf(
            aAnalyticsOptInState(),
            aAnalyticsOptInState(hasPolicyLink = false),
        )
}

/**
 * 创建示例分析选择状态
 *
 * @param hasPolicyLink 是否有隐私政策链接，默认为 true
 * @return 示例 AnalyticsOptInState 实例
 */
fun aAnalyticsOptInState(
    hasPolicyLink: Boolean = true,
) = AnalyticsOptInState(
    applicationName = "Element X",
    hasPolicyLink = hasPolicyLink,
    eventSink = {}
)
