/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.appconfig.AnalyticsConfig
import io.element.android.features.analytics.api.AnalyticsOptInEvents
import io.element.android.features.analytics.api.preferences.AnalyticsPreferencesState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 分析偏好设置 Presenter
 *
 * 负责处理分析偏好设置的业务逻辑，包括：
 * - 监听并显示当前分析启用状态
 * - 处理用户更改分析设置的请求
 *
 * @property analyticsService 分析服务
 * @property buildMeta 构建元信息
 * @see AnalyticsPreferencesState 分析偏好设置状态
 * @see AnalyticsService 分析服务接口
 */
@Inject
class AnalyticsPreferencesPresenter(
    private val analyticsService: AnalyticsService,
    private val buildMeta: BuildMeta,
) : Presenter<AnalyticsPreferencesState> {
    /**
     * 创建视图状态
     *
     * @return AnalyticsPreferencesState 当前偏好设置状态
     */
    @Composable
    override fun present(): AnalyticsPreferencesState {
        val localCoroutineScope = rememberCoroutineScope()
        val isEnabled = analyticsService.userConsentFlow.collectAsState(initial = false)

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
        }

        return AnalyticsPreferencesState(
            applicationName = buildMeta.applicationName,
            isEnabled = isEnabled.value,
            policyUrl = AnalyticsConfig.POLICY_LINK,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 设置分析启用状态
     *
     * @param enabled 是否启用分析
     */
    private fun CoroutineScope.setIsEnabled(enabled: Boolean) = launch {
        analyticsService.setUserConsent(enabled)
    }
}
