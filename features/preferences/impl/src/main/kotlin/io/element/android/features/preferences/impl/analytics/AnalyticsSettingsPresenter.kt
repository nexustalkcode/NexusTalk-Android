/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.analytics

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Inject
import io.element.android.features.analytics.api.preferences.AnalyticsPreferencesState
import io.element.android.libraries.architecture.Presenter

/**
 * 分析设置页面 Presenter
 *
 * 负责提供分析设置页面所需的数据，通过委托给分析首选项 Presenter 来获取状态。
 *
 * @property analyticsPreferencesPresenter 分析首选项 Presenter
 * @see AnalyticsSettingsState 分析设置状态
 */
@Inject
class AnalyticsSettingsPresenter(
    private val analyticsPreferencesPresenter: Presenter<AnalyticsPreferencesState>,
) : Presenter<AnalyticsSettingsState> {
    @Composable
    override fun present(): AnalyticsSettingsState {
        val analyticsPreferencesState = analyticsPreferencesPresenter.present()

        return AnalyticsSettingsState(
            analyticsPreferencesState = analyticsPreferencesState,
        )
    }
}
