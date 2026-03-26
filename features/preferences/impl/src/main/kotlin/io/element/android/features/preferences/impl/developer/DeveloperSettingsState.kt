/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer

import io.element.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.element.android.features.rageshake.api.preferences.RageshakePreferencesState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import io.element.android.libraries.matrix.api.tracing.TraceLogPack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * 开发者设置页面状态数据类
 *
 * @property features 功能标志列表
 * @property cacheSize 缓存大小
 * @property databaseSizes 数据库大小
 * @property rageshakeState 崩溃报告偏好状态
 * @property clearCacheAction 清除缓存操作状态
 * @property customElementCallBaseUrlState 自定义 Element Call 基础 URL 状态
 * @property tracingLogLevel 追踪日志级别
 * @property tracingLogPacks 追踪日志包列表
 * @property isEnterpriseBuild 是否为企业版构建
 * @property showColorPicker 是否显示颜色选择器
 * @property eventSink 事件处理函数
 */
data class DeveloperSettingsState(
    val features: ImmutableList<FeatureUiModel>,
    val cacheSize: AsyncData<String>,
    val databaseSizes: AsyncData<ImmutableMap<String, String>>,
    val rageshakeState: RageshakePreferencesState,
    val clearCacheAction: AsyncAction<Unit>,
    val customElementCallBaseUrlState: CustomElementCallBaseUrlState,
    val tracingLogLevel: AsyncData<LogLevelItem>,
    val tracingLogPacks: ImmutableList<TraceLogPack>,
    val isEnterpriseBuild: Boolean,
    val showColorPicker: Boolean,
    val eventSink: (DeveloperSettingsEvents) -> Unit
) {
    /** 是否显示加载指示器 */
    val showLoader = clearCacheAction is AsyncAction.Loading
}

/**
 * 自定义 Element Call 基础 URL 状态数据类
 *
 * @property baseUrl 基础 URL
 * @property validator URL 验证函数
 */
data class CustomElementCallBaseUrlState(
    val baseUrl: String?,
    val validator: (String?) -> Boolean,
)
