/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.appconfig.AnalyticsConfig
import io.element.android.features.analytics.api.AnalyticsOptInEvents
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 分析功能选择 Presenter
 *
 * 负责处理分析功能选择的业务逻辑，包括：
 * - 收集用户是否启用分析的选择
 * - 将选择保存到分析服务
 * - 设置用户已回答隐私同意询问
 *
 * @property buildMeta 构建元信息
 * @property analyticsService 分析服务
 * @see AnalyticsOptInState 分析功能选择状态
 * @see AnalyticsService 分析服务接口
 */
@Inject
class AnalyticsOptInPresenter(
    private val buildMeta: BuildMeta,
    private val analyticsService: AnalyticsService,
) : Presenter<AnalyticsOptInState> {
    /**
     * 创建视图状态
     *
     * @return AnalyticsOptInState 当前分析选择状态
     */
    @Composable
    override fun present(): AnalyticsOptInState {
        val localCoroutineScope = rememberCoroutineScope()

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
            localCoroutineScope.launch {
                analyticsService.setDidAskUserConsent()
            }
        }

        return AnalyticsOptInState(
            applicationName = buildMeta.applicationName,
            hasPolicyLink = AnalyticsConfig.POLICY_LINK.isNotEmpty(),
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
