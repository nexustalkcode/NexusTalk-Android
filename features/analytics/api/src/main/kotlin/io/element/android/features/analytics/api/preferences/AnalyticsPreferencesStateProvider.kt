/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.api.preferences

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 分析偏好设置状态预览参数提供者
 *
 * 提供 AnalyticsPreferencesState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 *
 * @see AnalyticsPreferencesState 分析偏好设置状态
 */
open class AnalyticsPreferencesStateProvider : PreviewParameterProvider<AnalyticsPreferencesState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 AnalyticsPreferencesState 序列
     */
    override val values: Sequence<AnalyticsPreferencesState>
        get() = sequenceOf(
            aAnalyticsPreferencesState().copy(isEnabled = true),
            aAnalyticsPreferencesState().copy(isEnabled = true, policyUrl = ""),
        )
}

/**
 * 创建示例分析偏好设置状态
 *
 * @param applicationName 应用名称，默认为 "Element X"
 * @param isEnabled 是否启用分析，默认为 false
 * @param policyUrl 隐私政策 URL，默认为 "https://element.io"
 * @return 示例 AnalyticsPreferencesState 实例
 */
fun aAnalyticsPreferencesState(
    applicationName: String = "Element X",
    isEnabled: Boolean = false,
    policyUrl: String = "https://element.io",
) = AnalyticsPreferencesState(
    applicationName = applicationName,
    isEnabled = isEnabled,
    policyUrl = policyUrl,
    eventSink = {}
)
