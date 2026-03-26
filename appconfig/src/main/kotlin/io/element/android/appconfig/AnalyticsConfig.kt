/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 分析配置 (Analytics Configuration)
 *
 * 此对象包含与应用分析功能相关的配置项。
 * 用于控制分析策略链接等功能的设置。
 */
object AnalyticsConfig {
    /** 分析策略页面的URL链接，用于向用户展示隐私政策和使用条款 */
    const val POLICY_LINK = BuildConfig.URL_POLICY
}
