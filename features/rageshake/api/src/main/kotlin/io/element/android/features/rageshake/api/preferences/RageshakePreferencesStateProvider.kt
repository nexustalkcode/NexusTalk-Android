/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.preferences

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 摇一摇偏好设置状态预览参数提供者
 *
 * 用于在预览中提供不同状态的 RageshakePreferencesState 实例。
 */
open class RageshakePreferencesStateProvider : PreviewParameterProvider<RageshakePreferencesState> {
    /**
     * 提供预览状态序列
     *
     * 包含多种典型的偏好设置状态用于UI预览：
     * 1. 启用状态，支持功能，灵敏度0.5
     * 2. 启用状态，不支持功能，灵敏度0.5
     */
    override val values: Sequence<RageshakePreferencesState>
        get() = sequenceOf(
            aRageshakePreferencesState(isEnabled = true, isSupported = true, sensitivity = 0.5f),
            aRageshakePreferencesState(isEnabled = true, isSupported = false, sensitivity = 0.5f),
        )
}

/**
 * 创建默认摇一摇偏好设置状态的辅助函数
 *
 * 用于预览和测试，创建一个默认的 RageshakePreferencesState 实例。
 *
 * @param isFeatureEnabled 功能是否启用，默认为true
 * @param isEnabled 摇一摇功能是否启用，默认为false
 * @param isSupported 是否支持摇一摇功能，默认为true
 * @param sensitivity 灵敏度值，默认为0.3f
 * @param eventSink 事件处理函数，默认为空函数
 * @return RageshakePreferencesState 创建的状态实例
 */
fun aRageshakePreferencesState(
    isFeatureEnabled: Boolean = true,
    isEnabled: Boolean = false,
    isSupported: Boolean = true,
    sensitivity: Float = 0.3f,
    eventSink: (RageshakePreferencesEvent) -> Unit = {}
) = RageshakePreferencesState(
    isFeatureEnabled = isFeatureEnabled,
    isEnabled = isEnabled,
    isSupported = isSupported,
    sensitivity = sensitivity,
    eventSink = eventSink,
)
