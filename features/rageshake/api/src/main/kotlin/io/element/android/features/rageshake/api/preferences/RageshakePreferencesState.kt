/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.preferences

/**
 * 崩溃检测偏好设置状态数据类
 *
 * 表示崩溃检测功能偏好设置界面的状态。
 *
 * @property isFeatureEnabled 功能是否启用
 * @property isEnabled 是否启用崩溃检测
 * @property isSupported 是否支持崩溃检测
 * @property sensitivity 灵敏度
 * @property eventSink 事件处理函数
 */
data class RageshakePreferencesState(
    val isFeatureEnabled: Boolean,
    val isEnabled: Boolean,
    val isSupported: Boolean,
    val sensitivity: Float,
    val eventSink: (RageshakePreferencesEvent) -> Unit,
)
