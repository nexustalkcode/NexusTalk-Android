/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

/**
 * 分享事件密封接口
 *
 * 定义分享界面中可能发生的用户交互事件。
 *
 * @see ShareEvents.ClearError 清除错误状态事件
 */
/**
 * Sealed interface for share events.
 *
 * Defines the possible user interaction events in the share UI.
 *
 * @see ShareEvents.ClearError Clear error state event
 */
sealed interface ShareEvents {
    /**
     * 清除错误状态事件
     *
     * 用于将分享操作状态重置为初始状态
     */
    /**
     * Event to clear error state.
     *
     * Used to reset the share operation state to initial state
     */
    data object ClearError : ShareEvents
}
