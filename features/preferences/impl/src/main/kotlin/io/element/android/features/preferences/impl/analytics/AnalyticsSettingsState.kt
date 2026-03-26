/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.analytics

import io.element.android.features.analytics.api.preferences.AnalyticsPreferencesState

/**
 * 分析设置页面状态数据类
 *
 * @property analyticsPreferencesState 分析首选项状态
 */
data class AnalyticsSettingsState(
    val analyticsPreferencesState: AnalyticsPreferencesState,
)
