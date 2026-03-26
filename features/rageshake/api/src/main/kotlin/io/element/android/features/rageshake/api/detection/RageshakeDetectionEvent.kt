/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.detection

import io.element.android.features.rageshake.api.screenshot.ImageResult

/**
 * 摇一摇检测事件密封接口
 *
 * 定义了摇一摇检测功能可能产生的各种事件，用于状态管理和事件处理。
 */
sealed interface RageshakeDetectionEvent {
    /**
     * 关闭对话框
     *
     * 用户选择不报告问题，关闭检测对话框。
     */
    data object Dismiss : RageshakeDetectionEvent

    /**
     * 禁用摇一摇功能
     *
     * 用户选择禁用摇一摇反馈功能。
     */
    data object Disable : RageshakeDetectionEvent

    /**
     * 开始检测
     *
     * 启动摇一摇检测服务，开始监听设备摇晃动作。
     */
    data object StartDetection : RageshakeDetectionEvent

    /**
     * 停止检测
     *
     * 停止摇一摇检测服务，暂停监听设备摇晃动作。
     */
    data object StopDetection : RageshakeDetectionEvent

    /**
     * 处理截图结果
     *
     * 当检测到摇晃动作后，处理截取的屏幕截图。
     *
     * @param imageResult 截图结果，包含成功或失败状态
     */
    data class ProcessScreenshot(val imageResult: ImageResult) : RageshakeDetectionEvent
}
