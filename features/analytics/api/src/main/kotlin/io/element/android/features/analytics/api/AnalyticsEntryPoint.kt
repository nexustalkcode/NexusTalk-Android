/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.api

import io.element.android.libraries.architecture.SimpleFeatureEntryPoint

/**
 * 分析功能入口点接口
 *
 * 定义分析（统计）功能的入口接口，
 * 继承自 SimpleFeatureEntryPoint，提供轻量级的功能入口。
 *
 * 用于在应用中嵌入分析功能的用户界面，
 * 让用户可以选择是否启用分析数据收集。
 *
 * @see AnalyticsOptInNode 分析功能节点
 */
fun interface AnalyticsEntryPoint : SimpleFeatureEntryPoint
