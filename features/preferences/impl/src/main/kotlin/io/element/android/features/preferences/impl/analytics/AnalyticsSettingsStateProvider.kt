/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.analytics

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.analytics.api.preferences.aAnalyticsPreferencesState

/**
 * 分析设置状态提供者
 *
 * 用于在预览模式下提供分析设置页面的示例状态数据。
 *
 * @see AnalyticsSettingsState 分析设置状态
 */
open class AnalyticsSettingsStateProvider : PreviewParameterProvider<AnalyticsSettingsState> {
    override val values: Sequence<AnalyticsSettingsState>
        get() = sequenceOf(
            aAnalyticsSettingsState(),
        )
}

/**
 * 创建示例 AnalyticsSettingsState 对象
 *
 * @return AnalyticsSettingsState 示例状态
 */
fun aAnalyticsSettingsState() = AnalyticsSettingsState(
    analyticsPreferencesState = aAnalyticsPreferencesState(),
)
