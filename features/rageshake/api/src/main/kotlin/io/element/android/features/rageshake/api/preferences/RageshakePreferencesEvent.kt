/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.preferences

/**
 * 摇一摇偏好设置事件密封接口
 *
 * 定义了摇一摇偏好设置功能可能产生的各种事件，用于状态管理和事件处理。
 */
sealed interface RageshakePreferencesEvent {
    /**
     * 设置灵敏度
     *
     * 更改摇一摇检测的灵敏度阈值。
     *
     * @param sensitivity 灵敏度值，范围为0到1之间
     */
    data class SetSensitivity(val sensitivity: Float) : RageshakePreferencesEvent

    /**
     * 设置是否启用
     *
     * 启用或禁用摇一摇检测功能。
     *
     * @param isEnabled 是否启用功能
     */
    data class SetIsEnabled(val isEnabled: Boolean) : RageshakePreferencesEvent
}
