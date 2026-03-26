/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer

import androidx.compose.ui.graphics.Color
import io.element.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import io.element.android.libraries.matrix.api.tracing.TraceLogPack

/**
 * 开发者设置事件密封接口
 *
 * 定义开发者设置页面中可能发生的各种用户交互事件。
 */
sealed interface DeveloperSettingsEvents {
    /** 更新功能标志启用状态 */
    data class UpdateEnabledFeature(val feature: FeatureUiModel, val isEnabled: Boolean) : DeveloperSettingsEvents
    /** 设置自定义 Element Call 基础 URL */
    data class SetCustomElementCallBaseUrl(val baseUrl: String?) : DeveloperSettingsEvents
    /** 设置追踪日志级别 */
    data class SetTracingLogLevel(val logLevel: LogLevelItem) : DeveloperSettingsEvents
    /** 切换追踪日志包 */
    data class ToggleTracingLogPack(val logPack: TraceLogPack, val enabled: Boolean) : DeveloperSettingsEvents
    /** 设置是否显示颜色选择器 */
    data class SetShowColorPicker(val show: Boolean) : DeveloperSettingsEvents
    /** 修改品牌颜色 */
    data class ChangeBrandColor(val color: Color?) : DeveloperSettingsEvents
    /** 清除缓存 */
    data object ClearCache : DeveloperSettingsEvents
    /** 清理数据库存储 */
    data object VacuumStores : DeveloperSettingsEvents
}
